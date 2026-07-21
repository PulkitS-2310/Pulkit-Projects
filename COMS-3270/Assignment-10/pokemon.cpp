#include "pokemon.h"

#include <algorithm>
#include <climits>
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <set>
#include <vector>

#include <ncurses.h>

#include "character.h"
#include "io.h"
#include "poke327.h"

using namespace std;

static const int DEFAULT_POTIONS = 5;
static const int DEFAULT_REVIVES = 3;
static const int DEFAULT_POKEBALLS = 5;
static const int STARTING_POKEBUCKS = 500;

static int rand_iv()
{
    return rand() % 16;
}

static int calculate_stat(int base, int iv, int level, bool is_hp)
{
    if (is_hp) {
        return (((base + iv) * 2 * level) / 100) + level + 10;
    }

    return (((base + iv) * 2 * level) / 100) + 5;
}

static int clamp_level(int level)
{
    if (level < 1) {
        return 1;
    }
    if (level > 100) {
        return 100;
    }
    return level;
}

static vector<Pokemon> get_default_pokemon_pool()
{
    vector<Pokemon> defaults;
    size_t i;

    for (i = 0; i < pokemon_data.size(); i++) {
        if (pokemon_data[i].id != INT_MAX && pokemon_data[i].is_default == 1) {
            defaults.push_back(pokemon_data[i]);
        }
    }

    if (defaults.empty()) {
        defaults = pokemon_data;
    }

    return defaults;
}

static vector<Move> choose_moves_for_pokemon(int pokemon_id, int level)
{
    vector<Move> learned = get_level_up_moves_for_pokemon(pokemon_id, level);
    vector<Move> chosen;
    set<int> used_ids;

    while (!learned.empty() && chosen.size() < 4) {
        int idx = rand() % learned.size();
        if (used_ids.insert(learned[idx].id).second) {
            chosen.push_back(learned[idx]);
        }
        learned.erase(learned.begin() + idx);
    }

    if (chosen.empty()) {
        chosen.push_back(get_fallback_move());
    }

    return chosen;
}

void load_pokemon_system()
{
    load_pokedex_data();
}

int current_world_distance()
{
    return abs(world.cur_idx[dim_x] - (WORLD_SIZE / 2)) +
           abs(world.cur_idx[dim_y] - (WORLD_SIZE / 2));
}

PokemonInstance generate_pokemon_by_species(int pokemon_id, int level)
{
    load_pokemon_system();

    PokemonInstance p;
    const Pokemon *base = get_pokemon_by_id(pokemon_id);
    PokemonBaseStats stats = get_base_stats_for_pokemon(pokemon_id);

    p.id = pokemon_id;
    p.name = base ? base->identifier : string("missingno");
    p.level = clamp_level(level);

    p.iv_hp = rand_iv();
    p.iv_attack = rand_iv();
    p.iv_defense = rand_iv();
    p.iv_speed = rand_iv();
    p.iv_sp_attack = rand_iv();
    p.iv_sp_defense = rand_iv();

    p.base_hp = stats.hp;
    p.base_attack = stats.attack;
    p.base_defense = stats.defense;
    p.base_speed = stats.speed;
    p.base_sp_attack = stats.sp_attack;
    p.base_sp_defense = stats.sp_defense;

    p.max_hp = calculate_stat(p.base_hp, p.iv_hp, p.level, true);
    p.current_hp = p.max_hp;
    p.attack = calculate_stat(p.base_attack, p.iv_attack, p.level, false);
    p.defense = calculate_stat(p.base_defense, p.iv_defense, p.level, false);
    p.speed = calculate_stat(p.base_speed, p.iv_speed, p.level, false);
    p.sp_attack = calculate_stat(p.base_sp_attack, p.iv_sp_attack, p.level, false);
    p.sp_defense = calculate_stat(p.base_sp_defense, p.iv_sp_defense, p.level, false);

    p.types = get_type_ids_for_pokemon(pokemon_id);
    p.moves = choose_moves_for_pokemon(pokemon_id, p.level);

    return p;
}

