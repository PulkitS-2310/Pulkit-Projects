#include "csv_parser.h"

#include <climits>
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <unistd.h>
#include <unordered_map>
#include <utility>
#include <vector>

using namespace std;

vector<Pokemon> pokemon_data;
vector<Move> moves_data;
vector<PokemonMove> pokemon_moves_data;
unordered_map<int, PokemonBaseStats> pokemon_stats_by_pokemon;
unordered_map<int, vector<int> > pokemon_types_by_pokemon;

static unordered_map<int, Pokemon> pokemon_by_id;
static unordered_map<int, Move> move_by_id;
static unordered_map<int, vector<PokemonMove> > moves_by_pokemon;
static bool pokedex_loaded = false;

static int safe_stoi(const string &value)
{
    return value.empty() ? INT_MAX : stoi(value);
}

string get_database_path()
{
    if (access("/share/cs327/pokedex", F_OK) == 0) {
        return "/share/cs327/pokedex";
    }

    char *home = getenv("HOME");
    if (home) {
        string path = string(home) + "/.poke327/pokedex";
        if (access(path.c_str(), F_OK) == 0) {
            return path;
        }
    }

    cerr << "Database not found\n";
    exit(1);
}

void parse_pokemon()
{
    pokemon_data.clear();
    pokemon_by_id.clear();

    const string file_path = get_database_path() + "/pokedex/data/csv/pokemon.csv";
    ifstream file(file_path.c_str());
    if (!file.is_open()) {
        cerr << "Failed to open pokemon.csv\n";
        return;
    }

    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        Pokemon p;

        getline(ss, value, ','); p.id = safe_stoi(value);
        getline(ss, value, ','); p.identifier = value;
        getline(ss, value, ','); p.species_id = safe_stoi(value);
        getline(ss, value, ','); p.height = safe_stoi(value);
        getline(ss, value, ','); p.weight = safe_stoi(value);
        getline(ss, value, ','); p.base_experience = safe_stoi(value);
        getline(ss, value, ','); p.order = safe_stoi(value);
        getline(ss, value, ','); p.is_default = safe_stoi(value);

        pokemon_data.push_back(p);
        if (p.id != INT_MAX) {
            pokemon_by_id[p.id] = p;
        }
    }
}

void parse_moves()
{
    moves_data.clear();
    move_by_id.clear();

    const string file_path = get_database_path() + "/pokedex/data/csv/moves.csv";
    ifstream file(file_path.c_str());
    if (!file.is_open()) {
        cerr << "Failed to open moves.csv\n";
        return;
    }

    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        Move m;

        getline(ss, value, ','); m.id = safe_stoi(value);
        getline(ss, value, ','); m.identifier = value;
        getline(ss, value, ','); m.generation_id = safe_stoi(value);
        getline(ss, value, ','); m.type_id = safe_stoi(value);
        getline(ss, value, ','); m.power = safe_stoi(value);
        getline(ss, value, ','); m.pp = safe_stoi(value);
        getline(ss, value, ','); m.accuracy = safe_stoi(value);
        getline(ss, value, ','); m.priority = safe_stoi(value);
        getline(ss, value, ','); m.target_id = safe_stoi(value);
        getline(ss, value, ','); m.damage_class_id = safe_stoi(value);
        getline(ss, value, ','); m.effect_id = safe_stoi(value);
        getline(ss, value, ','); m.effect_chance = safe_stoi(value);

        moves_data.push_back(m);
        if (m.id != INT_MAX) {
            move_by_id[m.id] = m;
        }
    }
}

void parse_pokemon_moves()
{
    pokemon_moves_data.clear();
    moves_by_pokemon.clear();

    const string file_path = get_database_path() + "/pokedex/data/csv/pokemon_moves.csv";
    ifstream file(file_path.c_str());
    if (!file.is_open()) {
        cerr << "Failed to open pokemon_moves.csv\n";
        return;
    }

    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        PokemonMove pm;

        getline(ss, value, ','); pm.pokemon_id = safe_stoi(value);
        getline(ss, value, ','); pm.version_group_id = safe_stoi(value);
        getline(ss, value, ','); pm.move_id = safe_stoi(value);
        getline(ss, value, ','); pm.pokemon_move_method_id = safe_stoi(value);
        getline(ss, value, ','); pm.level = safe_stoi(value);
        getline(ss, value, ','); pm.order = safe_stoi(value);

        pokemon_moves_data.push_back(pm);
        if (pm.pokemon_id != INT_MAX) {
            moves_by_pokemon[pm.pokemon_id].push_back(pm);
        }
    }
}

void parse_pokemon_species() {}
void parse_experience() {}
void parse_type_names() {}
void parse_stats() {}

