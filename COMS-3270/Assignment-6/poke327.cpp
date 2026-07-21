
#include <iostream>
#include <iomanip>
#include <vector>
#include <queue>
#include <memory>
#include <cstring>
#include <cstdlib>
#include <cstdint>
#include <climits>
#include <sys/time.h>
#include <unistd.h>
#include <errno.h>
#include <cctype>
#include <algorithm>
#include <string>
#include <sstream>
#include <ncurses.h>
extern "C" {
#include "heap.h"
}
using namespace std;

enum dim {
  dim_x,
  dim_y,
  num_dims
};

using pair_t = int16_t[num_dims];

#define MAP_X              80
#define MAP_Y              21
#define MIN_TREES          10
#define MIN_BOULDERS       10
#define TREE_PROB          95
#define BOULDER_PROB       95
#define WORLD_SIZE         401

#define MIN_TRAINERS       7
#define ADD_TRAINER_PROB   60
#define DIJKSTRA_PATH_MAX  (INT_MAX / 4)

#define PC_SYMBOL       '@'
#define HIKER_SYMBOL    'h'
#define RIVAL_SYMBOL    'r'
#define EXPLORER_SYMBOL 'e'
#define SENTRY_SYMBOL   's'
#define PACER_SYMBOL    'p'
#define SWIMMER_SYMBOL  'm'
#define WANDERER_SYMBOL 'w'

#define MOUNTAIN_SYMBOL       '%'
#define BOULDER_SYMBOL        '%'
#define TREE_SYMBOL           '^'
#define FOREST_SYMBOL         '^'
#define GATE_SYMBOL           '#'
#define PATH_SYMBOL           '#'
#define POKEMART_SYMBOL       'M'
#define POKEMON_CENTER_SYMBOL 'C'
#define TALL_GRASS_SYMBOL     ':'
#define SHORT_GRASS_SYMBOL    '.'
#define WATER_SYMBOL          '~'
#define ERROR_SYMBOL          'X'

enum terrain_type {
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
  ter_gate,
  num_terrain_types,
  ter_debug
};

enum movement_type {
  move_hiker,
  move_rival,
  move_pace,
  move_wander,
  move_sentry,
  move_explore,
  move_swim,
  move_pc,
  num_movement_types
};

enum character_type {
  char_pc,
  char_hiker,
  char_rival,
  char_swimmer,
  char_other,
  num_character_types
};

class Character {
public:
  pair_t pos {};
  char symbol;
  int next_turn;
  int seq_num;

  Character(char s) : symbol(s), next_turn(0), seq_num(0) {
    pos[dim_x] = pos[dim_y] = 0;
  }
  virtual ~Character() = default;
  virtual bool is_pc() const { return false; }
  virtual character_type ctype() const = 0;
  virtual movement_type mtype() const = 0;
};

class PC : public Character {
public:
  PC() : Character(PC_SYMBOL) {}
  bool is_pc() const override { return true; }
  character_type ctype() const override { return char_pc; }
  movement_type mtype() const override { return move_pc; }
};

class NPC : public Character {
public:
  character_type npc_ctype;
  movement_type npc_mtype;
  pair_t dir {};

  NPC(character_type ct, movement_type mt, char s) : Character(s), npc_ctype(ct), npc_mtype(mt) {
    dir[dim_x] = dir[dim_y] = 0;
  }
  character_type ctype() const override { return npc_ctype; }
  movement_type mtype() const override { return npc_mtype; }
};

struct TurnCmp {
  bool operator()(const Character *a, const Character *b) const {
    if (a->next_turn != b->next_turn) return a->next_turn > b->next_turn;
    return a->seq_num > b->seq_num;
  }
};

static pair_t all_dirs[8] = {
  { -1, -1 },
  { -1,  0 },
  { -1,  1 },
  {  0, -1 },
  {  0,  1 },
  {  1, -1 },
  {  1,  0 },
  {  1,  1 },
};

#define IM DIJKSTRA_PATH_MAX
static int32_t move_cost[num_character_types][num_terrain_types] = {
//  boulder,tree,path,mart,center,grass,clearing,mountain,forest,water,gate
  { IM, IM, 10, 10, 10, 20, 10, IM, IM, IM, 10 },
  { IM, IM, 10, 50, 50, 15, 10, 15, 15, IM, IM },
  { IM, IM, 10, 50, 50, 20, 10, IM, IM, IM, IM },
  { IM, IM,  7, IM, IM, IM, IM, IM, IM,  7, IM },
  { IM, IM, 10, 50, 50, 20, 10, IM, IM, IM, IM },
};
#undef IM

class Map {
public:
  terrain_type map[MAP_Y][MAP_X];
  uint8_t height[MAP_Y][MAP_X];
  Character *cmap[MAP_Y][MAP_X];
  priority_queue<Character *, vector<Character *>, TurnCmp> turn;
  vector<unique_ptr<NPC>> npcs;
  int8_t n, s, e, w;

  Map() : n(-1), s(-1), e(-1), w(-1) {
    memset(map, 0, sizeof(map));
    memset(height, 0, sizeof(height));
    memset(cmap, 0, sizeof(cmap));
  }
};

class World {
public:
  Map *world[WORLD_SIZE][WORLD_SIZE];
  pair_t cur_idx {};
  Map *cur_map;
  int hiker_dist[MAP_Y][MAP_X];
  int rival_dist[MAP_Y][MAP_X];
  PC pc;
  int char_seq_num;

  World() : cur_map(nullptr), char_seq_num(0) {
    memset(world, 0, sizeof(world));
    cur_idx[dim_x] = cur_idx[dim_y] = WORLD_SIZE / 2;
  }
};

static World world;

static inline terrain_type &mapxy(Map *m, int x, int y) { return m->map[y][x]; }
static inline uint8_t &heightxy(Map *m, int x, int y) { return m->height[y][x]; }

#define rand_dir(dir) {     \
  int _i = rand() & 0x7;    \
  dir[0] = all_dirs[_i][0]; \
  dir[1] = all_dirs[_i][1]; \
}

static bool is_in_bounds(int x, int y) {
  return x >= 0 && x < MAP_X && y >= 0 && y < MAP_Y;
}

static bool is_adjacent_terrain(Map *m, int x, int y, terrain_type ter) {
  for (int dy = -1; dy <= 1; dy++) {
    for (int dx = -1; dx <= 1; dx++) {
      if (!dx && !dy) continue;
      int nx = x + dx, ny = y + dy;
      if (is_in_bounds(nx, ny) && m->map[ny][nx] == ter) {
        return true;
      }
    }
  }
  return false;
}

struct PathNode {
  int x, y;
  int cost;
};

