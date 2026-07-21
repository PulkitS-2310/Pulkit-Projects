#include "PokemonTerrain.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void initialize_table(struct PokemonTerrain *m)
{
    m -> North_Exit = rand() % 78 + 1;
    m -> South_Exit = rand() % 78 + 1;
    m -> East_Exit = rand() % 19 + 1;
    m -> West_Exit = rand() % 19 + 1;

    
    for(int i = 0; i<21;i++){
        for(int j =0; j<80; j++){
            m -> map[i][j] = '.';
        }
    }

    for(int i = 0; i<21; i++){
       m -> map[i][0] = '%';
        m -> map [i][79] = '%';
    }

    for (int j = 0; j<80;j++){
       m -> map[0][j] = '%';
       m -> map[20][j] = '%';
    }

   m -> map[0][m -> North_Exit]= '#';
   m -> map[20][m -> South_Exit]= '#';
   m -> map[m -> West_Exit][0]= '#';
   m -> map[m -> East_Exit][79]= '#';
}


void Grow_Roads(char map[21][80],
                int r, int c,
                int tr, int tc)
{
    map[r][c] = '#';

    while (r != tr || c != tc) {

        int moves[4][2];
        int count = 0;

        if (r < tr) moves[count][0] = 1,  moves[count++][1] = 0;
        if (r > tr) moves[count][0] = -1, moves[count++][1] = 0;
        if (c < tc) moves[count][0] = 0,  moves[count++][1] = 1;
        if (c > tc) moves[count][0] = 0,  moves[count++][1] = -1;

        if (rand() % 4 == 0) {
            moves[count][0] = (rand() % 2 ? 1 : -1);
            moves[count++][1] = 0;
        }

        int pick = rand() % count;
        r += moves[pick][0];
        c += moves[pick][1];

        if (r < 1) r = 1;
        if (r > 19) r = 19;
        if (c < 1) c = 1;
        if (c > 78) c = 78;

        if (map[r][c] == '.')
            map[r][c] = '#';
    }
}

void print_table (struct PokemonTerrain *m){

     for(int i = 0; i<21;i++){
        for(int j =0; j<80; j++){
            printf("%c", m -> map[i][j]);
        }
        printf("\n");
    }
}

void place_building(char map[21][80], char building)
{
    int Placed_Buildings = 0;
    int Attempt = 0;

    while (!Placed_Buildings && Attempt < 10000) {
        Attempt++;

        
        int Row = rand() % 18 + 1;
        int Col = rand() % 77 + 1;

        
        if (map[Row][Col] == '.' &&
            map[Row+1][Col] == '.' &&
            map[Row][Col+1] == '.' &&
            map[Row+1][Col+1] == '.') {

            if (map[Row-1][Col] == '#' || map[Row-1][Col+1] == '#' ||
                map[Row+2][Col] == '#' || map[Row+2][Col+1] == '#' ||
                map[Row][Col-1] == '#' || map[Row+1][Col-1] == '#' ||
                map[Row][Col+2] == '#' || map[Row+1][Col+2] == '#') {

                map[Row][Col]     = building;
                map[Row+1][Col]   = building;
                map[Row][Col+1]   = building;
                map[Row+1][Col+1] = building;

                Placed_Buildings = 1;
            }
        }
    }
}



void Grow_Terrain(char map[21][80], int row, int col, char terrain){

    for(int i = 0; i< 100; i++){
        int new_row = row;
        int new_col = col;
        int direction = rand()%4;

        if (direction == 0){
            new_row--;
        }
        else if(direction == 1){
            new_row++;
        }
        else if(direction == 2){
            new_col--;
        }
        else if(direction == 3){
            new_col++;
        }

        if(new_row >=1 && new_row <= 19 && new_col>=1 && new_col <= 78){
            if(terrain == '%' && map[new_row][new_col] == '.'){
                map[new_row][new_col] = terrain;
                row = new_row;
                col = new_col;
            }

             if(terrain == ':' && map[new_row][new_col] == '.'){
                map[new_row][new_col] = terrain;
                row = new_row;
                col = new_col;
            }
            if(terrain == '~' && (map[new_row][new_col]=='.' || map[new_row][new_col] == ':')){
                map[new_row][new_col] = terrain;
                row = new_row;
                col = new_col;
            }
            if(terrain == '^' && (map[new_row][new_col]=='.' || map[new_row][new_col] == ':')){
                map[new_row][new_col] = terrain;
                row = new_row;
                col = new_col;
            }
        }
    }
}

struct PokemonTerrain *World[401][401];


