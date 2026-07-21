#ifndef CSV_PARSER_H
#define CSV_PARSER_H

#include <string>
#include <vector>


// Structs
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

extern std::vector<Pokemon> pokemon_data;

void parse_pokemon();
void parse_moves();
void parse_pokemon_moves();
void parse_pokemon_species();
void parse_experience();
void parse_type_names();
void parse_pokemon_stats();
void parse_stats();
void parse_pokemon_types();

#endif