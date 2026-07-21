#ifndef POKEMON_H
#define POKEMON_H

#include <string>
#include <vector>

struct PokemonInstance {
    int id;
    std::string name;
    int level;

    // Stats
    int hp;
    int attack;
    int defense;
    int speed;
    int sp_attack;
    int sp_defense;

    // IVs
    int iv_hp;
    int iv_attack;
    int iv_defense;
    int iv_speed;
    int iv_sp_attack;
    int iv_sp_defense;

    // Moves (just names for now)
    std::vector<std::string> moves;
};

PokemonInstance generate_random_pokemon(int distance);
void give_starter_pokemon();
void trigger_wild_encounter();
#endif