int main(){
    srand(time(NULL));

    int x = 200;
    int y = 200;

    if(World[y][x] == NULL){
        World[y][x] = malloc(sizeof(struct PokemonTerrain));
        initialize_table(World[y][x]);
    //     Grow_Roads(World[y][x]->map, 1, World[y][x]->North_Exit, 19, World[y][x]->South_Exit);
    //     Grow_Roads(World[y][x]->map, World[y][x]->West_Exit, 1, World[y][x]->East_Exit, 78);
        Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
        Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
        Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '^');
        Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '~');
    // //    place_building(World[y][x]->map, 'M');
    // //             place_building(World[y][x]->map, 'C');

    }
    print_table(World[y][x]);
    while (1) {
    char command;

    scanf(" %c", &command);

    if (command == 'q') {
        break;
    }
    if (command == 'n') {
        if (y > 0) {
            y--;

            if (World[y][x] == NULL) {
                World[y][x] = malloc(sizeof(struct PokemonTerrain));
                initialize_table(World[y][x]);
                 Grow_Roads(World[y][x]->map, 1, World[y][x]->North_Exit, 19, World[y][x]->South_Exit);
                 Grow_Roads(World[y][x]->map, World[y][x]->West_Exit, 1, World[y][x]->East_Exit, 78);
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '^');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '~');
                place_building(World[y][x]->map, 'M');
                place_building(World[y][x]->map, 'C');

            }

            print_table(World[y][x]);
        }
    }

     if (command == 's') {
        if (y < 201) {
            y++;

            if (World[y][x] == NULL) {
                World[y][x] = malloc(sizeof(struct PokemonTerrain));
                initialize_table(World[y][x]);
                 Grow_Roads(World[y][x]->map, 1, World[y][x]->North_Exit, 19, World[y][x]->South_Exit);
                 Grow_Roads(World[y][x]->map, World[y][x]->West_Exit, 1, World[y][x]->East_Exit, 78);
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '^');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '~');
                place_building(World[y][x]->map, 'M');
                place_building(World[y][x]->map, 'C');

                
            }

            print_table(World[y][x]);
        }
    }

     if (command == 'e') {
        if (x < 200) {
            x++;

            if (World[y][x] == NULL) {
                World[y][x] = malloc(sizeof(struct PokemonTerrain));
                initialize_table(World[y][x]);
                 Grow_Roads(World[y][x]->map, 1, World[y][x]->North_Exit, 19, World[y][x]->South_Exit);
                 Grow_Roads(World[y][x]->map, World[y][x]->West_Exit, 1, World[y][x]->East_Exit, 78);
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '^');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '~');
             place_building(World[y][x]->map, 'M');
                place_building(World[y][x]->map, 'C');

            }

            print_table(World[y][x]);
        }
    }

     if (command == 'w') {
        if (x > 0) {
            x--;

            if (World[y][x] == NULL) {
                World[y][x] = malloc(sizeof(struct PokemonTerrain));
                initialize_table(World[y][x]);
                 Grow_Roads(World[y][x]->map, 1, World[y][x]->North_Exit, 19, World[y][x]->South_Exit);
                 Grow_Roads(World[y][x]->map, World[y][x]->West_Exit, 1, World[y][x]->East_Exit, 78);
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, ':');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '^');
                Grow_Terrain(World[y][x]->map, rand() % 19 + 1, rand() % 78 + 1, '~');
              place_building(World[y][x]->map, 'M');
                place_building(World[y][x]->map, 'C');
            }

            print_table(World[y][x]);
        }
    }
  if (command == 'f') {
    int New_X, New_Y;

    scanf("%d %d", &New_X, &New_Y);

    if (New_X < 0 || New_X > 200 || New_Y < 0 || New_Y > 201) {
        printf("Out of bounds\n");
        continue;
    }

    x = New_X;
    y = New_Y;

    if (World[y][x] == NULL) {
        World[y][x] = malloc(sizeof(struct PokemonTerrain));
        initialize_table(World[y][x]);

        Grow_Roads(World[y][x]->map, 1,
                   World[y][x]->North_Exit, 19,
                   World[y][x]->South_Exit);

        Grow_Roads(World[y][x]->map,
                   World[y][x]->West_Exit, 1,
                   World[y][x]->East_Exit, 78);

        Grow_Terrain(World[y][x]->map, rand()%19+1, rand()%78+1, ':');
        Grow_Terrain(World[y][x]->map, rand()%19+1, rand()%78+1, ':');
        Grow_Terrain(World[y][x]->map, rand()%19+1, rand()%78+1, '^');
        Grow_Terrain(World[y][x]->map, rand()%19+1, rand()%78+1, '~');
       place_building(World[y][x]->map, 'M');
                place_building(World[y][x]->map, 'C');

    }

    print_table(World[y][x]);
 }
 }
  return 0;
}