PokemonInstance generate_random_pokemon(int distance)
{
    load_pokemon_system();

    vector<Pokemon> defaults = get_default_pokemon_pool();
    Pokemon chosen = defaults[rand() % defaults.size()];

    int min_level;
    int max_level;

    if (distance <= 200) {
        min_level = 1;
        max_level = distance / 2;
        if (max_level < 1) {
            max_level = 1;
        }
    } else {
        min_level = (distance - 200) / 2;
        if (min_level < 1) {
            min_level = 1;
        }
        max_level = 100;
    }

    if (max_level < min_level) {
        max_level = min_level;
    }

    return generate_pokemon_by_species(chosen.id,
                                       (rand() % (max_level - min_level + 1)) +
                                       min_level);
}

void restore_pc_supplies()
{
    world.pc.potions = DEFAULT_POTIONS;
    world.pc.revives = DEFAULT_REVIVES;
    world.pc.pokeballs = DEFAULT_POKEBALLS;
}

void give_starter_pokemon()
{
    load_pokemon_system();

    vector<Pokemon> defaults = get_default_pokemon_pool();
    Pokemon p1 = defaults[rand() % defaults.size()];
    Pokemon p2 = defaults[rand() % defaults.size()];
    Pokemon p3 = defaults[rand() % defaults.size()];

    while (p2.id == p1.id) {
        p2 = defaults[rand() % defaults.size()];
    }
    while (p3.id == p1.id || p3.id == p2.id) {
        p3 = defaults[rand() % defaults.size()];
    }

    PokemonInstance starter1 = generate_pokemon_by_species(p1.id, 1);
    PokemonInstance starter2 = generate_pokemon_by_species(p2.id, 1);
    PokemonInstance starter3 = generate_pokemon_by_species(p3.id, 1);

    cout << "Choose your starter Pokemon:\n";
    cout << "1. " << starter1.name << " (Level " << starter1.level << ")\n";
    cout << "2. " << starter2.name << " (Level " << starter2.level << ")\n";
    cout << "3. " << starter3.name << " (Level " << starter3.level << ")\n";

    int choice = 1;
    cin >> choice;

    PokemonInstance chosen = starter1;
    if (choice == 2) {
        chosen = starter2;
    } else if (choice == 3) {
        chosen = starter3;
    }

    world.pc.party.clear();
    world.pc.storage.clear();
    world.pc.party.push_back(chosen);
    world.pc.active_pokemon = 0;
    restore_pc_supplies();
    world.pc.pokebucks = STARTING_POKEBUCKS;

    cout << "You chose " << chosen.name << "!\n";
}

void trigger_wild_encounter()
{
    PokemonInstance wild = generate_random_pokemon(current_world_distance());
    io_wild_battle(wild);
}

void assign_trainer_party(npc *n, int distance)
{
    if (!n) {
        return;
    }

    n->party.clear();
    n->active_pokemon = 0;

    int party_size = 1 + (rand() % 6);
    int i;
    for (i = 0; i < party_size; i++) {
        n->party.push_back(generate_random_pokemon(distance));
    }
}

void heal_party(vector<PokemonInstance> &party)
{
    size_t i;
    for (i = 0; i < party.size(); i++) {
        party[i].current_hp = party[i].max_hp;
    }
}

int count_usable_pokemon(const vector<PokemonInstance> &party)
{
    int count = 0;
    size_t i;

    for (i = 0; i < party.size(); i++) {
        if (!party[i].is_knocked_out()) {
            count++;
        }
    }

    return count;
}

int first_usable_pokemon(const vector<PokemonInstance> &party)
{
    size_t i;
    for (i = 0; i < party.size(); i++) {
        if (!party[i].is_knocked_out()) {
            return static_cast<int>(i);
        }
    }

    return -1;
}

bool party_has_usable_pokemon(const vector<PokemonInstance> &party)
{
    return first_usable_pokemon(party) != -1;
}

bool attempt_move_hit(const Move &move)
{
    int accuracy = (move.accuracy == INT_MAX) ? 100 : move.accuracy;
    return (rand() % 100) < accuracy;
}

bool move_gets_stab(const PokemonInstance &pokemon, const Move &move)
{
    size_t i;
    for (i = 0; i < pokemon.types.size(); i++) {
        if (pokemon.types[i] == move.type_id) {
            return true;
        }
    }
    return false;
}


