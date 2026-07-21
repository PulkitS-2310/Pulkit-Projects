#ifndef POKEMON_H
#define POKEMON_H

#include <string>
#include <vector>

#include "csv_parser.h"

class npc;

struct PokemonInstance {
    int id;
    std::string name;
    int level;

    int max_hp;
    int current_hp;
    int attack;
    int defense;
    int speed;
    int sp_attack;
    int sp_defense;

    int iv_hp;
    int iv_attack;
    int iv_defense;
    int iv_speed;
    int iv_sp_attack;
    int iv_sp_defense;

    int base_hp;
    int base_attack;
    int base_defense;
    int base_speed;
    int base_sp_attack;
    int base_sp_defense;

    std::vector<int> types;
    std::vector<Move> moves;

    PokemonInstance()
        : id(0), level(1), max_hp(1), current_hp(1), attack(1), defense(1),
          speed(1), sp_attack(1), sp_defense(1), iv_hp(0), iv_attack(0),
          iv_defense(0), iv_speed(0), iv_sp_attack(0), iv_sp_defense(0),
          base_hp(50), base_attack(50), base_defense(50), base_speed(50),
          base_sp_attack(50), base_sp_defense(50) {}

    bool is_knocked_out() const { return current_hp <= 0; }
};

void load_pokemon_system();
int current_world_distance();

PokemonInstance generate_random_pokemon(int distance);
PokemonInstance generate_pokemon_by_species(int pokemon_id, int level);
void give_starter_pokemon();
void trigger_wild_encounter();
void assign_trainer_party(npc *n, int distance);

void restore_pc_supplies();
void heal_party(std::vector<PokemonInstance> &party);
int count_usable_pokemon(const std::vector<PokemonInstance> &party);
int first_usable_pokemon(const std::vector<PokemonInstance> &party);
bool party_has_usable_pokemon(const std::vector<PokemonInstance> &party);

bool attempt_move_hit(const Move &move);
bool move_gets_stab(const PokemonInstance &pokemon, const Move &move);
int compute_damage(const PokemonInstance &attacker,
                   const PokemonInstance &defender,
                   const Move &move);
Move get_fallback_move();

#endif