struct DijkstraCmp {
  bool operator()(const PathNode &a, const PathNode &b) const {
    return a.cost > b.cost;
  }
};

static int32_t edge_penalty(int8_t x, int8_t y) {
  return (x == 1 || y == 1 || x == MAP_X - 2 || y == MAP_Y - 2) ? 2 : 1;
}

static uint32_t can_see(Map *m, Character *voyeur, Character *exhibitionist)
{
  pair_t first, second;
  pair_t del, f;
  int16_t a, b, c, i;

  first[dim_x] = voyeur->pos[dim_x];
  first[dim_y] = voyeur->pos[dim_y];
  second[dim_x] = exhibitionist->pos[dim_x];
  second[dim_y] = exhibitionist->pos[dim_y];

  if (second[dim_x] > first[dim_x]) {
    del[dim_x] = second[dim_x] - first[dim_x];
    f[dim_x] = 1;
  } else {
    del[dim_x] = first[dim_x] - second[dim_x];
    f[dim_x] = -1;
  }

  if (second[dim_y] > first[dim_y]) {
    del[dim_y] = second[dim_y] - first[dim_y];
    f[dim_y] = 1;
  } else {
    del[dim_y] = first[dim_y] - second[dim_y];
    f[dim_y] = -1;
  }

  if (del[dim_x] > del[dim_y]) {
    a = del[dim_y] + del[dim_y];
    c = a - del[dim_x];
    b = c - del[dim_x];
    for (i = 0; i <= del[dim_x]; i++) {
      if (((m->map[first[dim_y]][first[dim_x]] != ter_water) &&
           (m->map[first[dim_y]][first[dim_x]] != ter_path)) &&
          i && (i != del[dim_x])) {
        return 0;
      }
      first[dim_x] += f[dim_x];
      if (c < 0) {
        c += a;
      } else {
        c += b;
        first[dim_y] += f[dim_y];
      }
    }
    return 1;
  } else {
    a = del[dim_x] + del[dim_x];
    c = a - del[dim_y];
    b = c - del[dim_y];
    for (i = 0; i <= del[dim_y]; i++) {
      if (((m->map[first[dim_y]][first[dim_x]] != ter_water) &&
           (m->map[first[dim_y]][first[dim_x]] != ter_path)) &&
          i && (i != del[dim_y])) {
        return 0;
      }
      first[dim_y] += f[dim_y];
      if (c < 0) {
        c += a;
      } else {
        c += b;
        first[dim_x] += f[dim_x];
      }
    }
    return 1;
  }
}

static int gaussian[5][5] = {
  {  1,  4,  7,  4,  1 },
  {  4, 16, 26, 16,  4 },
  {  7, 26, 41, 26,  7 },
  {  4, 16, 26, 16,  4 },
  {  1,  4,  7,  4,  1 }
};

static int smooth_height(Map *m)
{
  struct QueueNode { int x, y; };
  queue<QueueNode> q;
  uint8_t height[MAP_Y][MAP_X];
  memset(height, 0, sizeof(height));

  for (int i = 1; i < 255; i += 20) {
    int x, y;
    do {
      x = rand() % MAP_X;
      y = rand() % MAP_Y;
    } while (height[y][x]);
    height[y][x] = i;
    q.push({x, y});
  }

  while (!q.empty()) {
    auto cur = q.front();
    q.pop();
    int x = cur.x, y = cur.y;
    int i = height[y][x];

    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        int nx = x + dx, ny = y + dy;
        if (is_in_bounds(nx, ny) && !height[ny][nx]) {
          height[ny][nx] = i;
          q.push({nx, ny});
        }
      }
    }
  }

  for (int pass = 0; pass < 2; pass++) {
    uint8_t temp[MAP_Y][MAP_X];
    memset(temp, 0, sizeof(temp));
    for (int y = 0; y < MAP_Y; y++) {
      for (int x = 0; x < MAP_X; x++) {
        int s = 0, t = 0;
        for (int p = 0; p < 5; p++) {
          for (int qq = 0; qq < 5; qq++) {
            int yy = y + (p - 2);
            int xx = x + (qq - 2);
            if (yy >= 0 && yy < MAP_Y && xx >= 0 && xx < MAP_X) {
              s += gaussian[p][qq];
              t += height[yy][xx] * gaussian[p][qq];
            }
          }
        }
        temp[y][x] = t / s;
      }
    }
    memcpy(height, temp, sizeof(height));
  }

  memcpy(m->height, height, sizeof(m->height));
  return 0;
}

static void dijkstra_path(Map *m, pair_t from, pair_t to)
{
  static int dist[MAP_Y][MAP_X];
  static pair_t prev[MAP_Y][MAP_X];
  priority_queue<PathNode, vector<PathNode>, DijkstraCmp> pq;

  for (int y = 0; y < MAP_Y; y++) {
    for (int x = 0; x < MAP_X; x++) {
      dist[y][x] = INT_MAX;
      prev[y][x][dim_x] = prev[y][x][dim_y] = -1;
    }
  }

  dist[from[dim_y]][from[dim_x]] = 0;
  pq.push({from[dim_x], from[dim_y], 0});

  while (!pq.empty()) {
    PathNode cur = pq.top();
    pq.pop();

    if (cur.cost != dist[cur.y][cur.x]) continue;
    if (cur.x == to[dim_x] && cur.y == to[dim_y]) break;

    const int dirs[4][2] = {{0,-1},{-1,0},{1,0},{0,1}};
    for (auto &d : dirs) {
      int nx = cur.x + d[0];
      int ny = cur.y + d[1];
      if (nx <= 0 || nx >= MAP_X - 1 || ny <= 0 || ny >= MAP_Y - 1) continue;
      long long nc = ((long long) dist[cur.y][cur.x] + m->height[cur.y][cur.x]) *
                     edge_penalty(nx, ny);
      if (nc < dist[ny][nx]) {
        dist[ny][nx] = (int) nc;
        prev[ny][nx][dim_x] = cur.x;
        prev[ny][nx][dim_y] = cur.y;
        pq.push({nx, ny, dist[ny][nx]});
      }
    }
  }

  for (int x = to[dim_x], y = to[dim_y];
       (x != from[dim_x]) || (y != from[dim_y]); ) {
    int px = prev[y][x][dim_x];
    int py = prev[y][x][dim_y];
    if (px == -1 || py == -1) break;
    if (x != to[dim_x] || y != to[dim_y]) {
      mapxy(m, x, y) = ter_path;
      heightxy(m, x, y) = 0;
    }
    x = px;
    y = py;
  }
}