static double single_type_effectiveness(int move_type, int defender_type)
{
    /* Type ids match the CSV data:
       1 normal, 2 fighting, 3 flying, 4 poison, 5 ground, 6 rock,
       7 bug, 8 ghost, 9 steel, 10 fire, 11 water, 12 grass,
       13 electric, 14 psychic, 15 ice, 16 dragon, 17 dark, 18 fairy. */

    switch (move_type) {
    case 1:  /* normal */
        if (defender_type == 6 || defender_type == 9) return 0.5;
        if (defender_type == 8) return 0.0;
        break;
    case 2:  /* fighting */
        if (defender_type == 1 || defender_type == 6 || defender_type == 9 ||
            defender_type == 15 || defender_type == 17) return 2.0;
        if (defender_type == 3 || defender_type == 4 || defender_type == 7 ||
            defender_type == 14 || defender_type == 18) return 0.5;
        if (defender_type == 8) return 0.0;
        break;
    case 3:  /* flying */
        if (defender_type == 2 || defender_type == 7 || defender_type == 12) return 2.0;
        if (defender_type == 6 || defender_type == 9 || defender_type == 13) return 0.5;
        break;
    case 4:  /* poison */
        if (defender_type == 12 || defender_type == 18) return 2.0;
        if (defender_type == 4 || defender_type == 5 || defender_type == 6 ||
            defender_type == 8) return 0.5;
        if (defender_type == 9) return 0.0;
        break;
    case 5:  /* ground */
        if (defender_type == 4 || defender_type == 6 || defender_type == 9 ||
            defender_type == 10 || defender_type == 13) return 2.0;
        if (defender_type == 7 || defender_type == 12) return 0.5;
        if (defender_type == 3) return 0.0;
        break;
    case 6:  /* rock */
        if (defender_type == 3 || defender_type == 7 || defender_type == 10 ||
            defender_type == 15) return 2.0;
        if (defender_type == 2 || defender_type == 5 || defender_type == 9) return 0.5;
        break;
    case 7:  /* bug */
        if (defender_type == 12 || defender_type == 14 || defender_type == 17) return 2.0;
        if (defender_type == 2 || defender_type == 3 || defender_type == 4 ||
            defender_type == 8 || defender_type == 9 || defender_type == 10 ||
            defender_type == 18) return 0.5;
        break;
    case 8:  /* ghost */
        if (defender_type == 8 || defender_type == 14) return 2.0;
        if (defender_type == 17) return 0.5;
        if (defender_type == 1) return 0.0;
        break;
    case 9:  /* steel */
        if (defender_type == 6 || defender_type == 15 || defender_type == 18) return 2.0;
        if (defender_type == 9 || defender_type == 10 || defender_type == 11 ||
            defender_type == 13) return 0.5;
        break;
    case 10: /* fire */
        if (defender_type == 7 || defender_type == 9 || defender_type == 12 ||
            defender_type == 15) return 2.0;
        if (defender_type == 6 || defender_type == 10 || defender_type == 11 ||
            defender_type == 16) return 0.5;
        break;
    case 11: /* water */
        if (defender_type == 5 || defender_type == 6 || defender_type == 10) return 2.0;
        if (defender_type == 11 || defender_type == 12 || defender_type == 16) return 0.5;
        break;
    case 12: /* grass */
        if (defender_type == 5 || defender_type == 6 || defender_type == 11) return 2.0;
        if (defender_type == 3 || defender_type == 4 || defender_type == 7 ||
            defender_type == 9 || defender_type == 10 || defender_type == 12 ||
            defender_type == 16) return 0.5;
        break;
    case 13: /* electric */
        if (defender_type == 3 || defender_type == 11) return 2.0;
        if (defender_type == 12 || defender_type == 13 || defender_type == 16) return 0.5;
        if (defender_type == 5) return 0.0;
        break;
    case 14: /* psychic */
        if (defender_type == 2 || defender_type == 4) return 2.0;
        if (defender_type == 9 || defender_type == 14) return 0.5;
        if (defender_type == 17) return 0.0;
        break;
    case 15: /* ice */
        if (defender_type == 3 || defender_type == 5 || defender_type == 12 ||
            defender_type == 16) return 2.0;
        if (defender_type == 9 || defender_type == 10 || defender_type == 11 ||
            defender_type == 15) return 0.5;
        break;
    case 16: /* dragon */
        if (defender_type == 16) return 2.0;
        if (defender_type == 9) return 0.5;
        if (defender_type == 18) return 0.0;
        break;
    case 17: /* dark */
        if (defender_type == 8 || defender_type == 14) return 2.0;
        if (defender_type == 2 || defender_type == 17 || defender_type == 18) return 0.5;
        break;
    case 18: /* fairy */
        if (defender_type == 2 || defender_type == 16 || defender_type == 17) return 2.0;
        if (defender_type == 4 || defender_type == 9 || defender_type == 10) return 0.5;
        break;
    default:
        break;
    }

    return 1.0;
}

