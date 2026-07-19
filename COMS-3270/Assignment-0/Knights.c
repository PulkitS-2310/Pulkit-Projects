
#include <stdio.h>

#define DIM 5

int visited[DIM][DIM] = {0};
int path[DIM * DIM];

int moves[8][2] = {
    {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
    {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
};

void tour(int row, int col, int step) {
    
    if (step == DIM * DIM) {
        for (int i = 0; i < DIM * DIM; i++) {
            printf("%d", path[i]);
            if (i < DIM * DIM - 1) {
                printf(",");
            }
        }
        printf("\n");
        return;
    }

   
    for (int i = 0; i < 8; i++) {
        int next_row = row + moves[i][0];
        int next_col = col + moves[i][1];

        
        if (next_row >= 0 && next_row < DIM &&
            next_col >= 0 && next_col < DIM &&
            visited[next_row][next_col] == 0) {

            
            visited[next_row][next_col] = 1;
            path[step] = next_row * DIM + next_col + 1;

            
            tour(next_row, next_col, step + 1);

           
            visited[next_row][next_col] = 0;
        }
    }
}

int main(void) {
    
    for (int row = 0; row < DIM; row++) {
        for (int col = 0; col < DIM; col++) {

            
            for (int i = 0; i < DIM; i++)
                for (int j = 0; j < DIM; j++)
                    visited[i][j] = 0;

            
            visited[row][col] = 1;
            path[0] = row * DIM + col + 1;

            
            tour(row, col, 1);
        }
    }

    return 0;
}