static int build_paths(Map *m)
{
  pair_t from, to;

  if (m->e != -1 && m->w != -1) {
    from[dim_x] = 1;         to[dim_x] = MAP_X - 2;
    from[dim_y] = m->w;      to[dim_y] = m->e;
    dijkstra_path(m, from, to);
  }

  if (m->n != -1 && m->s != -1) {
    from[dim_y] = 1;         to[dim_y] = MAP_Y - 2;
    from[dim_x] = m->n;      to[dim_x] = m->s;
    dijkstra_path(m, from, to);
  }

  if (m->e == -1) {
    if (m->s == -1) {
      from[dim_x] = 1; from[dim_y] = m->w;
      to[dim_x] = m->n; to[dim_y] = 1;
    } else {
      from[dim_x] = 1; from[dim_y] = m->w;
      to[dim_x] = m->s; to[dim_y] = MAP_Y - 2;
    }
    dijkstra_path(m, from, to);
  }

  if (m->w == -1) {
    if (m->s == -1) {
      from[dim_x] = MAP_X - 2; from[dim_y] = m->e;
      to[dim_x] = m->n;        to[dim_y] = 1;
    } else {
      from[dim_x] = MAP_X - 2; from[dim_y] = m->e;
      to[dim_x] = m->s;        to[dim_y] = MAP_Y - 2;
    }
    dijkstra_path(m, from, to);
  }

  if (m->n == -1) {
    if (m->e == -1) {
      from[dim_x] = 1;        from[dim_y] = m->w;
      to[dim_x] = m->s;       to[dim_y] = MAP_Y - 2;
    } else {
      from[dim_x] = MAP_X - 2; from[dim_y] = m->e;
      to[dim_x] = m->s;        to[dim_y] = MAP_Y - 2;
    }
    dijkstra_path(m, from, to);
  }

  if (m->s == -1) {
    if (m->e == -1) {
      from[dim_x] = 1;        from[dim_y] = m->w;
      to[dim_x] = m->n;       to[dim_y] = 1;
    } else {
      from[dim_x] = MAP_X - 2; from[dim_y] = m->e;
      to[dim_x] = m->n;        to[dim_y] = 1;
    }
    dijkstra_path(m, from, to);
  }

  return 0;
}

static void find_building_location(Map *m, pair_t p)
{
  while (true) {
    p[dim_x] = rand() % (MAP_X - 3) + 1;
    p[dim_y] = rand() % (MAP_Y - 3) + 1;

    if ((((mapxy(m, p[dim_x] - 1, p[dim_y]    ) == ter_path) &&
          (mapxy(m, p[dim_x] - 1, p[dim_y] + 1) == ter_path)) ||
         ((mapxy(m, p[dim_x] + 2, p[dim_y]    ) == ter_path) &&
          (mapxy(m, p[dim_x] + 2, p[dim_y] + 1) == ter_path)) ||
         ((mapxy(m, p[dim_x]    , p[dim_y] - 1) == ter_path) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y] - 1) == ter_path)) ||
         ((mapxy(m, p[dim_x]    , p[dim_y] + 2) == ter_path) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y] + 2) == ter_path))) &&
        (((mapxy(m, p[dim_x]    , p[dim_y]    ) != ter_mart) &&
          (mapxy(m, p[dim_x]    , p[dim_y]    ) != ter_center) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y]    ) != ter_mart) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y]    ) != ter_center) &&
          (mapxy(m, p[dim_x]    , p[dim_y] + 1) != ter_mart) &&
          (mapxy(m, p[dim_x]    , p[dim_y] + 1) != ter_center) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y] + 1) != ter_mart) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y] + 1) != ter_center))) &&
        (((mapxy(m, p[dim_x]    , p[dim_y]    ) != ter_path) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y]    ) != ter_path) &&
          (mapxy(m, p[dim_x]    , p[dim_y] + 1) != ter_path) &&
          (mapxy(m, p[dim_x] + 1, p[dim_y] + 1) != ter_path)))) {
      break;
    }
  }
}

static int place_pokemart(Map *m)
{
  pair_t p;
  find_building_location(m, p);
  mapxy(m, p[dim_x],     p[dim_y])     = ter_mart;
  mapxy(m, p[dim_x] + 1, p[dim_y])     = ter_mart;
  mapxy(m, p[dim_x],     p[dim_y] + 1) = ter_mart;
  mapxy(m, p[dim_x] + 1, p[dim_y] + 1) = ter_mart;
  return 0;
}

static int place_center(Map *m)
{
  pair_t p;
  find_building_location(m, p);
  mapxy(m, p[dim_x],     p[dim_y])     = ter_center;
  mapxy(m, p[dim_x] + 1, p[dim_y])     = ter_center;
  mapxy(m, p[dim_x],     p[dim_y] + 1) = ter_center;
  mapxy(m, p[dim_x] + 1, p[dim_y] + 1) = ter_center;
  return 0;
}

static int map_terrain(Map *m, int8_t n, int8_t s, int8_t e, int8_t w)
{
  int num_grass    = rand() % 4 + 2;
  int num_clearing = rand() % 4 + 2;
  int num_mountain = rand() % 2 + 1;
  int num_forest   = rand() % 2 + 1;
  int num_water    = rand() % 2 + 1;
  int num_total    = num_grass + num_clearing + num_mountain + num_forest + num_water;

  memset(m->map, 0, sizeof(m->map));

  struct QueueNode { int x, y; };
  queue<QueueNode> q;

  terrain_type type = ter_grass;
  for (int i = 0; i < num_total; i++) {
    int x, y;
    do {
      x = rand() % MAP_X;
      y = rand() % MAP_Y;
    } while (m->map[y][x]);

    if (i == 0) type = ter_grass;
    else if (i == num_grass) type = ter_clearing;
    else if (i == num_grass + num_clearing) type = ter_mountain;
    else if (i == num_grass + num_clearing + num_mountain) type = ter_forest;
    else if (i == num_grass + num_clearing + num_mountain + num_forest) type = ter_water;

    m->map[y][x] = type;
    q.push({x, y});
  }

  while (!q.empty()) {
    auto cur = q.front();
    q.pop();
    int x = cur.x, y = cur.y;
    terrain_type t = m->map[y][x];
    bool added_current = false;

    if (x - 1 >= 0 && !m->map[y][x - 1]) {
      if ((rand() % 100) < 80) {
        m->map[y][x - 1] = t;
        q.push({x - 1, y});
      } else if (!added_current) {
        added_current = true;
        q.push({x, y});
      }
    }
    if (y - 1 >= 0 && !m->map[y - 1][x]) {
      if ((rand() % 100) < 20) {
        m->map[y - 1][x] = t;
        q.push({x, y - 1});
      } else if (!added_current) {
        added_current = true;
        q.push({x, y});
      }
    }
    if (y + 1 < MAP_Y && !m->map[y + 1][x]) {
      if ((rand() % 100) < 20) {
        m->map[y + 1][x] = t;
        q.push({x, y + 1});
      } else if (!added_current) {
        added_current = true;
        q.push({x, y});
      }
    }
    if (x + 1 < MAP_X && !m->map[y][x + 1]) {
      if ((rand() % 100) < 80) {
        m->map[y][x + 1] = t;
        q.push({x + 1, y});
      } else if (!added_current) {
        added_current = true;
        q.push({x, y});
      }
    }
  }

  for (int y = 0; y < MAP_Y; y++) {
    for (int x = 0; x < MAP_X; x++) {
      if (y == 0 || y == MAP_Y - 1 || x == 0 || x == MAP_X - 1) {
        m->map[y][x] = ter_boulder;
      }
    }
  }

  m->n = n; m->s = s; m->e = e; m->w = w;

  if (n != -1) { mapxy(m, n, 0) = ter_gate; mapxy(m, n, 1) = ter_gate; }
  if (s != -1) { mapxy(m, s, MAP_Y - 1) = ter_gate; mapxy(m, s, MAP_Y - 2) = ter_gate; }
  if (w != -1) { mapxy(m, 0, w) = ter_gate; mapxy(m, 1, w) = ter_gate; }
  if (e != -1) { mapxy(m, MAP_X - 1, e) = ter_gate; mapxy(m, MAP_X - 2, e) = ter_gate; }

  return 0;
}

