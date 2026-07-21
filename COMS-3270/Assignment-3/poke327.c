#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <sys/time.h>
#include <assert.h>
#include <errno.h>
#include <ctype.h>
#include "heap.h"


// #define malloc(size) ({          
//   void *_tmp;                    
//   assert((_tmp = malloc(size))); 
//   _tmp;                          
// })


typedef struct path {
  heap_node_t *hn;
  uint8_t pos[2];
  uint8_t from[2];
  int32_t cost;
} path_t;

typedef struct trainer_path{
   heap_node_t *hn;
   uint8_t pos[2];
   int32_t cost;
} trainer_path_t;

typedef enum dim {
  dim_x,
  dim_y,
  num_dims
} dim_t;

typedef int16_t pair_t[num_dims];

#define MAP_X              80
#define MAP_Y              21
#define MIN_TREES          10
#define MIN_BOULDERS       10
#define TREE_PROB          95
#define BOULDER_PROB       95
#define WORLD_SIZE         401

#define mappair(pair) (m->map[pair[dim_y]][pair[dim_x]])
#define mapxy(x, y) (m->map[y][x])
#define heightpair(pair) (m->height[pair[dim_y]][pair[dim_x]])
#define heightxy(x, y) (m->height[y][x])

typedef enum __attribute__ ((__packed__)) terrain_type {
  ter_debug,
  ter_boulder,
  ter_tree,
  ter_path,
  ter_mart,
  ter_center,
  ter_grass,
  ter_clearing,
  ter_mountain,
  ter_forest,
  ter_water,
  ter_gate
} terrain_type_t;

int pc_x, pc_y;
terrain_type_t pc_under; 
int hiker_dist[MAP_Y][MAP_X];
int rival_dist[MAP_Y][MAP_X];


static int dir[8][2] = {
  {-1, -1}, {-1, 0}, {-1, 1},
  { 0, -1},           { 0, 1},
  { 1, -1}, { 1, 0},  { 1, 1}
};

typedef struct map {
  terrain_type_t map[MAP_Y][MAP_X];
  uint8_t height[MAP_Y][MAP_X];
  int8_t n, s, e, w;
} map_t;

typedef struct queue_node {
  int x, y;
  struct queue_node *next;
} queue_node_t;

typedef struct world {
  map_t *world[WORLD_SIZE][WORLD_SIZE];
  pair_t cur_idx;
  map_t *cur_map;
} world_t;

 static int place_trees(map_t *m);
static int place_boulders(map_t *m);
static int build_paths(map_t *m);
int get_hiker_cost(terrain_type_t x);
int get_rival_cost(terrain_type_t x);

static void enforce_border(map_t *m);

static void dijkstra_path(map_t *m, pair_t from, pair_t to);
static void dijkstra_hiker(map_t *m, int pc_x, int pc_y);
static void dijkstra_rival(map_t *m, int pc_x, int pc_y);

world_t world;

static int32_t path_cmp(const void *key, const void *with) {
  return ((path_t *) key)->cost - ((path_t *) with)->cost;
}

static int32_t trainer_path_cmp(const void *key, const void *with) {
  return ((trainer_path_t *) key)->cost -
         ((trainer_path_t *) with)->cost;
}
static int32_t edge_penalty(int8_t x, int8_t y)
{
  return (x == 1 || y == 1 || x == MAP_X - 2 || y == MAP_Y - 2) ? 2 : 1;
}

void place_pc(map_t *m)
{
  while (1) {
    int x = rand() % (MAP_X - 2) + 1;
    int y = rand() % (MAP_Y - 2) + 1;

    if (m->map[y][x] == ter_path) {
      pc_x = x;
      pc_y = y;
      pc_under = m->map[y][x];
      break;
    }
  }
}