void parse_pokemon_stats()
{
    pokemon_stats_by_pokemon.clear();

    const string file_path = get_database_path() + "/pokedex/data/csv/pokemon_stats.csv";
    ifstream file(file_path.c_str());
    if (!file.is_open()) {
        cerr << "Failed to open pokemon_stats.csv\n";
        return;
    }

    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        PokemonStat ps;

        getline(ss, value, ','); ps.pokemon_id = safe_stoi(value);
        getline(ss, value, ','); ps.stat_id = safe_stoi(value);
        getline(ss, value, ','); ps.base_stat = safe_stoi(value);
        getline(ss, value, ','); ps.effort = safe_stoi(value);

        if (ps.pokemon_id == INT_MAX || ps.stat_id == INT_MAX ||
            ps.base_stat == INT_MAX) {
            continue;
        }

        PokemonBaseStats &stats = pokemon_stats_by_pokemon[ps.pokemon_id];
        switch (ps.stat_id) {
        case 1: stats.hp = ps.base_stat; break;
        case 2: stats.attack = ps.base_stat; break;
        case 3: stats.defense = ps.base_stat; break;
        case 4: stats.sp_attack = ps.base_stat; break;
        case 5: stats.sp_defense = ps.base_stat; break;
        case 6: stats.speed = ps.base_stat; break;
        default: break;
        }
    }
}

void parse_pokemon_types()
{
    pokemon_types_by_pokemon.clear();

    const string file_path = get_database_path() + "/pokedex/data/csv/pokemon_types.csv";
    ifstream file(file_path.c_str());
    if (!file.is_open()) {
        cerr << "Failed to open pokemon_types.csv\n";
        return;
    }

    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        PokemonType pt;

        getline(ss, value, ','); pt.pokemon_id = safe_stoi(value);
        getline(ss, value, ','); pt.type_id = safe_stoi(value);
        getline(ss, value, ','); pt.slot = safe_stoi(value);

        if (pt.pokemon_id == INT_MAX || pt.type_id == INT_MAX) {
            continue;
        }

        pokemon_types_by_pokemon[pt.pokemon_id].push_back(pt.type_id);
    }
}

void load_pokedex_data()
{
    if (pokedex_loaded) {
        return;
    }

    parse_pokemon();
    parse_moves();
    parse_pokemon_moves();
    parse_pokemon_stats();
    parse_pokemon_types();

    pokedex_loaded = true;
}

const Pokemon *get_pokemon_by_id(int id)
{
    load_pokedex_data();
    unordered_map<int, Pokemon>::const_iterator it = pokemon_by_id.find(id);
    return (it == pokemon_by_id.end()) ? NULL : &it->second;
}

const Move *get_move_by_id(int id)
{
    load_pokedex_data();
    unordered_map<int, Move>::const_iterator it = move_by_id.find(id);
    return (it == move_by_id.end()) ? NULL : &it->second;
}

vector<Move> get_level_up_moves_for_pokemon(int pokemon_id, int level)
{
    load_pokedex_data();

    vector<Move> result;
    unordered_map<int, int> move_to_level;
    unordered_map<int, vector<PokemonMove> >::const_iterator it =
        moves_by_pokemon.find(pokemon_id);

    if (it == moves_by_pokemon.end()) {
        return result;
    }

    const vector<PokemonMove> &entries = it->second;
    size_t i;
    for (i = 0; i < entries.size(); i++) {
        const PokemonMove &pm = entries[i];
        if (pm.pokemon_move_method_id != 1 ||
            pm.move_id == INT_MAX ||
            pm.level == INT_MAX ||
            pm.level > level) {
            continue;
        }

        unordered_map<int, int>::iterator found = move_to_level.find(pm.move_id);
        if (found == move_to_level.end() || found->second < pm.level) {
            move_to_level[pm.move_id] = pm.level;
        }
    }

    unordered_map<int, int>::const_iterator move_it;
    for (move_it = move_to_level.begin(); move_it != move_to_level.end(); ++move_it) {
        const Move *m = get_move_by_id(move_it->first);
        if (m) {
            result.push_back(*m);
        }
    }

    return result;
}

vector<int> get_type_ids_for_pokemon(int pokemon_id)
{
    load_pokedex_data();
    unordered_map<int, vector<int> >::const_iterator it =
        pokemon_types_by_pokemon.find(pokemon_id);
    return (it == pokemon_types_by_pokemon.end()) ? vector<int>() : it->second;
}

PokemonBaseStats get_base_stats_for_pokemon(int pokemon_id)
{
    load_pokedex_data();
    unordered_map<int, PokemonBaseStats>::const_iterator it =
        pokemon_stats_by_pokemon.find(pokemon_id);
    return (it == pokemon_stats_by_pokemon.end()) ? PokemonBaseStats() : it->second;
}