static int place_boulders(Map *m)
{
  for (int i = 0; i < MIN_BOULDERS || rand() % 100 < BOULDER_PROB; i++) {
    int y = rand() % (MAP_Y - 2) + 1;
    int x = rand() % (MAP_X - 2) + 1;
    if (m->map[y][x] != ter_forest && m->map[y][x] != ter_path && m->map[y][x] != ter_gate) {
      m->map[y][x] = ter_boulder;
    }
  }
  return 0;
}

static int place_trees(Map *m)
{
  for (int i = 0; i < MIN_TREES || rand() % 100 < TREE_PROB; i++) {
    int y = rand() % (MAP_Y - 2) + 1;
    int x = rand() % (MAP_X - 2) + 1;
    if (m->map[y][x] != ter_mountain && m->map[y][x] != ter_path &&
        m->map[y][x] != ter_water && m->map[y][x] != ter_gate) {
      m->map[y][x] = ter_tree;
    }
  }
  return 0;
}

static void pathfind(Map *m);

static void rand_pos(pair_t pos)
{
  pos[dim_x] = (rand() % (MAP_X - 2)) + 1;
  pos[dim_y] = (rand() % (MAP_Y - 2)) + 1;
}

static NPC *create_npc(character_type ct, movement_type mt, char symbol) {
  auto npc = make_unique<NPC>(ct, mt, symbol);
  NPC *raw = npc.get();
  raw->seq_num = world.char_seq_num++;
  world.cur_map->npcs.push_back(move(npc));
  return raw;
}

static void new_hiker()
{
  pair_t pos;
  do {
    rand_pos(pos);
  } while (world.hiker_dist[pos[dim_y]][pos[dim_x]] >= DIJKSTRA_PATH_MAX ||
           world.cur_map->cmap[pos[dim_y]][pos[dim_x]]);

  NPC *c = create_npc(char_hiker, move_hiker, HIKER_SYMBOL);
  c->pos[dim_y] = pos[dim_y];
  c->pos[dim_x] = pos[dim_x];
  c->next_turn = 0;
  world.cur_map->turn.push(c);
  world.cur_map->cmap[pos[dim_y]][pos[dim_x]] = c;
}

static void new_rival()
{
  pair_t pos;
  do {
    rand_pos(pos);
  } while (world.rival_dist[pos[dim_y]][pos[dim_x]] >= DIJKSTRA_PATH_MAX ||
           world.rival_dist[pos[dim_y]][pos[dim_x]] < 0 ||
           world.cur_map->cmap[pos[dim_y]][pos[dim_x]]);

  NPC *c = create_npc(char_rival, move_rival, RIVAL_SYMBOL);
  c->pos[dim_y] = pos[dim_y];
  c->pos[dim_x] = pos[dim_x];
  c->next_turn = 0;
  world.cur_map->turn.push(c);
  world.cur_map->cmap[pos[dim_y]][pos[dim_x]] = c;
}

static void new_swimmer()
{
  pair_t pos;
  do {
    rand_pos(pos);
  } while (world.cur_map->map[pos[dim_y]][pos[dim_x]] != ter_water ||
           world.cur_map->cmap[pos[dim_y]][pos[dim_x]]);

  NPC *c = create_npc(char_swimmer, move_swim, SWIMMER_SYMBOL);
  c->pos[dim_y] = pos[dim_y];
  c->pos[dim_x] = pos[dim_x];
  rand_dir(c->dir);
  c->next_turn = 0;
  world.cur_map->turn.push(c);
  world.cur_map->cmap[pos[dim_y]][pos[dim_x]] = c;
}

static void new_char_other()
{
  pair_t pos;
  do {
    rand_pos(pos);
  } while (world.rival_dist[pos[dim_y]][pos[dim_x]] >= DIJKSTRA_PATH_MAX ||
           world.rival_dist[pos[dim_y]][pos[dim_x]] < 0 ||
           world.cur_map->cmap[pos[dim_y]][pos[dim_x]]);

  character_type ct = char_other;
  movement_type mt;
  char symbol;
  switch (rand() % 4) {
    case 0: mt = move_pace;    symbol = PACER_SYMBOL;    break;
    case 1: mt = move_wander;  symbol = WANDERER_SYMBOL; break;
    case 2: mt = move_sentry;  symbol = SENTRY_SYMBOL;   break;
    default: mt = move_explore; symbol = EXPLORER_SYMBOL; break;
  }

  NPC *c = create_npc(ct, mt, symbol);
  c->pos[dim_y] = pos[dim_y];
  c->pos[dim_x] = pos[dim_x];
  rand_dir(c->dir);
  c->next_turn = 0;
  world.cur_map->turn.push(c);
  world.cur_map->cmap[pos[dim_y]][pos[dim_x]] = c;
}

static void place_characters()
{
  int num_trainers = 3;
  new_hiker();
  new_rival();
  new_swimmer();
  do {
    switch (rand() % 10) {
      case 0: new_hiker(); break;
      case 1: new_rival(); break;
      case 2: new_swimmer(); break;
      default: new_char_other(); break;
    }
  } while (++num_trainers < MIN_TRAINERS || ((rand() % 100) < ADD_TRAINER_PROB));
}

