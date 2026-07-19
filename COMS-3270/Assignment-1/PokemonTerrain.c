#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void initialize_table(char map[21][80],
                      int *N_exit, int *S_exit,
                      int *E_exit, int *W_exit)
{
    *N_exit = rand() % 78 + 1;
    *S_exit = rand() % 78 + 1;
    *E_exit = rand() % 19 + 1;
    *W_exit = rand() % 19 + 1;

    
    for(int i = 0; i<21;i++){
        for(int j =0; j<80; j++){
            map[i][j] = '.';
        }
    }

    for(int i = 0; i<21; i++){
        map[i][0] = '%';
        map [i][79] = '%';
    }

    for (int j = 0; j<80;j++){
        map[0][j] = '%';
        map[20][j] = '%';
    }

    map[0][*N_exit]= '#';
    map[20][*S_exit]= '#';
    map[*W_exit][0]= '#';
    map[*E_exit][79]= '#';

}


void Grow_Roads(char map[21][80],
                int row, int col,
                int Target_Row, int Target_Col)
{
    map[row][col] = '#';

    while (row != Target_Row || col != Target_Col) {

        int moves[4][2];
        int count = 0;

        if (row < Target_Row) moves[count][0] = 1,  moves[count++][1] = 0;
        if (row > Target_Row) moves[count][0] = -1, moves[count++][1] = 0;
        if (col < Target_Col) moves[count][0] = 0,  moves[count++][1] = 1;
        if (col > Target_Col) moves[count][0] = 0,  moves[count++][1] = -1;

        if (rand() % 4 == 0) {
            moves[count][0] = (rand() % 2 ? 1 : -1);
            moves[count++][1] = 0;
        }

        int pick = rand() % count;
        row += moves[pick][0];
        col += moves[pick][1];

        if (row < 1) row = 1;
        if (row > 19) row = 19;
        if (col < 1) col = 1;
        if (col> 78) col = 78;

        if (map[row][col] == '.')
            map[row][col] = '#';
    }
}

void print_table (char map[21][80]){

     for(int i = 0; i<21;i++){
        for(int j =0; j<80; j++){
            printf("%c", map[i][j]);
        }
        printf("\n");
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

int main(){
    int N_exit, S_exit, E_exit, W_exit;
    char map[21][80];
    srand(time(NULL));
    initialize_table(map, &N_exit, &S_exit, &E_exit, &W_exit);
    Grow_Roads(map, 1, N_exit, 19, S_exit);
    Grow_Roads(map, W_exit, 1, E_exit, 78);
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, ':');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, ':');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '^');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '^');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '^');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '^');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '~');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, ':');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, ':');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '%');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '%');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '%');
    Grow_Terrain(map, rand() % 19 + 1, rand() % 78 + 1, '%');
    print_table(map);

}