static void dijkstra_path(map_t *m, pair_t from, pair_t to)
{
  static path_t path[MAP_Y][MAP_X];
  heap_t h;
  path_t *p;
  uint32_t x, y;

  /* Initialize all nodes */
  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      path[y][x].pos[dim_y] = y;
      path[y][x].pos[dim_x] = x;
      path[y][x].cost = INT_MAX;
      path[y][x].hn = NULL;
    }
  }

  /* Start node cost = 0 */
  path[from[dim_y]][from[dim_x]].cost = 0;

  heap_init(&h, path_cmp, NULL);

  /* Insert ALL cells into heap (including borders!) */
  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      path[y][x].hn = heap_insert(&h, &path[y][x]);
    }
  }

  while ((p = heap_remove_min(&h))) {

    p->hn = NULL;

    /* If we reached destination, reconstruct path */
    if (p->pos[dim_y] == to[dim_y] &&
        p->pos[dim_x] == to[dim_x]) {

      int cx = to[dim_x];
      int cy = to[dim_y];

      while (cx != from[dim_x] || cy != from[dim_y]) {

        if (!(cx == to[dim_x] && cy == to[dim_y])) {
          mapxy(cx, cy) = ter_path;
          heightxy(cx, cy) = 0;
        }

        int px = path[cy][cx].from[dim_x];
        int py = path[cy][cx].from[dim_y];

        cx = px;
        cy = py;
      }

      heap_delete(&h);
      return;
    }

    /* 4-directional movement */
    int dx[4] = {0, -1, 1, 0};
    int dy[4] = {-1, 0, 0, 1};

    for (int i = 0; i < 4; i++) {

      int nx = p->pos[dim_x] + dx[i];
      int ny = p->pos[dim_y] + dy[i];

      if (nx < 0 || nx >= MAP_X ||
          ny < 0 || ny >= MAP_Y)
        continue;

      path_t *neighbor = &path[ny][nx];

      if (!neighbor->hn)
        continue;

      int new_cost =
        (p->cost + heightpair(p->pos)) *
        edge_penalty(nx, ny);

      if (new_cost < neighbor->cost) {
        neighbor->cost = new_cost;
        neighbor->from[dim_x] = p->pos[dim_x];
        neighbor->from[dim_y] = p->pos[dim_y];
        heap_decrease_key_no_replace(&h, neighbor->hn);
      }
    }
  }

  heap_delete(&h);
}
static void dijkstra_hiker(map_t *m, int pc_x, int pc_y)
{
  static trainer_path_t path[MAP_Y][MAP_X];
  heap_t h;
  trainer_path_t *p;
  uint32_t x, y;

  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      path[y][x].pos[dim_y] = y;
      path[y][x].pos[dim_x] = x;
      path[y][x].cost = INT_MAX;
      path[y][x].hn = NULL;
    }
  }

  path[pc_y][pc_x].cost = 0;

  heap_init(&h, trainer_path_cmp, NULL);

  for (y = 1; y < MAP_Y - 1; y++) {
    for (x = 1; x < MAP_X - 1; x++) {
      path[y][x].hn = heap_insert(&h, &path[y][x]);
    }
  }

  while ((p = heap_remove_min(&h))) {

    p->hn = NULL;

    for (int i = 0; i < 8; i++) {

      int nx = p->pos[dim_x] + dir[i][1];
      int ny = p->pos[dim_y] + dir[i][0];

      if (nx <= 0 || nx >= MAP_X - 1 ||
          ny <= 0 || ny >= MAP_Y - 1)
        continue;

      trainer_path_t *neighbor = &path[ny][nx];

      if (!neighbor->hn)
        continue;

      int terrain_cost = get_hiker_cost(m->map[ny][nx]);

      if (terrain_cost == INT_MAX)
        continue;

      int new_cost = p->cost + terrain_cost;

      if (new_cost < neighbor->cost) {
        neighbor->cost = new_cost;
        heap_decrease_key_no_replace(&h, neighbor->hn);
      }
    }
  }
  for (y = 0; y < MAP_Y; y++) {
  for (x = 0; x < MAP_X; x++) {
    hiker_dist[y][x] = path[y][x].cost;
  }
 }

  heap_delete(&h);
}


static int gaussian[5][5] = {
  {  1,  4,  7,  4,  1 },
  {  4, 16, 26, 16,  4 },
  {  7, 26, 41, 26,  7 },
  {  4, 16, 26, 16,  4 },
  {  1,  4,  7,  4,  1 }
};

