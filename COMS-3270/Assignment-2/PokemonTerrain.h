#ifndef POKEMONTERRAIN_H
#define POKEMONTERRAIN_H

struct PokemonTerrain {
    char map[21][80];
    int North_Exit;
    int South_Exit;
    int East_Exit;
    int West_Exit;
};

void initialize_table(struct PokemonTerrain *m);
void print_table(struct PokemonTerrain *m);

#endif
