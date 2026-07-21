#include "pokemon.h"
#include "csv_parser.h"
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <limits>
#include "io.h"

extern std::vector<Pokemon> pokemon_data; // we'll create this

// Helper: generate IV
int rand_iv() {
    return rand() % 16;
}

// Calculate stat
int calculate_stat(int base, int iv, int level, bool is_hp) {
    if (is_hp) {
        return (((base + iv) * 2 * level) / 100) + level + 10;
    } else {
        return (((base + iv) * 2 * level) / 100) + 5;
    }
}


PokemonInstance generate_random_pokemon(int distance) {
    PokemonInstance p;


    int idx = rand() % pokemon_data.size();
    Pokemon base = pokemon_data[idx];

    p.id = base.id;
    p.name = base.identifier;


    int min_level, max_level;

    if (distance <= 200) {
        min_level = 1;
        max_level = distance / 2 > 0 ? distance / 2 : 1;
    } else {
        min_level = (distance - 200) / 2;
        max_level = 100;
    }

    p.level = rand() % (max_level - min_level + 1) + min_level;

    // IVs
    p.iv_hp = rand_iv();
    p.iv_attack = rand_iv();
    p.iv_defense = rand_iv();
    p.iv_speed = rand_iv();
    p.iv_sp_attack = rand_iv();
    p.iv_sp_defense = rand_iv();

    int base_stat = 50;

    p.hp = calculate_stat(base_stat, p.iv_hp, p.level, true);
    p.attack = calculate_stat(base_stat, p.iv_attack, p.level, false);
    p.defense = calculate_stat(base_stat, p.iv_defense, p.level, false);
    p.speed = calculate_stat(base_stat, p.iv_speed, p.level, false);
    p.sp_attack = calculate_stat(base_stat, p.iv_sp_attack, p.level, false);
    p.sp_defense = calculate_stat(base_stat, p.iv_sp_defense, p.level, false);

    // Placeholder moves
    p.moves.push_back("Tackle");
    p.moves.push_back("Growl");

    return p;
}

void give_starter_pokemon() {
    PokemonInstance p1 = generate_random_pokemon(0);
    PokemonInstance p2 = generate_random_pokemon(0);
    PokemonInstance p3 = generate_random_pokemon(0);

    std::cout << "Choose your starter Pokemon:\n";
    std::cout << "1. " << p1.name << " (Level " << p1.level << ")\n";
    std::cout << "2. " << p2.name << " (Level " << p2.level << ")\n";
    std::cout << "3. " << p3.name << " (Level " << p3.level << ")\n";

    int choice;
    std::cin >> choice;
    
    PokemonInstance chosen;

    if (choice == 1) chosen = p1;
    else if (choice == 2) chosen = p2;
    else chosen = p3;

    std::cout << "You chose " << chosen.name << "!\n";
}
#include <ncurses.h>

void trigger_wild_encounter() {
    clear();

    PokemonInstance wild = generate_random_pokemon(50);

    mvprintw(0, 0, "A wild %s appeared! (Level %d)",
             wild.name.c_str(), wild.level);

    mvprintw(1, 0, "HP: %d", wild.hp);
    mvprintw(2, 0, "Attack: %d", wild.attack);
    mvprintw(3, 0, "Defense: %d", wild.defense);
    mvprintw(4, 0, "Speed: %d", wild.speed);
    mvprintw(5, 0, "Sp. Attack: %d", wild.sp_attack);
    mvprintw(6, 0, "Sp. Defense: %d", wild.sp_defense);

    mvprintw(8, 0, "Moves: ");

    int col = 7;
    for (size_t i = 0; i < wild.moves.size(); i++) {
        mvprintw(8, col, "%s", wild.moves[i].c_str());
        col += wild.moves[i].length();

        if (i + 1 < wild.moves.size()) {
            mvprintw(8, col, ", ");
            col += 2;
        }
    }

    mvprintw(10, 0, "Press any key to continue...");
    refresh();

    getch();
}