static int smooth_height(map_t *m)
{
  int32_t i, x, y;
  int32_t s, t, p, q;
  queue_node_t *head, *tail, *tmp;
  /*  FILE *out;*/
  uint8_t height[MAP_Y][MAP_X];

  memset(&height, 0, sizeof (height));

  /* Seed with some values */
  for (i = 1; i < 255; i += 20) {
    do {
      x = rand() % MAP_X;
      y = rand() % MAP_Y;
    } while (height[y][x]);
    height[y][x] = i;
    if (i == 1) {
      head = tail = malloc(sizeof (*tail));
    } else {
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
    }
    tail->next = NULL;
    tail->x = x;
    tail->y = y;
  }

  /*
  out = fopen("seeded.pgm", "w");
  fprintf(out, "P5\n%u %u\n255\n", MAP_X, MAP_Y);
  fwrite(&height, sizeof (height), 1, out);
  fclose(out);
  */
  
  /* Diffuse the vaules to fill the space */
  while (head) {
    x = head->x;
    y = head->y;
    i = height[y][x];

    if (x - 1 >= 0 && y - 1 >= 0 && !height[y - 1][x - 1]) {
      height[y - 1][x - 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x - 1;
      tail->y = y - 1;
    }
    if (x - 1 >= 0 && !height[y][x - 1]) {
      height[y][x - 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x - 1;
      tail->y = y;
    }
    if (x - 1 >= 0 && y + 1 < MAP_Y && !height[y + 1][x - 1]) {
      height[y + 1][x - 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x - 1;
      tail->y = y + 1;
    }
    if (y - 1 >= 0 && !height[y - 1][x]) {
      height[y - 1][x] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x;
      tail->y = y - 1;
    }
    if (y + 1 < MAP_Y && !height[y + 1][x]) {
      height[y + 1][x] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x;
      tail->y = y + 1;
    }
    if (x + 1 < MAP_X && y - 1 >= 0 && !height[y - 1][x + 1]) {
      height[y - 1][x + 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x + 1;
      tail->y = y - 1;
    }
    if (x + 1 < MAP_X && !height[y][x + 1]) {
      height[y][x + 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x + 1;
      tail->y = y;
    }
    if (x + 1 < MAP_X && y + 1 < MAP_Y && !height[y + 1][x + 1]) {
      height[y + 1][x + 1] = i;
      tail->next = malloc(sizeof (*tail));
      tail = tail->next;
      tail->next = NULL;
      tail->x = x + 1;
      tail->y = y + 1;
    }

    tmp = head;
    head = head->next;
    free(tmp);
  }

  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      for (s = t = p = 0; p < 5; p++) {
        for (q = 0; q < 5; q++) {
          if (y + (p - 2) >= 0 && y + (p - 2) < MAP_Y &&
              x + (q - 2) >= 0 && x + (q - 2) < MAP_X) {
            s += gaussian[p][q];
            t += height[y + (p - 2)][x + (q - 2)] * gaussian[p][q];
          }
        }
      }
      m->height[y][x] = t / s;
    }
  }
  /* Let's do it again, until it's smooth like Kenny G. */
  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      for (s = t = p = 0; p < 5; p++) {
        for (q = 0; q < 5; q++) {
          if (y + (p - 2) >= 0 && y + (p - 2) < MAP_Y &&
              x + (q - 2) >= 0 && x + (q - 2) < MAP_X) {
            s += gaussian[p][q];
            t += height[y + (p - 2)][x + (q - 2)] * gaussian[p][q];
          }
        }
      }
      m->height[y][x] = t / s;
    }
  }

  /*
  out = fopen("diffused.pgm", "w");
  fprintf(out, "P5\n%u %u\n255\n", MAP_X, MAP_Y);
  fwrite(&height, sizeof (height), 1, out);
  fclose(out);

  out = fopen("smoothed.pgm", "w");
  fprintf(out, "P5\n%u %u\n255\n", MAP_X, MAP_Y);
  fwrite(&m->height, sizeof (m->height), 1, out);
  fclose(out);
  */

  return 0;
}
int build_paths(map_t *m)
{
  pair_t from, to;

  if (m->n != -1 && m->s != -1) {
    from[dim_x] = m->n; from[dim_y] = 1;
    to[dim_x]   = m->s; to[dim_y]   = MAP_Y - 2;
    dijkstra_path(m, from, to);

    
    m->map[1][m->n] = ter_path;
    m->map[MAP_Y - 2][m->s] = ter_path;
  }

 
  if (m->w != -1 && m->e != -1) {
    from[dim_x] = 1;        from[dim_y] = m->w;
    to[dim_x]   = MAP_X - 2; to[dim_y]  = m->e;
    dijkstra_path(m, from, to);

    
    m->map[m->w][1] = ter_path;
    m->map[m->e][MAP_X - 2] = ter_path;
  }

  return 0;
}

static void find_building_location(map_t *m, pair_t p)
{
  do {
    p[dim_x] = rand() % (MAP_X - 3) + 1;
    p[dim_y] = rand() % (MAP_Y - 3) + 1;

    if ((((mapxy(p[dim_x] - 1, p[dim_y]    ) == ter_path)     &&
          (mapxy(p[dim_x] - 1, p[dim_y] + 1) == ter_path))    ||
         ((mapxy(p[dim_x] + 2, p[dim_y]    ) == ter_path)     &&
          (mapxy(p[dim_x] + 2, p[dim_y] + 1) == ter_path))    ||
         ((mapxy(p[dim_x]    , p[dim_y] - 1) == ter_path)     &&
          (mapxy(p[dim_x] + 1, p[dim_y] - 1) == ter_path))    ||
         ((mapxy(p[dim_x]    , p[dim_y] + 2) == ter_path)     &&
          (mapxy(p[dim_x] + 1, p[dim_y] + 2) == ter_path)))   &&
        (((mapxy(p[dim_x]    , p[dim_y]    ) != ter_mart)     &&
          (mapxy(p[dim_x]    , p[dim_y]    ) != ter_center)   &&
          (mapxy(p[dim_x] + 1, p[dim_y]    ) != ter_mart)     &&
          (mapxy(p[dim_x] + 1, p[dim_y]    ) != ter_center)   &&
          (mapxy(p[dim_x]    , p[dim_y] + 1) != ter_mart)     &&
          (mapxy(p[dim_x]    , p[dim_y] + 1) != ter_center)   &&
          (mapxy(p[dim_x] + 1, p[dim_y] + 1) != ter_mart)     &&
          (mapxy(p[dim_x] + 1, p[dim_y] + 1) != ter_center))) &&
        (((mapxy(p[dim_x]    , p[dim_y]    ) != ter_path)     &&
          (mapxy(p[dim_x] + 1, p[dim_y]    ) != ter_path)     &&
          (mapxy(p[dim_x]    , p[dim_y] + 1) != ter_path)     &&
          (mapxy(p[dim_x] + 1, p[dim_y] + 1) != ter_path)))) {
          break;
    }
  } while (1);
}

static int place_pokemart(map_t *m)
{
  pair_t p;

  find_building_location(m, p);

  mapxy(p[dim_x]    , p[dim_y]    ) = ter_mart;
  mapxy(p[dim_x] + 1, p[dim_y]    ) = ter_mart;
  mapxy(p[dim_x]    , p[dim_y] + 1) = ter_mart;
  mapxy(p[dim_x] + 1, p[dim_y] + 1) = ter_mart;

  return 0;
}

static int place_center(map_t *m)
{  pair_t p;

  find_building_location(m, p);

  mapxy(p[dim_x]    , p[dim_y]    ) = ter_center;
  mapxy(p[dim_x] + 1, p[dim_y]    ) = ter_center;
  mapxy(p[dim_x]    , p[dim_y] + 1) = ter_center;
  mapxy(p[dim_x] + 1, p[dim_y] + 1) = ter_center;

  return 0;
}

/* Chooses tree or boulder for border cell.  Choice is biased by dominance *
 * of neighboring cells.                                                   */
// static terrain_type_t border_type(map_t *m, int32_t x, int32_t y)
// {
//   int32_t p, q;
//   int32_t r, t;
//   int32_t miny, minx, maxy, maxx;
  
//   r = t = 0;
  
//   miny = y - 1 >= 0 ? y - 1 : 0;
//   maxy = y + 1 <= MAP_Y ? y + 1: MAP_Y;
//   minx = x - 1 >= 0 ? x - 1 : 0;
//   maxx = x + 1 <= MAP_X ? x + 1: MAP_X;

//   for (q = miny; q < maxy; q++) {
//     for (p = minx; p < maxx; p++) {
//       if (q != y || p != x) {
//         if (m->map[q][p] == ter_mountain ||
//             m->map[q][p] == ter_boulder) {
//           r++;
//         } else if (m->map[q][p] == ter_forest ||
//                    m->map[q][p] == ter_tree) {
//           t++;
//         }
//       }
//     }
//   }
  
//   if (t == r) {
//     return rand() & 1 ? ter_boulder : ter_tree;
//   } else if (t > r) {
//     if (rand() % 10) {
//       return ter_tree;
//     } else {
//       return ter_boulder;
//     }
//   } else {
//     if (rand() % 10) {
//       return ter_boulder;
//     } else {
//       return ter_tree;
//     }
//   }
// }

// static int map_terrain(map_t *m, int8_t n, int8_t s, int8_t e, int8_t w)
// {
//   int32_t i, x, y;
//   queue_node_t *head, *tail, *tmp;
//   //  FILE *out;
//   int num_grass, num_clearing, num_mountain, num_forest, num_water, num_total;
//   terrain_type_t type;
//   int added_current = 0;
  
//   num_grass = rand() % 4 + 2;
//   num_clearing = rand() % 4 + 2;
//   num_mountain = rand() % 2 + 1;
//   num_forest = rand() % 2 + 1;
//   num_water = rand() % 2 + 1;
//   num_total = num_grass + num_clearing + num_mountain + num_forest + num_water;

//   memset(&m->map, 0, sizeof (m->map));

//   /* Seed with some values */
//   for (i = 0; i < num_total; i++) {
//     do {
//       x = rand() % MAP_X;
//       y = rand() % MAP_Y;
//     } while (m->map[y][x]);
//     if (i == 0) {
//       type = ter_grass;
//     } else if (i == num_grass) {
//       type = ter_clearing;
//     } else if (i == num_grass + num_clearing) {
//       type = ter_mountain;
//     } else if (i == num_grass + num_clearing + num_mountain) {
//       type = ter_forest;
//     } else if (i == num_grass + num_clearing + num_mountain + num_forest) {
//       type = ter_water;
//     }
//     m->map[y][x] = type;
//     if (i == 0) {
//       head = tail = malloc(sizeof (*tail));
//     } else {
//       tail->next = malloc(sizeof (*tail));
//       tail = tail->next;
//     }
//     tail->next = NULL;
//     tail->x = x;
//     tail->y = y;
//   }

//   /*
//   out = fopen("seeded.pgm", "w");
//   fprintf(out, "P5\n%u %u\n255\n", MAP_X, MAP_Y);
//   fwrite(&m->map, sizeof (m->map), 1, out);
//   fclose(out);
//   */

//   /* Diffuse the vaules to fill the space */
//   while (head) {
//     x = head->x;
//     y = head->y;
//     i = m->map[y][x];
    
//     if (x - 1 >= 0 && !m->map[y][x - 1]) {
//       if ((rand() % 100) < 80) {
//         m->map[y][x - 1] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x - 1;
//         tail->y = y;
//       } else if (!added_current) {
//         added_current = 1;
//         m->map[y][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y;
//       }
//     }

//     if (y - 1 >= 0 && !m->map[y - 1][x]) {
//       if ((rand() % 100) < 20) {
//         m->map[y - 1][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y - 1;
//       } else if (!added_current) {
//         added_current = 1;
//         m->map[y][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y;
//       }
//     }

//     if (y + 1 < MAP_Y && !m->map[y + 1][x]) {
//       if ((rand() % 100) < 20) {
//         m->map[y + 1][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y + 1;
//       } else if (!added_current) {
//         added_current = 1;
//         m->map[y][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y;
//       }
//     }

//     if (x + 1 < MAP_X && !m->map[y][x + 1]) {
//       if ((rand() % 100) < 80) {
//         m->map[y][x + 1] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x + 1;
//         tail->y = y;
//       } else if (!added_current) {
//         added_current = 1;
//         m->map[y][x] = i;
//         tail->next = malloc(sizeof (*tail));
//         tail = tail->next;
//         tail->next = NULL;
//         tail->x = x;
//         tail->y = y;
//       }
//     }

//     added_current = 0;
//     tmp = head;
//     head = head->next;
//     free(tmp);
//   }

//   /*
//   out = fopen("diffused.pgm", "w");
//   fprintf(out, "P5\n%u %u\n255\n", MAP_X, MAP_Y);
//   fwrite(&m->map, sizeof (m->map), 1, out);
//   fclose(out);
//   */
  
//   /* Set entire border to boulder */
// for (x = 0; x < MAP_X; x++) {
//   mapxy(x, 0) = ter_boulder;
//   mapxy(x, MAP_Y - 1) = ter_boulder;
// }

// for (y = 0; y < MAP_Y; y++) {
//   mapxy(0, y) = ter_boulder;
//   mapxy(MAP_X - 1, y) = ter_boulder;
// }

//   m->n = n;
//   m->s = s;
//   m->e = e;
//   m->w = w;

//   if (n != -1) {
//     mapxy(n,         0        ) = ter_gate;
//     mapxy(n,         1        ) = ter_gate;
//   }
//   if (s != -1) {
//     mapxy(s,         MAP_Y - 1) = ter_gate;
//     mapxy(s,         MAP_Y - 2) = ter_gate;
//   }
//   if (w != -1) {
//     mapxy(0,         w        ) = ter_gate;
//     mapxy(1,         w        ) = ter_gate;
//   }
//   if (e != -1) {
//     mapxy(MAP_X - 1, e        ) = ter_gate;
//     mapxy(MAP_X - 2, e        ) = ter_gate;
//   }

//   return 0;
// }

static int place_boulders(map_t *m)
{
  int i;
  int x, y;

  for (i = 0; i < MIN_BOULDERS || rand() % 100 < BOULDER_PROB; i++) {
    y = rand() % (MAP_Y - 2) + 1;
    x = rand() % (MAP_X - 2) + 1;
    if (m->map[y][x] != ter_forest &&
        m->map[y][x] != ter_path   &&
        m->map[y][x] != ter_gate) {
      m->map[y][x] = ter_boulder;
    }
  }

  return 0;
}

static int place_trees(map_t *m)
{
  int i, x, y;

  for (i = 0; i < 20; i++) {
    y = rand() % (MAP_Y - 2) + 1;
    x = rand() % (MAP_X - 2) + 1;

    if (m->map[y][x] != ter_path &&
        m->map[y][x] != ter_gate) {
      m->map[y][x] = ter_tree;
    }
  }

  return 0;
}
static int map_terrain(map_t *m, int8_t n, int8_t s, int8_t e, int8_t w)
{
  int x, y;

  memset(m->map, 0, sizeof(m->map));

  /* Interior terrain: mix grass and clearings */
  for (y = 1; y < MAP_Y - 1; y++) {
    for (x = 1; x < MAP_X - 1; x++) {

      /* 60% grass, 40% clearing */
      if (rand() % 100 < 60)
        m->map[y][x] = ter_grass;
      else
        m->map[y][x] = ter_clearing;
    }
  }

  /* Borders are mountains (%) */
  for (x = 0; x < MAP_X; x++) {
    m->map[0][x] = ter_mountain;
    m->map[MAP_Y - 1][x] = ter_mountain;
  }

  for (y = 0; y < MAP_Y; y++) {
    m->map[y][0] = ter_mountain;
    m->map[y][MAP_X - 1] = ter_mountain;
  }

  m->n = n;
  m->s = s;
  m->e = e;
  m->w = w;

  /* Single gate per side */
  if (n != -1)
    m->map[0][n] = ter_gate;

  if (s != -1)
    m->map[MAP_Y - 1][s] = ter_gate;

  if (w != -1)
    m->map[w][0] = ter_gate;

  if (e != -1)
    m->map[e][MAP_X - 1] = ter_gate;

  return 0;
}

static int new_map()
{
  int d, p;
  int n, s, e, w;


  if (world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]]) {
    world.cur_map =
      world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]];
    return 0;
  }

  world.cur_map =
    world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]] =
      malloc(sizeof(*world.cur_map));

  smooth_height(world.cur_map);


  if (world.cur_idx[dim_y] == 0) {
    n = -1;
  } else if (world.world[world.cur_idx[dim_y] - 1]
                        [world.cur_idx[dim_x]]) {
    n = world.world[world.cur_idx[dim_y] - 1]
                           [world.cur_idx[dim_x]]->s;
  } else {
    n = 1 + rand() % (MAP_X - 2);
  }


  if (world.cur_idx[dim_y] == WORLD_SIZE - 1) {
    s = -1;
  } else if (world.world[world.cur_idx[dim_y] + 1]
                        [world.cur_idx[dim_x]]) {
    s = world.world[world.cur_idx[dim_y] + 1]
                           [world.cur_idx[dim_x]]->n;
  } else {
    s = 1 + rand() % (MAP_X - 2);
  }

 
  if (world.cur_idx[dim_x] == 0) {
    w = -1;
  } else if (world.world[world.cur_idx[dim_y]]
                        [world.cur_idx[dim_x] - 1]) {
    w = world.world[world.cur_idx[dim_y]]
                           [world.cur_idx[dim_x] - 1]->e;
  } else {
    w = 1 + rand() % (MAP_Y - 2);
  }

  
  if (world.cur_idx[dim_x] == WORLD_SIZE - 1) {
    e = -1;
  } else if (world.world[world.cur_idx[dim_y]]
                        [world.cur_idx[dim_x] + 1]) {
    e = world.world[world.cur_idx[dim_y]]
                           [world.cur_idx[dim_x] + 1]->w;
  } else {
    e = 1 + rand() % (MAP_Y - 2);
  }

 
  map_terrain(world.cur_map, n, s, e, w);

 
  
  place_trees(world.cur_map);
  place_boulders(world.cur_map);

  
  build_paths(world.cur_map);
  enforce_border(world.cur_map);
  
  d = abs(world.cur_idx[dim_x] - (WORLD_SIZE / 2)) +
      abs(world.cur_idx[dim_y] - (WORLD_SIZE / 2));

  p = d > 200 ? 5 : (50 - ((45 * d) / 200));

  if ((rand() % 100) < p || d == 0)
    place_pokemart(world.cur_map);

  if ((rand() % 100) < p || d == 0)
    place_center(world.cur_map);

  return 0;
}
static void print_map()
{
  int x, y;
  int default_reached = 0;

  printf("\n\n\n");

  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {

      if (x == pc_x && y == pc_y) {
        putchar('@');
        continue;
      }

      switch (world.cur_map->map[y][x]) {
      case ter_boulder:
      case ter_mountain:
        putchar('%');
        break;

      case ter_tree:
      case ter_forest:
        putchar('^');
        break;

      case ter_path:
      case ter_gate:
        putchar('#');
        break;

      case ter_mart:
        putchar('M');
        break;

      case ter_center:
        putchar('C');
        break;

      case ter_grass:
        putchar(':');
        break;

      case ter_clearing:
        putchar('.');
        break;

      case ter_water:
        putchar('~');
        break;

      default:
        default_reached = 1;
        break;
      }
    }
    putchar('\n');
  }

  if (default_reached) {
    fprintf(stderr, "Default reached in %s\n", __FUNCTION__);
  }
}