static void init_pc_on_map(Map *m, int x = -1, int y = -1)
{
  if (x == -1 || y == -1) {
    do {
      x = rand() % (MAP_X - 2) + 1;
      y = rand() % (MAP_Y - 2) + 1;
    } while (m->map[y][x] != ter_path);
  }
  world.pc.pos[dim_x] = x;
  world.pc.pos[dim_y] = y;
  world.pc.seq_num = world.char_seq_num++;
  world.pc.next_turn = 0;
  m->cmap[y][x] = &world.pc;
  m->turn.push(&world.pc);
}

static void place_pc_on_existing_map(Map *m, int x, int y)
{
  world.pc.pos[dim_x] = x;
  world.pc.pos[dim_y] = y;
  m->cmap[y][x] = &world.pc;
}

static int find_first_path(Map *m, int &outx, int &outy)
{
  for (int y = 1; y < MAP_Y - 1; y++) {
    for (int x = 1; x < MAP_X - 1; x++) {
      if (m->map[y][x] == ter_path && !m->cmap[y][x]) {
        outx = x;
        outy = y;
        return 1;
      }
    }
  }
  return 0;
}

static int new_map(bool place_pc_here)
{
  if (world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]]) {
    world.cur_map = world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]];
    return 0;
  }

  world.cur_map = world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x]] = new Map;

  smooth_height(world.cur_map);

  int e, w, n, s;
  if (!world.cur_idx[dim_y]) n = -1;
  else if (world.world[world.cur_idx[dim_y] - 1][world.cur_idx[dim_x]]) n = world.world[world.cur_idx[dim_y] - 1][world.cur_idx[dim_x]]->s;
  else n = 1 + rand() % (MAP_X - 2);

  if (world.cur_idx[dim_y] == WORLD_SIZE - 1) s = -1;
  else if (world.world[world.cur_idx[dim_y] + 1][world.cur_idx[dim_x]]) s = world.world[world.cur_idx[dim_y] + 1][world.cur_idx[dim_x]]->n;
  else s = 1 + rand() % (MAP_X - 2);

  if (!world.cur_idx[dim_x]) w = -1;
  else if (world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x] - 1]) w = world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x] - 1]->e;
  else w = 1 + rand() % (MAP_Y - 2);

  if (world.cur_idx[dim_x] == WORLD_SIZE - 1) e = -1;
  else if (world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x] + 1]) e = world.world[world.cur_idx[dim_y]][world.cur_idx[dim_x] + 1]->w;
  else e = 1 + rand() % (MAP_Y - 2);

  map_terrain(world.cur_map, n, s, e, w);
  place_boulders(world.cur_map);
  place_trees(world.cur_map);
  build_paths(world.cur_map);

  int d = abs(world.cur_idx[dim_x] - (WORLD_SIZE / 2)) +
          abs(world.cur_idx[dim_y] - (WORLD_SIZE / 2));
  int p = d > 200 ? 5 : (50 - ((45 * d) / 200));
  if ((rand() % 100) < p || !d) place_pokemart(world.cur_map);
  if ((rand() % 100) < p || !d) place_center(world.cur_map);

  memset(world.cur_map->cmap, 0, sizeof(world.cur_map->cmap));

  if (place_pc_here) {
    init_pc_on_map(world.cur_map);
  } else {
    int fakex = world.cur_map->w != -1 ? 1 : (world.cur_map->e != -1 ? MAP_X - 2 :
               (world.cur_map->n != -1 ? world.cur_map->n : world.cur_map->s));
    int fakey = world.cur_map->w != -1 ? world.cur_map->w :
               (world.cur_map->e != -1 ? world.cur_map->e :
               (world.cur_map->n != -1 ? 1 : MAP_Y - 2));
    world.pc.pos[dim_x] = fakex;
    world.pc.pos[dim_y] = fakey;
    pathfind(world.cur_map);
    world.cur_map->cmap[fakey][fakex] = nullptr;
  }

  pathfind(world.cur_map);
  place_characters();
  return 0;
}

static void init_world()
{
  world.cur_idx[dim_x] = world.cur_idx[dim_y] = WORLD_SIZE / 2;
  world.char_seq_num = 0;
  new_map(true);
}

#define ter_cost(x, y, c) move_cost[c][m->map[y][x]]

static void pathfind(Map *m)
{
  for (int y = 0; y < MAP_Y; y++) {
    for (int x = 0; x < MAP_X; x++) {
      world.hiker_dist[y][x] = world.rival_dist[y][x] = DIJKSTRA_PATH_MAX;
    }
  }

  struct Node { int x, y, cost; };
  struct Cmp { bool operator()(const Node &a, const Node &b) const { return a.cost > b.cost; } };

  priority_queue<Node, vector<Node>, Cmp> pq;

  world.hiker_dist[world.pc.pos[dim_y]][world.pc.pos[dim_x]] = 0;
  pq.push({world.pc.pos[dim_x], world.pc.pos[dim_y], 0});

  while (!pq.empty()) {
    Node cur = pq.top(); pq.pop();
    if (cur.cost != world.hiker_dist[cur.y][cur.x]) continue;

    for (int i = 0; i < 8; i++) {
      int nx = cur.x + all_dirs[i][dim_x];
      int ny = cur.y + all_dirs[i][dim_y];
      if (nx <= 0 || nx >= MAP_X - 1 || ny <= 0 || ny >= MAP_Y - 1) continue;
      int step = move_cost[char_hiker][m->map[cur.y][cur.x]];
      if (step >= DIJKSTRA_PATH_MAX) continue;
      if (world.hiker_dist[ny][nx] > world.hiker_dist[cur.y][cur.x] + step) {
        world.hiker_dist[ny][nx] = world.hiker_dist[cur.y][cur.x] + step;
        pq.push({nx, ny, world.hiker_dist[ny][nx]});
      }
    }
  }

  while (!pq.empty()) pq.pop();
  world.rival_dist[world.pc.pos[dim_y]][world.pc.pos[dim_x]] = 0;
  pq.push({world.pc.pos[dim_x], world.pc.pos[dim_y], 0});

  while (!pq.empty()) {
    Node cur = pq.top(); pq.pop();
    if (cur.cost != world.rival_dist[cur.y][cur.x]) continue;

    for (int i = 0; i < 8; i++) {
      int nx = cur.x + all_dirs[i][dim_x];
      int ny = cur.y + all_dirs[i][dim_y];
      if (nx <= 0 || nx >= MAP_X - 1 || ny <= 0 || ny >= MAP_Y - 1) continue;
      int step = move_cost[char_rival][m->map[cur.y][cur.x]];
      if (step >= DIJKSTRA_PATH_MAX) continue;
      if (world.rival_dist[ny][nx] > world.rival_dist[cur.y][cur.x] + step) {
        world.rival_dist[ny][nx] = world.rival_dist[cur.y][cur.x] + step;
        pq.push({nx, ny, world.rival_dist[ny][nx]});
      }
    }
  }
}

