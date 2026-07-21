#ifndef CSV_PARSER_H
#define CSV_PARSER_H

#include <string>
#include <unordered_map>
#include <vector>

struct Pokemon {
    int id;
    std::string identifier;
    int species_id;
    int height;
    int weight;
    int base_experience;
    int order;
    int is_default;
};

struct Move {
    int id;
    std::string identifier;
    int generation_id;
    int type_id;
    int power;
    int pp;
    int accuracy;
    int priority;
    int target_id;
    int damage_class_id;
    int effect_id;
    int effect_chance;
};

struct PokemonMove {
    int pokemon_id;
    int version_group_id;
    int move_id;
    int pokemon_move_method_id;
    int level;
    int order;
};

struct PokemonStat {
    int pokemon_id;
    int stat_id;
    int base_stat;
    int effort;
};

struct PokemonType {
    int pokemon_id;
    int type_id;
    int slot;
};

struct PokemonBaseStats {
    int hp;
    int attack;
    int defense;
    int speed;
    int sp_attack;
    int sp_defense;

    PokemonBaseStats()
        : hp(50), attack(50), defense(50), speed(50),
          sp_attack(50), sp_defense(50) {}
};

extern std::vector<Pokemon> pokemon_data;
extern std::vector<Move> moves_data;
extern std::vector<PokemonMove> pokemon_moves_data;
extern std::unordered_map<int, PokemonBaseStats> pokemon_stats_by_pokemon;
extern std::unordered_map<int, std::vector<int> > pokemon_types_by_pokemon;

std::string get_database_path();
void load_pokedex_data();

void parse_pokemon();
void parse_moves();
void parse_pokemon_moves();
void parse_pokemon_species();
void parse_experience();
void parse_type_names();
void parse_pokemon_stats();
void parse_stats();
void parse_pokemon_types();

const Pokemon *get_pokemon_by_id(int id);
const Move *get_move_by_id(int id);
std::vector<Move> get_level_up_moves_for_pokemon(int pokemon_id, int level);
std::vector<int> get_type_ids_for_pokemon(int pokemon_id);
PokemonBaseStats get_base_stats_for_pokemon(int pokemon_id);

#endif