void init_world()
{
  world.cur_idx[dim_x] = world.cur_idx[dim_y] = WORLD_SIZE / 2;
  new_map();
  place_pc(world.cur_map);

  dijkstra_hiker(world.cur_map, pc_x, pc_y);
  dijkstra_rival(world.cur_map, pc_x, pc_y);
}

void delete_world()
{
  int x, y;

  for (y = 0; y < WORLD_SIZE; y++) {
    for (x = 0; x < WORLD_SIZE; x++) {
      if (world.world[y][x]) {
        free(world.world[y][x]);
        world.world[y][x] = NULL;
      }
    }
  }
}

int get_hiker_cost(terrain_type_t x){
  if(x == ter_path || x == ter_gate){
    return 10;
  }
  else if(x == ter_grass){
    return 15;
  }
  else if(x == ter_clearing){
    return 10;
  }
  else if(x == ter_mountain || x == ter_forest){
    return 15;
  }
  else if(x == ter_mart || x == ter_center){
    return 50;
  }
  else{
    return INT_MAX;
  }
}

int get_rival_cost(terrain_type_t x){
  if(x == ter_path || x == ter_gate){
    return 10;
  }
  else if(x == ter_grass){
    return 20;
  }
  else if(x == ter_clearing){
    return 10;
  }
  else if(x == ter_mart || x == ter_center){
    return 50;
  }
  else if(x == ter_mountain || x == ter_forest || x == ter_water || x == ter_boulder || x == ter_tree){
    return INT_MAX;
  }
  return INT_MAX;
}