static void print_map()
{
  clear();

  for (int y = 0; y < MAP_Y; y++) {
    for (int x = 0; x < MAP_X; x++) {
      if (world.cur_map->cmap[y][x]) {
        if (world.cur_map->cmap[y][x] == &world.pc) attron(COLOR_PAIR(7));
        else attron(COLOR_PAIR(6));
        mvaddch(y, x, world.cur_map->cmap[y][x]->symbol);
        if (world.cur_map->cmap[y][x] == &world.pc) attroff(COLOR_PAIR(7));
        else attroff(COLOR_PAIR(6));
        continue;
      }

      switch (world.cur_map->map[y][x]) {
        case ter_boulder:
        case ter_mountain:
          attron(COLOR_PAIR(1)); mvaddch(y, x, BOULDER_SYMBOL); attroff(COLOR_PAIR(1)); break;
        case ter_tree:
        case ter_forest:
          attron(COLOR_PAIR(2)); mvaddch(y, x, TREE_SYMBOL); attroff(COLOR_PAIR(2)); break;
        case ter_grass:
          attron(COLOR_PAIR(2)); mvaddch(y, x, TALL_GRASS_SYMBOL); attroff(COLOR_PAIR(2)); break;
        case ter_clearing:
          attron(COLOR_PAIR(2)); mvaddch(y, x, SHORT_GRASS_SYMBOL); attroff(COLOR_PAIR(2)); break;
        case ter_path:
        case ter_gate:
          attron(COLOR_PAIR(3)); mvaddch(y, x, PATH_SYMBOL); attroff(COLOR_PAIR(3)); break;
        case ter_water:
          attron(COLOR_PAIR(4)); mvaddch(y, x, WATER_SYMBOL); attroff(COLOR_PAIR(4)); break;
        case ter_mart:
          attron(COLOR_PAIR(5)); mvaddch(y, x, POKEMART_SYMBOL); attroff(COLOR_PAIR(5)); break;
        case ter_center:
          attron(COLOR_PAIR(5)); mvaddch(y, x, POKEMON_CENTER_SYMBOL); attroff(COLOR_PAIR(5)); break;
        default:
          mvaddch(y, x, ERROR_SYMBOL); break;
      }
    }
  }

  mvprintw(MAP_Y, 0, "Map (%d,%d)  Move: yk u / h . l / bn j  t:list  f:fly  >:enter  Q:quit",
           world.cur_idx[dim_x] - (WORLD_SIZE / 2),
           world.cur_idx[dim_y] - (WORLD_SIZE / 2));
  refresh();
}

static void show_message(const string &line1, const string &line2 = "Press any key to continue")
{
  clear();
  mvprintw(10, 5, "%s", line1.c_str());
  mvprintw(12, 5, "%s", line2.c_str());
  refresh();
  getch();
}

static void show_trainer_list()
{
  struct TrainerInfo {
    char symbol;
    int dy, dx;
    int dist;
  };

  vector<TrainerInfo> trainers;
  for (int y = 0; y < MAP_Y; y++) {
    for (int x = 0; x < MAP_X; x++) {
      if (world.cur_map->cmap[y][x] && world.cur_map->cmap[y][x] != &world.pc) {
        int dy = y - world.pc.pos[dim_y];
        int dx = x - world.pc.pos[dim_x];
        trainers.push_back({world.cur_map->cmap[y][x]->symbol, dy, dx, abs(dy) + abs(dx)});
      }
    }
  }

  sort(trainers.begin(), trainers.end(), [](const TrainerInfo &a, const TrainerInfo &b) {
    if (a.dist != b.dist) return a.dist < b.dist;
    if (a.dy != b.dy) return a.dy < b.dy;
    return a.dx < b.dx;
  });

  int start = 0;
  const int max_lines = 20;
  int ch;

  while (true) {
    clear();
    int lines = min(max_lines, (int) trainers.size() - start);
    for (int i = 0; i < lines; i++) {
      auto &t = trainers[start + i];
      if (t.dy == 0) {
        mvprintw(i, 0, "%c, %d %s", t.symbol, abs(t.dx), t.dx < 0 ? "west" : "east");
      } else if (t.dx == 0) {
        mvprintw(i, 0, "%c, %d %s", t.symbol, abs(t.dy), t.dy < 0 ? "north" : "south");
      } else {
        mvprintw(i, 0, "%c, %d %s and %d %s", t.symbol,
                 abs(t.dy), t.dy < 0 ? "north" : "south",
                 abs(t.dx), t.dx < 0 ? "west" : "east");
      }
    }
    mvprintw(22, 0, "Use UP/DOWN arrows or k/j to scroll. ESC to exit.");
    refresh();

    ch = getch();
    if (ch == 27) break;
    if ((ch == KEY_UP || ch == 'k') && start > 0) start--;
    if ((ch == KEY_DOWN || ch == 'j') && start + max_lines < (int) trainers.size()) start++;
  }
}

static bool valid_pc_terrain(terrain_type t)
{
  return t != ter_tree && t != ter_boulder && t != ter_mountain && t != ter_water;
}


static void move_hiker_func(Character *c, pair_t dest)
{
  int min_cost = INT_MAX;
  int base = rand() & 0x7;

  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  for (int i = base; i < 8 + base; i++) {
    int nx = c->pos[dim_x] + all_dirs[i & 0x7][dim_x];
    int ny = c->pos[dim_y] + all_dirs[i & 0x7][dim_y];
    if (nx <= 0 || nx >= MAP_X - 1 || ny <= 0 || ny >= MAP_Y - 1) continue;
    if (world.cur_map->map[ny][nx] == ter_gate) continue;
    if (!world.cur_map->cmap[ny][nx] && world.hiker_dist[ny][nx] <= min_cost) {
      min_cost = world.hiker_dist[ny][nx];
      dest[dim_x] = nx;
      dest[dim_y] = ny;
    }
  }
}

