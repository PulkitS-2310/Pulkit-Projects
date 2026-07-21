#include "csv_parser.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <climits>
#include <cstdlib>
#include <unistd.h>

using namespace std;

string get_database_path() {
    if (access("/share/cs327/pokedex", F_OK) == 0) {
        return "/share/cs327/pokedex";
    }

    char *home = getenv("HOME");
    string path = string(home) + "/.poke327/pokedex";

    if (access(path.c_str(), F_OK) == 0) {
        return path;
    }

    cerr << "Database not found\n";
    exit(1);
}

// -------------------- POKEMON --------------------
void parse_pokemon() {
    string base = get_database_path();
    string file_path = base + "/pokedex/data/csv/pokemon.csv";

    ifstream file(file_path);
    if (!file.is_open()) {
        cerr << "Failed to open pokemon.csv\n";
        return;
    }

    vector<Pokemon> data;
    string line;

    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        Pokemon p;

        getline(ss, value, ','); p.id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.identifier = value;
        getline(ss, value, ','); p.species_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.height = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.weight = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.base_experience = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.order = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); p.is_default = value.empty() ? INT_MAX : stoi(value);

        data.push_back(p);
    }

    for (const auto &p : data) {
        if (p.id != INT_MAX) cout << p.id << " ";
        cout << p.identifier << " ";
        if (p.species_id != INT_MAX) cout << p.species_id << " ";
        if (p.height != INT_MAX) cout << p.height << " ";
        if (p.weight != INT_MAX) cout << p.weight << " ";
        if (p.base_experience != INT_MAX) cout << p.base_experience << " ";
        if (p.order != INT_MAX) cout << p.order << " ";
        if (p.is_default != INT_MAX) cout << p.is_default;
        cout << "\n";
    }
}

// -------------------- MOVES --------------------
void parse_moves() {
    string base = get_database_path();
    string file_path = base + "/pokedex/data/csv/moves.csv";

    ifstream file(file_path);
    if (!file.is_open()) {
        cerr << "Failed to open moves.csv\n";
        return;
    }

    vector<Move> data;
    string line;

    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        string value;
        Move m;

        getline(ss, value, ','); m.id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.identifier = value;
        getline(ss, value, ','); m.generation_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.type_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.power = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.pp = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.accuracy = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.priority = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.target_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.damage_class_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.effect_id = value.empty() ? INT_MAX : stoi(value);
        getline(ss, value, ','); m.effect_chance = value.empty() ? INT_MAX : stoi(value);

        data.push_back(m);
    }

    for (const auto &m : data) {
        if (m.id != INT_MAX) cout << m.id << " ";
        cout << m.identifier << " ";
        if (m.generation_id != INT_MAX) cout << m.generation_id << " ";
        if (m.type_id != INT_MAX) cout << m.type_id << " ";
        if (m.power != INT_MAX) cout << m.power << " ";
        if (m.pp != INT_MAX) cout << m.pp << " ";
        if (m.accuracy != INT_MAX) cout << m.accuracy << " ";
        if (m.priority != INT_MAX) cout << m.priority << " ";
        if (m.target_id != INT_MAX) cout << m.target_id << " ";
        if (m.damage_class_id != INT_MAX) cout << m.damage_class_id << " ";
        if (m.effect_id != INT_MAX) cout << m.effect_id << " ";
        if (m.effect_chance != INT_MAX) cout << m.effect_chance;
        cout << "\n";
    }
}

// -------------------- GENERIC PRINT PARSERS --------------------
void parse_pokemon_moves() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/pokemon_moves.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}

void parse_pokemon_species() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/pokemon_species.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}

void parse_experience() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/experience.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}

void parse_type_names() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/type_names.csv");
    string line;
    getline(file, line);

    while (getline(file, line)) {
        stringstream ss(line);
        vector<string> row;
        string value;

        while (getline(ss, value, ',')) row.push_back(value);

        if (row.size() > 1 && row[1] == "9") {
            for (auto &v : row) cout << (v.empty() ? " " : v) << " ";
            cout << "\n";
        }
    }
}

void parse_pokemon_stats() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/pokemon_stats.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}

void parse_stats() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/stats.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}

void parse_pokemon_types() {
    string base = get_database_path();
    ifstream file(base + "/pokedex/data/csv/pokemon_types.csv");
    string line, value;
    getline(file, line);
    while (getline(file, line)) {
        stringstream ss(line);
        while (getline(ss, value, ',')) cout << (value.empty() ? " " : value) << " ";
        cout << "\n";
    }
}