static void dijkstra_rival(map_t *m, int pc_x, int pc_y)
{
  static trainer_path_t path[MAP_Y][MAP_X];
  heap_t h;
  trainer_path_t *p;
  uint32_t x, y;

  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      path[y][x].pos[dim_y] = y;
      path[y][x].pos[dim_x] = x;
      path[y][x].cost = INT_MAX;
      path[y][x].hn = NULL;
    }
  }

  path[pc_y][pc_x].cost = 0;

  heap_init(&h, trainer_path_cmp, NULL);

  for (y = 1; y < MAP_Y - 1; y++) {
    for (x = 1; x < MAP_X - 1; x++) {
      path[y][x].hn = heap_insert(&h, &path[y][x]);
    }
  }

  while ((p = heap_remove_min(&h))) {

    p->hn = NULL;

    for (int i = 0; i < 8; i++) {

      int nx = p->pos[dim_x] + dir[i][1];
      int ny = p->pos[dim_y] + dir[i][0];

      if (nx <= 0 || nx >= MAP_X - 1 ||
          ny <= 0 || ny >= MAP_Y - 1)
        continue;

      trainer_path_t *neighbor = &path[ny][nx];

      if (!neighbor->hn)
        continue;

      int terrain_cost = get_rival_cost(m->map[ny][nx]);

      if (terrain_cost == INT_MAX)
        continue;

      int new_cost = p->cost + terrain_cost;

      if (new_cost < neighbor->cost) {
        neighbor->cost = new_cost;
        heap_decrease_key_no_replace(&h, neighbor->hn);
      }
    }
  }
  for (y = 0; y < MAP_Y; y++) {
  for (x = 0; x < MAP_X; x++) {
    rival_dist[y][x] = path[y][x].cost;
  }
}
  heap_delete(&h);
}