static void move_rival_func(Character *c, pair_t dest)
{
  int min_cost = INT_MAX;
  int base = rand() & 0x7;

  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  for (int i = base; i < 8 + base; i++) {
    int nx = c->pos[dim_x] + all_dirs[i & 0x7][dim_x];
    int ny = c->pos[dim_y] + all_dirs[i & 0x7][dim_y];
    if (nx <= 0 || nx >= MAP_X - 1 || ny <= 0 || ny >= MAP_Y - 1) continue;
    if (world.cur_map->map[ny][nx] == ter_gate) continue;
    if (!world.cur_map->cmap[ny][nx] && world.rival_dist[ny][nx] < min_cost) {
      min_cost = world.rival_dist[ny][nx];
      dest[dim_x] = nx;
      dest[dim_y] = ny;
    }
  }
}

static void move_pacer_func(Character *c, pair_t dest)
{
  NPC *n = dynamic_cast<NPC *>(c);
  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  int nx = c->pos[dim_x] + n->dir[dim_x];
  int ny = c->pos[dim_y] + n->dir[dim_y];
  terrain_type t = world.cur_map->map[ny][nx];

  if (t == ter_gate || (t != ter_path && t != ter_grass && t != ter_clearing) || world.cur_map->cmap[ny][nx]) {
    n->dir[dim_x] *= -1;
    n->dir[dim_y] *= -1;
    nx = c->pos[dim_x] + n->dir[dim_x];
    ny = c->pos[dim_y] + n->dir[dim_y];
    t = world.cur_map->map[ny][nx];
  }

  if ((t == ter_path || t == ter_grass || t == ter_clearing) && !world.cur_map->cmap[ny][nx]) {
    dest[dim_x] = nx;
    dest[dim_y] = ny;
  }
}

static void move_wanderer_func(Character *c, pair_t dest)
{
  NPC *n = dynamic_cast<NPC *>(c);
  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  int nx = c->pos[dim_x] + n->dir[dim_x];
  int ny = c->pos[dim_y] + n->dir[dim_y];
  if (world.cur_map->map[ny][nx] == ter_gate ||
      world.cur_map->map[ny][nx] != world.cur_map->map[c->pos[dim_y]][c->pos[dim_x]] ||
      world.cur_map->cmap[ny][nx]) {
    rand_dir(n->dir);
    nx = c->pos[dim_x] + n->dir[dim_x];
    ny = c->pos[dim_y] + n->dir[dim_y];
  }

  if (world.cur_map->map[ny][nx] == world.cur_map->map[c->pos[dim_y]][c->pos[dim_x]] &&
      world.cur_map->map[ny][nx] != ter_gate && !world.cur_map->cmap[ny][nx]) {
    dest[dim_x] = nx;
    dest[dim_y] = ny;
  }
}

static void move_sentry_func(Character *c, pair_t dest)
{
  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];
}

static void move_explorer_func(Character *c, pair_t dest)
{
  NPC *n = dynamic_cast<NPC *>(c);
  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  int nx = c->pos[dim_x] + n->dir[dim_x];
  int ny = c->pos[dim_y] + n->dir[dim_y];

  if (world.cur_map->map[ny][nx] == ter_gate ||
      move_cost[char_other][world.cur_map->map[ny][nx]] == DIJKSTRA_PATH_MAX ||
      world.cur_map->cmap[ny][nx]) {
    rand_dir(n->dir);
    nx = c->pos[dim_x] + n->dir[dim_x];
    ny = c->pos[dim_y] + n->dir[dim_y];
  }

  if (world.cur_map->map[ny][nx] != ter_gate &&
      move_cost[char_other][world.cur_map->map[ny][nx]] != DIJKSTRA_PATH_MAX &&
      !world.cur_map->cmap[ny][nx]) {
    dest[dim_x] = nx;
    dest[dim_y] = ny;
  }
}

static void move_swimmer_func(Character *c, pair_t dest)
{
  Map *m = world.cur_map;
  NPC *n = dynamic_cast<NPC *>(c);
  pair_t dir;

  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];

  if (is_adjacent_terrain(m, world.pc.pos[dim_x], world.pc.pos[dim_y], ter_water) &&
      can_see(m, c, &world.pc)) {
    dir[dim_x] = world.pc.pos[dim_x] - c->pos[dim_x];
    dir[dim_y] = world.pc.pos[dim_y] - c->pos[dim_y];
    if (dir[dim_x]) dir[dim_x] /= abs(dir[dim_x]);
    if (dir[dim_y]) dir[dim_y] /= abs(dir[dim_y]);

    if ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] == ter_water) ||
        ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] == ter_path) &&
         is_adjacent_terrain(m, dest[dim_x] + dir[dim_x], dest[dim_y] + dir[dim_y], ter_water))) {
      dest[dim_x] += dir[dim_x];
      dest[dim_y] += dir[dim_y];
    } else if ((m->map[dest[dim_y]][dest[dim_x] + dir[dim_x]] == ter_water) ||
               ((m->map[dest[dim_y]][dest[dim_x] + dir[dim_x]] == ter_path) &&
                is_adjacent_terrain(m, dest[dim_x] + dir[dim_x], dest[dim_y], ter_water))) {
      dest[dim_x] += dir[dim_x];
    } else if ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x]] == ter_water) ||
               ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x]] == ter_path) &&
                is_adjacent_terrain(m, dest[dim_x], dest[dim_y] + dir[dim_y], ter_water))) {
      dest[dim_y] += dir[dim_y];
    }
  } else {
    dir[dim_x] = n->dir[dim_x];
    dir[dim_y] = n->dir[dim_y];

    if ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] != ter_water) &&
        !((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] == ter_path) &&
          is_adjacent_terrain(m, dest[dim_x] + dir[dim_x], dest[dim_y] + dir[dim_y], ter_water))) {
      rand_dir(dir);
      n->dir[dim_x] = dir[dim_x];
      n->dir[dim_y] = dir[dim_y];
    }

    if ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] == ter_water) ||
        ((m->map[dest[dim_y] + dir[dim_y]][dest[dim_x] + dir[dim_x]] == ter_path) &&
         is_adjacent_terrain(m, dest[dim_x] + dir[dim_x], dest[dim_y] + dir[dim_y], ter_water))) {
      dest[dim_x] += dir[dim_x];
      dest[dim_y] += dir[dim_y];
    }
  }

  if (m->cmap[dest[dim_y]][dest[dim_x]]) {
    dest[dim_x] = c->pos[dim_x];
    dest[dim_y] = c->pos[dim_y];
  }
}

static void move_pc_func(Character *c, pair_t dest)
{
  dest[dim_x] = c->pos[dim_x];
  dest[dim_y] = c->pos[dim_y];
}

static void (*move_func[num_movement_types])(Character *, pair_t) = {
  move_hiker_func,
  move_rival_func,
  move_pacer_func,
  move_wanderer_func,
  move_sentry_func,
  move_explorer_func,
  move_swimmer_func,
  move_pc_func,
};