double type_effectiveness_multiplier(const PokemonInstance &defender,
                                     const Move &move)
{
    double multiplier = 1.0;

    for (size_t i = 0; i < defender.types.size(); i++) {
        multiplier *= single_type_effectiveness(move.type_id, defender.types[i]);
    }

    return multiplier;
}

std::string type_effectiveness_message(double multiplier)
{
    if (multiplier == 0.0) {
        return " It had no effect.";
    }
    if (multiplier > 1.0) {
        return " It's super effective!";
    }
    if (multiplier < 1.0) {
        return " It's not very effective.";
    }

    return "";
}

int attempt_pokemon_capture(const PokemonInstance &wild)
{
    int hp_percent;
    int chance;

    if (wild.max_hp <= 0) {
        hp_percent = 100;
    } else {
        hp_percent = (wild.current_hp * 100) / wild.max_hp;
    }

    if (hp_percent < 0) {
        hp_percent = 0;
    }
    if (hp_percent > 100) {
        hp_percent = 100;
    }

    /* A simplified catch formula for this project:
       low HP helps a lot, higher level makes the Pokemon harder to catch. */
    chance = 25 + ((100 - hp_percent) / 2) - (wild.level / 3);

    if (wild.is_knocked_out()) {
        chance += 20;
    }

    if (chance < 10) {
        chance = 10;
    }
    if (chance > 90) {
        chance = 90;
    }

    return (rand() % 100) < chance;
}

int compute_damage(const PokemonInstance &attacker,
                   const PokemonInstance &defender,
                   const Move &move)
{
    int power = move.power;

    if (power == INT_MAX || power < 0) {
        power = 0;
    }

    /* For status / non-damaging moves, use the project simplification:
       just treat them as minimum 1 damage instead of letting the formula
       turn them into weird values like 3 damage. */
    if (power == 0) {
        return 1;
    }

    int attack = attacker.attack;
    int defense = defender.defense <= 0 ? 1 : defender.defense;

    double critical =
        ((rand() % 256) < (attacker.base_speed / 2)) ? 1.5 : 1.0;
    double random_factor = (85 + (rand() % 16)) / 100.0;
    double stab = move_gets_stab(attacker, move) ? 1.5 : 1.0;
    double type = type_effectiveness_multiplier(defender, move);

    if (type == 0.0) {
        return 0;
    }

    double level_term = ((2.0 * attacker.level) / 5.0) + 2.0;
    double damage = ((((level_term * power * attack) / defense) / 50.0) + 2.0) *
                    critical * random_factor * stab * type;

    if (damage < 1.0) {
        damage = 1.0;
    }

    return static_cast<int>(damage);
}
Move get_fallback_move()
{
    load_pokemon_system();

    size_t i;
    for (i = 0; i < moves_data.size(); i++) {
        if (moves_data[i].identifier == "tackle") {
            return moves_data[i];
        }
    }

    for (i = 0; i < moves_data.size(); i++) {
        if (moves_data[i].power != INT_MAX && moves_data[i].power > 0) {
            return moves_data[i];
        }
    }

    Move move;
    move.id = 0;
    move.identifier = "struggle";
    move.generation_id = 0;
    move.type_id = 1;
    move.power = 50;
    move.pp = 35;
    move.accuracy = 100;
    move.priority = 0;
    move.target_id = 0;
    move.damage_class_id = 0;
    move.effect_id = 0;
    move.effect_chance = 0;
    return move;
}