void print_distance_map(int dist[MAP_Y][MAP_X])
{
  int x, y;

  printf("\n");

  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {

      if (dist[y][x] == INT_MAX) {
        printf("   ");
      } else {
        printf("%02d ", dist[y][x] % 100);
      }
    }
    printf("\n");
  }
}

int main(int argc, char *argv[])
{
  struct timeval tv;
  uint32_t seed;
  char c;
  int x, y;

  if (argc == 2) {
    errno = 0;
    seed = strtol(argv[1], NULL, 10);
    if (!isdigit(argv[1][0]) || errno) {
      fprintf(stderr, "Invalid seed value on command line.\n");
      return -1;
    }
  } else {
    gettimeofday(&tv, NULL);
    seed = (tv.tv_usec ^ (tv.tv_sec << 20)) & 0xffffffff;
  }

  printf("Using seed: %u\n", seed);
  srand(seed);

  init_world();

  do {
  print_map();

  dijkstra_hiker(world.cur_map, pc_x, pc_y);
  print_distance_map(hiker_dist);

  dijkstra_rival(world.cur_map, pc_x, pc_y);
  print_distance_map(rival_dist);  
    printf("Current position is %d%cx%d%c (%d,%d).  "
           "Enter command: ",
           abs(world.cur_idx[dim_x] - (WORLD_SIZE / 2)),
           world.cur_idx[dim_x] - (WORLD_SIZE / 2) >= 0 ? 'E' : 'W',
           abs(world.cur_idx[dim_y] - (WORLD_SIZE / 2)),
           world.cur_idx[dim_y] - (WORLD_SIZE / 2) <= 0 ? 'N' : 'S',
           world.cur_idx[dim_x] - (WORLD_SIZE / 2),
           world.cur_idx[dim_y] - (WORLD_SIZE / 2));
    if (scanf(" %c", &c) != 1) {
      /* To handle EOF */
      putchar('\n');
      break;
    }
    switch (c) {
    case 'n':
      if (world.cur_idx[dim_y]) {
        world.cur_idx[dim_y]--;
        new_map();
      }
      break;
    case 's':
      if (world.cur_idx[dim_y] < WORLD_SIZE - 1) {
        world.cur_idx[dim_y]++;
        new_map();
      }
      break;
    case 'e':
      if (world.cur_idx[dim_x] < WORLD_SIZE - 1) {
        world.cur_idx[dim_x]++;
        new_map();
      }
      break;
    case 'w':
      if (world.cur_idx[dim_x]) {
        world.cur_idx[dim_x]--;
        new_map();
      }
      break;
    case 'q':
      break;
    case 'f':
      scanf(" %d %d", &x, &y);
      if (x >= -(WORLD_SIZE / 2) && x <= WORLD_SIZE / 2 &&
          y >= -(WORLD_SIZE / 2) && y <= WORLD_SIZE / 2) {
        world.cur_idx[dim_x] = x + (WORLD_SIZE / 2);
        world.cur_idx[dim_y] = y + (WORLD_SIZE / 2);
        new_map();
      }
      break;
    case '?':
    case 'h':
      printf("Move with 'e'ast, 'w'est, 'n'orth, 's'outh or 'f'ly x y.\n"
             "Quit with 'q'.  '?' and 'h' print this help message.\n");
      break;
    default:
      fprintf(stderr, "%c: Invalid input.  Enter '?' for help.\n", c);
      break;
    }
  } while (c != 'q');

  delete_world();

  printf("But how are you going to be the very best if you quit?\n");
  
  return 0;
}