static void handle_building_entry()
{
  terrain_type t = world.cur_map->map[world.pc.pos[dim_y]][world.pc.pos[dim_x]];
  if (t == ter_mart) {
    show_message("Welcome to the Pokemart!", "Press any key to leave");
  } else if (t == ter_center) {
    show_message("Welcome to the Pokemon Center!", "Press any key to leave");
  }
}

static bool move_to_map(int new_world_x, int new_world_y, int new_pc_x, int new_pc_y)
{
  if (new_world_x < 0 || new_world_x >= WORLD_SIZE || new_world_y < 0 || new_world_y >= WORLD_SIZE) {
    return false;
  }

  Map *old_map = world.cur_map;
  old_map->cmap[world.pc.pos[dim_y]][world.pc.pos[dim_x]] = nullptr;

  world.cur_idx[dim_x] = new_world_x;
  world.cur_idx[dim_y] = new_world_y;

  bool existed = world.world[new_world_y][new_world_x] != nullptr;
  new_map(false);

  if (!existed && world.cur_map->cmap[new_pc_y][new_pc_x]) {
    world.cur_map->cmap[new_pc_y][new_pc_x] = nullptr;
  }

  if (world.cur_map->cmap[new_pc_y][new_pc_x]) {
    if (!find_first_path(world.cur_map, new_pc_x, new_pc_y)) {
      return false;
    }
  }

  place_pc_on_existing_map(world.cur_map, new_pc_x, new_pc_y);
  pathfind(world.cur_map);
  return true;
}

static bool try_gate_move(int newx, int newy)
{
  if (newx == 0 && world.cur_map->map[newy][newx] == ter_gate) {
    return move_to_map(world.cur_idx[dim_x] - 1, world.cur_idx[dim_y], MAP_X - 2, newy);
  }
  if (newx == MAP_X - 1 && world.cur_map->map[newy][newx] == ter_gate) {
    return move_to_map(world.cur_idx[dim_x] + 1, world.cur_idx[dim_y], 1, newy);
  }
  if (newy == 0 && world.cur_map->map[newy][newx] == ter_gate) {
    return move_to_map(world.cur_idx[dim_x], world.cur_idx[dim_y] - 1, newx, MAP_Y - 2);
  }
  if (newy == MAP_Y - 1 && world.cur_map->map[newy][newx] == ter_gate) {
    return move_to_map(world.cur_idx[dim_x], world.cur_idx[dim_y] + 1, newx, 1);
  }
  return false;
}



void game_loop() {
  Character *c;
  int key;

  while (1) {

    print_map();  // fixes unused error

    if (world.cur_map->turn.empty()) continue;

    c = world.cur_map->turn.top();
    world.cur_map->turn.pop();

    if (!c) continue;

    if (c->is_pc()) {

      key = getch();

      int newx = world.pc.pos[dim_x];
      int newy = world.pc.pos[dim_y];

      switch (key) {

        case 'Q':
        case 'q':
          return;

        case 'y': newx--; newy--; break;
        case 'k': newy--; break;
        case 'u': newx++; newy--; break;
        case 'h': newx--; break;
        case 'l': newx++; break;
        case 'b': newx--; newy++; break;
        case 'j': newy++; break;
        case 'n': newx++; newy++; break;
        case '.': break;

        case 't':
          show_trainer_list();
          break;

        case '>':
          handle_building_entry();
          break;

        case 'f': {
          int x, y;
          mvprintw(22, 0, "Fly to coordinates x y in range [-200,200]: ");
          clrtoeol();
          echo();
          scanw("%d %d", &x, &y);
          noecho();

          if (x >= -200 && x <= 200 && y >= -200 && y <= 200) {
            world.cur_idx[dim_x] = x + 200;
            world.cur_idx[dim_y] = y + 200;

            if (!world.world[y + 200][x + 200]) {
              new_map(1);
            }

            world.cur_map = world.world[y + 200][x + 200];

            for (int i = 0; i < MAP_Y; i++) {
              for (int j = 0; j < MAP_X; j++) {
                if (world.cur_map->map[i][j] == ter_path) {
                  world.pc.pos[dim_x] = j;
                  world.pc.pos[dim_y] = i;
                  world.cur_map->cmap[i][j] = &world.pc;
                  goto placed;
                }
              }
            }
          placed:;
          }
          break;
      }

      // movement handling
      if (newx != world.pc.pos[dim_x] || newy != world.pc.pos[dim_y]) {

        if (try_gate_move(newx, newy)) {
          // moved maps
        }
        else if (valid_pc_terrain(world.cur_map->map[newy][newx]) &&
                 !world.cur_map->cmap[newy][newx]) {

          world.cur_map->cmap[world.pc.pos[dim_y]][world.pc.pos[dim_x]] = nullptr;

          world.pc.pos[dim_x] = newx;
          world.pc.pos[dim_y] = newy;

          world.cur_map->cmap[newy][newx] = &world.pc;
        }
      }

    }
  } 
  else {

      pair_t dest;
      move_func[c->mtype()](c, dest);

      if (dest[dim_x] != c->pos[dim_x] || dest[dim_y] != c->pos[dim_y]) {

        world.cur_map->cmap[c->pos[dim_y]][c->pos[dim_x]] = nullptr;

        c->pos[dim_x] = dest[dim_x];
        c->pos[dim_y] = dest[dim_y];

        world.cur_map->cmap[dest[dim_y]][dest[dim_x]] = c;
      }
    }

    world.cur_map->turn.push(c);
  }
}
int main(int argc, char *argv[])
{
  timeval tv {};
  uint32_t seed;

  if (argc == 2) {
    errno = 0;
    seed = strtol(argv[1], nullptr, 10);
    if (!isdigit(argv[1][0]) || errno) {
      cerr << "Invalid seed value on command line.\n";
      return -1;
    }
  } else {
    gettimeofday(&tv, nullptr);
    seed = (tv.tv_usec ^ (tv.tv_sec << 20)) & 0xffffffff;
  }

  cout << "Using seed: " << seed << "\n";
  srand(seed);

  initscr();
  start_color();
  use_default_colors();
  noecho();
  cbreak();
  keypad(stdscr, TRUE);

  init_pair(1, COLOR_GREEN,  -1);
  init_pair(2, COLOR_YELLOW, -1);
  init_pair(3, COLOR_WHITE,  -1);
  init_pair(4, COLOR_BLUE,   -1);
  init_pair(5, COLOR_RED,    -1);
  init_pair(6, COLOR_CYAN,   -1);
  init_pair(7, COLOR_MAGENTA,-1);

  init_world();
  game_loop();
  endwin();

  
  cout << "But how are you going to be the very best if you quit?\n";
  return 0;
}