static void enforce_border(map_t *m)
{
  
  for (int x = 0; x < MAP_X; x++) {
    m->map[0][x] = ter_mountain;
    m->map[MAP_Y - 1][x] = ter_mountain;
  }
  for (int y = 0; y < MAP_Y; y++) {
    m->map[y][0] = ter_mountain;
    m->map[y][MAP_X - 1] = ter_mountain;
  }

 
  if (m->n != -1) m->map[0][m->n] = ter_gate;
  if (m->s != -1) m->map[MAP_Y - 1][m->s] = ter_gate;
  if (m->w != -1) m->map[m->w][0] = ter_gate;
  if (m->e != -1) m->map[m->e][MAP_X - 1] = ter_gate;
}

// int main(int argc, char *argv[])
// {
//   struct timeval tv;
//   uint32_t seed;

//   if (argc == 2) {
//     seed = atoi(argv[1]);
//   } else {
//     gettimeofday(&tv, NULL);
//     seed = (tv.tv_usec ^ (tv.tv_sec << 20)) & 0xffffffff;
//   }

//   printf("Using seed: %u\n", seed);
//   srand(seed);

//   init_world();

//   print_map();

//   dijkstra_hiker(world.cur_map, pc_x, pc_y);
//   print_distance_map(hiker_dist);

//   dijkstra_rival(world.cur_map, pc_x, pc_y);
//   print_distance_map(rival_dist);

//   delete_world();

//   return 0;
// }

// int main()
// {
//   uint32_t seed;
//   char input[32];

//   while (1) {

//     printf("\nEnter seed (or q to quit): ");

//     if (scanf("%31s", input) != 1)
//       break;

//     if (input[0] == 'q' || input[0] == 'Q')
//       break;

//     seed = atoi(input);

//     printf("Using seed: %u\n", seed);
//     srand(seed);

//     /* Reset world index to center */
//     world.cur_idx[dim_x] = WORLD_SIZE / 2;
//     world.cur_idx[dim_y] = WORLD_SIZE / 2;

//     new_map();
//     place_pc(world.cur_map);

//     print_map();

//     dijkstra_hiker(world.cur_map, pc_x, pc_y);
//     print_distance_map(hiker_dist);

//     dijkstra_rival(world.cur_map, pc_x, pc_y);
//     print_distance_map(rival_dist);

//     delete_world();
//   }

//   return 0;
// }