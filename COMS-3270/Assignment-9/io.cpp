#include <unistd.h>
#include <ncurses.h>
#include <ctype.h>
#include <stdlib.h>
#include <limits.h>
#include <cstdarg>
#include <cstdio>
#include <string>
#include <vector>

#include "io.h"
#include "character.h"
#include "poke327.h"
#include "pokemon.h"

#define TRAINER_LIST_FIELD_WIDTH 46

typedef struct io_message {
  /* Will print " --more-- " at end of line when another message follows. *
   * Leave 10 extra spaces for that.                                      */
  char msg[71];
  struct io_message *next;
} io_message_t;

static io_message_t *io_head, *io_tail;

void io_init_terminal(void)
{
  initscr();
  raw();
  noecho();
  curs_set(0);
  keypad(stdscr, TRUE);
  start_color();
  init_pair(COLOR_RED, COLOR_RED, COLOR_BLACK);
  init_pair(COLOR_GREEN, COLOR_GREEN, COLOR_BLACK);
  init_pair(COLOR_YELLOW, COLOR_YELLOW, COLOR_BLACK);
  init_pair(COLOR_BLUE, COLOR_BLUE, COLOR_BLACK);
  init_pair(COLOR_MAGENTA, COLOR_MAGENTA, COLOR_BLACK);
  init_pair(COLOR_CYAN, COLOR_CYAN, COLOR_BLACK);
  init_pair(COLOR_WHITE, COLOR_WHITE, COLOR_BLACK);
}

void io_reset_terminal(void)
{
  endwin();

  while (io_head) {
    io_tail = io_head;
    io_head = io_head->next;
    free(io_tail);
  }
  io_tail = NULL;
}

void io_queue_message(const char *format, ...)
{
  io_message_t *tmp;
  va_list ap;

  if (!(tmp = (io_message_t *) malloc(sizeof (*tmp)))) {
    perror("malloc");
    exit(1);
  }

  tmp->next = NULL;

  va_start(ap, format);

  vsnprintf(tmp->msg, sizeof (tmp->msg), format, ap);

  va_end(ap);

  if (!io_head) {
    io_head = io_tail = tmp;
  } else {
    io_tail->next = tmp;
    io_tail = tmp;
  }
}

static void io_print_message_queue(uint32_t y, uint32_t x)
{
  while (io_head) {
    io_tail = io_head;
    attron(COLOR_PAIR(COLOR_CYAN));
    mvprintw(y, x, "%-80s", io_head->msg);
    attroff(COLOR_PAIR(COLOR_CYAN));
    io_head = io_head->next;
    if (io_head) {
      attron(COLOR_PAIR(COLOR_CYAN));
      mvprintw(y, x + 70, "%10s", " --more-- ");
      attroff(COLOR_PAIR(COLOR_CYAN));
      refresh();
      getch();
    }
    free(io_tail);
  }
  io_tail = NULL;
}

/**************************************************************************
 * Compares trainer distances from the PC according to the rival distance *
 * map.  This gives the approximate distance that the PC must travel to   *
 * get to the trainer (doesn't account for crossing buildings).  This is  *
 * not the distance from the NPC to the PC unless the NPC is a rival.     *
 *                                                                        *
 * Not a bug.                                                             *
 **************************************************************************/
static int compare_trainer_distance(const void *v1, const void *v2)
{
  const character *const *c1 = (const character * const *) v1;
  const character *const *c2 = (const character * const *) v2;

  return (world.rival_dist[(*c1)->pos[dim_y]][(*c1)->pos[dim_x]] -
          world.rival_dist[(*c2)->pos[dim_y]][(*c2)->pos[dim_x]]);
}

static character *io_nearest_visible_trainer()
{
  character **c, *n;
  uint32_t x, y, count;

  c = (character **) malloc(world.cur_map->num_trainers * sizeof (*c));

  /* Get a linear list of trainers */
  for (count = 0, y = 1; y < MAP_Y - 1; y++) {
    for (x = 1; x < MAP_X - 1; x++) {
      if (world.cur_map->cmap[y][x] && world.cur_map->cmap[y][x] !=
          &world.pc) {
        c[count++] = world.cur_map->cmap[y][x];
      }
    }
  }

  /* Sort it by distance from PC */
  qsort(c, count, sizeof (*c), compare_trainer_distance);

  n = c[0];

  free(c);

  return n;
}

void io_display()
{
  uint32_t y, x;
  character *c;

  clear();
  for (y = 0; y < MAP_Y; y++) {
    for (x = 0; x < MAP_X; x++) {
      if (world.cur_map->cmap[y][x]) {
        mvaddch(y + 1, x, world.cur_map->cmap[y][x]->symbol);
      } else {
        switch (world.cur_map->map[y][x]) {
        case ter_boulder:
          attron(COLOR_PAIR(COLOR_MAGENTA));
          mvaddch(y + 1, x, BOULDER_SYMBOL);
          attroff(COLOR_PAIR(COLOR_MAGENTA));
          break;
        case ter_mountain:
          attron(COLOR_PAIR(COLOR_MAGENTA));
          mvaddch(y + 1, x, MOUNTAIN_SYMBOL);
          attroff(COLOR_PAIR(COLOR_MAGENTA));
          break;
        case ter_tree:
          attron(COLOR_PAIR(COLOR_GREEN));
          mvaddch(y + 1, x, TREE_SYMBOL);
          attroff(COLOR_PAIR(COLOR_GREEN));
          break;
        case ter_forest:
          attron(COLOR_PAIR(COLOR_GREEN));
          mvaddch(y + 1, x, FOREST_SYMBOL);
          attroff(COLOR_PAIR(COLOR_GREEN));
          break;
        case ter_path:
        case ter_bailey:
          attron(COLOR_PAIR(COLOR_YELLOW));
          mvaddch(y + 1, x, PATH_SYMBOL);
          attroff(COLOR_PAIR(COLOR_YELLOW));
          break;
        case ter_gate:
          attron(COLOR_PAIR(COLOR_YELLOW));
          mvaddch(y + 1, x, GATE_SYMBOL);
          attroff(COLOR_PAIR(COLOR_YELLOW));
          break;
        case ter_mart:
          attron(COLOR_PAIR(COLOR_BLUE));
          mvaddch(y + 1, x, POKEMART_SYMBOL);
          attroff(COLOR_PAIR(COLOR_BLUE));
          break;
        case ter_center:
          attron(COLOR_PAIR(COLOR_RED));
          mvaddch(y + 1, x, POKEMON_CENTER_SYMBOL);
          attroff(COLOR_PAIR(COLOR_RED));
          break;
        case ter_grass:
          attron(COLOR_PAIR(COLOR_GREEN));
          mvaddch(y + 1, x, TALL_GRASS_SYMBOL);
          attroff(COLOR_PAIR(COLOR_GREEN));
          break;
        case ter_clearing:
          attron(COLOR_PAIR(COLOR_GREEN));
          mvaddch(y + 1, x, SHORT_GRASS_SYMBOL);
          attroff(COLOR_PAIR(COLOR_GREEN));
          break;
        case ter_water:
          attron(COLOR_PAIR(COLOR_CYAN));
          mvaddch(y + 1, x, WATER_SYMBOL);
          attroff(COLOR_PAIR(COLOR_CYAN));
          break;
        default:
 /* Use zero as an error symbol, since it stands out somewhat, and it's *
  * not otherwise used.                                                 */
          attron(COLOR_PAIR(COLOR_CYAN));
          mvaddch(y + 1, x, ERROR_SYMBOL);
          attroff(COLOR_PAIR(COLOR_CYAN)); 
       }
      }
    }
  }

  mvprintw(23, 1, "PC position is (%2d,%2d) on map %d%cx%d%c.",
           world.pc.pos[dim_x],
           world.pc.pos[dim_y],
           abs(world.cur_idx[dim_x] - (WORLD_SIZE / 2)),
           world.cur_idx[dim_x] - (WORLD_SIZE / 2) >= 0 ? 'E' : 'W',
           abs(world.cur_idx[dim_y] - (WORLD_SIZE / 2)),
           world.cur_idx[dim_y] - (WORLD_SIZE / 2) <= 0 ? 'N' : 'S');
  mvprintw(22, 1, "%d known %s.", world.cur_map->num_trainers,
           world.cur_map->num_trainers > 1 ? "trainers" : "trainer");
  mvprintw(22, 30, "Nearest visible trainer: ");
  if ((c = io_nearest_visible_trainer())) {
    attron(COLOR_PAIR(COLOR_RED));
    mvprintw(22, 55, "%c at vector %d%cx%d%c.",
             c->symbol,
             abs(c->pos[dim_y] - world.pc.pos[dim_y]),
             ((c->pos[dim_y] - world.pc.pos[dim_y]) <= 0 ?
              'N' : 'S'),
             abs(c->pos[dim_x] - world.pc.pos[dim_x]),
             ((c->pos[dim_x] - world.pc.pos[dim_x]) <= 0 ?
              'W' : 'E'));
    attroff(COLOR_PAIR(COLOR_RED));
  } else {
    attron(COLOR_PAIR(COLOR_BLUE));
    mvprintw(22, 55, "NONE.");
    attroff(COLOR_PAIR(COLOR_BLUE));
  }

  io_print_message_queue(0, 0);

  refresh();
}

uint32_t io_teleport_pc(pair_t dest)
{
  /* Just for fun. And debugging.  Mostly debugging. */

  do {
    dest[dim_x] = rand_range(1, MAP_X - 2);
    dest[dim_y] = rand_range(1, MAP_Y - 2);
  } while (world.cur_map->cmap[dest[dim_y]][dest[dim_x]]                  ||
           move_cost[char_pc][world.cur_map->map[dest[dim_y]]
                                                [dest[dim_x]]] ==
             DIJKSTRA_PATH_MAX                                            ||
           world.rival_dist[dest[dim_y]][dest[dim_x]] < 0);

  return 0;
}

static void io_scroll_trainer_list(char (*s)[TRAINER_LIST_FIELD_WIDTH],
                                   uint32_t count)
{
  uint32_t offset;
  uint32_t i;

  offset = 0;

  while (1) {
    for (i = 0; i < 13; i++) {
      mvprintw(i + 6, 19, " %-40s ", s[i + offset]);
    }
    switch (getch()) {
    case KEY_UP:
      if (offset) {
        offset--;
      }
      break;
    case KEY_DOWN:
      if (offset < (count - 13)) {
        offset++;
      }
      break;
    case 27:
      return;
    }

  }
}

static void io_list_trainers_display(npc **c, uint32_t count)
{
  uint32_t i;
  char (*s)[TRAINER_LIST_FIELD_WIDTH]; /* pointer to array of 40 char */

  s = (char (*)[TRAINER_LIST_FIELD_WIDTH]) malloc(count * sizeof (*s));

  mvprintw(3, 19, " %-40s ", "");
  /* Borrow the first element of our array for this string: */
  snprintf(s[0], TRAINER_LIST_FIELD_WIDTH, "You know of %d trainers:", count);
  mvprintw(4, 19, " %-40s ", *s);
  mvprintw(5, 19, " %-40s ", "");

  for (i = 0; i < count; i++) {
    snprintf(s[i], TRAINER_LIST_FIELD_WIDTH, "%16s %c: %2d %s by %2d %s",
             char_type_name[c[i]->ctype],
             c[i]->symbol,
             abs(c[i]->pos[dim_y] - world.pc.pos[dim_y]),
             ((c[i]->pos[dim_y] - world.pc.pos[dim_y]) <= 0 ?
              "North" : "South"),
             abs(c[i]->pos[dim_x] - world.pc.pos[dim_x]),
             ((c[i]->pos[dim_x] - world.pc.pos[dim_x]) <= 0 ?
              "West" : "East"));
    if (count <= 13) {
      /* Handle the non-scrolling case right here. *
       * Scrolling in another function.            */
      mvprintw(i + 6, 19, " %-40s ", s[i]);
    }
  }

  if (count <= 13) {
    mvprintw(count + 6, 19, " %-40s ", "");
    mvprintw(count + 7, 19, " %-40s ", "Hit escape to continue.");
    while (getch() != 27 /* escape */)
      ;
  } else {
    mvprintw(19, 19, " %-40s ", "");
    mvprintw(20, 19, " %-40s ",
             "Arrows to scroll, escape to continue.");
    io_scroll_trainer_list(s, count);
  }

  free(s);
}

static void io_list_trainers()
{
  npc **c;
  uint32_t x, y, count;

  c = (npc **) malloc(world.cur_map->num_trainers * sizeof (*c));

  /* Get a linear list of trainers */
  for (count = 0, y = 1; y < MAP_Y - 1; y++) {
    for (x = 1; x < MAP_X - 1; x++) {
      if (world.cur_map->cmap[y][x] && world.cur_map->cmap[y][x] !=
          &world.pc) {
        c[count++] = dynamic_cast <npc *> (world.cur_map->cmap[y][x]);
      }
    }
  }

  /* Sort it by distance from PC */
  qsort(c, count, sizeof (*c), compare_trainer_distance);

  /* Display it */
  io_list_trainers_display(c, count);
  free(c);

  /* And redraw the map */
  io_display();
}

void io_pokemart()
{
  restore_pc_supplies();
  clear();
  mvprintw(0, 0, "Welcome to the Pokemart!");
  mvprintw(1, 0, "Your supplies have been restored to the default amounts.");
  mvprintw(3, 0, "Potions: %d  Revives: %d  Pokeballs: %d",
           world.pc.potions, world.pc.revives, world.pc.pokeballs);
  mvprintw(5, 0, "Press any key to continue...");
  refresh();
  getch();
}

void io_pokemon_center()
{
  heal_party(world.pc.party);
  clear();
  mvprintw(0, 0, "Welcome to the Pokemon Center!");
  mvprintw(1, 0, "Your Pokemon have been fully healed.");
  mvprintw(3, 0, "Press any key to continue...");
  refresh();
  getch();
}

enum battle_action_type {
  battle_action_none,
  battle_action_move,
  battle_action_potion,
  battle_action_revive,
  battle_action_pokeball,
  battle_action_run,
  battle_action_switch
};

typedef struct battle_action {
  battle_action_type type;
  int move_index;
  int target_index;
} battle_action_t;

static std::string move_name(const Move &move)
{
  return move.identifier.empty() ? std::string("move") : move.identifier;
}

static void battle_pause_message(const std::string &message)
{
  clear();
  mvprintw(0, 0, "%s", message.c_str());
  mvprintw(2, 0, "Press any key to continue...");
  refresh();
  getch();
}

static void draw_battle_screen(const PokemonInstance &player_pokemon,
                               const PokemonInstance &enemy_pokemon,
                               const char *enemy_label,
                               bool is_wild)
{
  clear();
  mvprintw(0, 0, "%s %s  Lv:%d",
           is_wild ? "Wild" : "Enemy",
           enemy_label,
           enemy_pokemon.level);
  mvprintw(1, 0, "HP: %d/%d", enemy_pokemon.current_hp, enemy_pokemon.max_hp);

  mvprintw(4, 0, "Go! %s  Lv:%d",
           player_pokemon.name.c_str(),
           player_pokemon.level);
  mvprintw(5, 0, "HP: %d/%d", player_pokemon.current_hp, player_pokemon.max_hp);

  mvprintw(8, 0, "Items -> Potions: %d  Revives: %d  Pokeballs: %d",
           world.pc.potions, world.pc.revives, world.pc.pokeballs);
}

static std::string party_prompt_for_count(size_t count)
{
  if (count <= 1) {
    return "Press 1, or ESC to cancel.";
  }

  return "Press 1-" + std::to_string(count) + ", or ESC to cancel.";
}

static int choose_party_index(const std::vector<PokemonInstance> &party,
                              bool require_knocked_out,
                              int disallow_index,
                              const char *title)
{
  int key;

  while (1) {
    clear();
    mvprintw(0, 0, "%s", title);
    mvprintw(1, 0, "%s", party_prompt_for_count(party.size()).c_str());

    for (size_t i = 0; i < party.size(); i++) {
      mvprintw(static_cast<int>(i) + 3, 0,
               "%zu. %-14s Lv:%3d HP:%3d/%3d %s",
               i + 1,
               party[i].name.c_str(),
               party[i].level,
               party[i].current_hp,
               party[i].max_hp,
               party[i].is_knocked_out() ? "(KO)" : "");
    }

    refresh();
    key = getch();

    if (key == 27) {
      return -1;
    }

    if (key >= '1' && key <= '6') {
      int idx = key - '1';
      if (idx >= static_cast<int>(party.size())) {
        continue;
      }
      if (idx == disallow_index) {
        battle_pause_message("That Pokemon is already active.");
        continue;
      }
      if (require_knocked_out && !party[idx].is_knocked_out()) {
        battle_pause_message("Choose a knocked out Pokemon.");
        continue;
      }
      if (!require_knocked_out && party[idx].is_knocked_out()) {
        battle_pause_message("Choose a Pokemon that can still battle.");
        continue;
      }
      return idx;
    }
  }
}

static std::string move_prompt_for_count(size_t count)
{
  if (count <= 1) {
    return "Press 1, or ESC to go back.";
  }

  return "Press 1-" + std::to_string(count) + ", or ESC to go back.";
}
static battle_action_t choose_player_action(bool is_wild,
                                            const PokemonInstance &enemy_pokemon)
{
  battle_action_t action;
  action.type = battle_action_none;
  action.move_index = -1;
  action.target_index = -1;

  while (1) {
    const PokemonInstance &active = world.pc.party[world.pc.active_pokemon];
    draw_battle_screen(active, enemy_pokemon, enemy_pokemon.name.c_str(), is_wild);

    if (active.is_knocked_out()) {
      mvprintw(10, 0, "Your active Pokemon is knocked out.");
      mvprintw(11, 0, "Choose 4 to switch Pokemon.");
    } else {
      mvprintw(10, 0, "1. Fight   2. Bag   3. Run   4. Pokemon");
    }

    refresh();

    switch (getch()) {
        case '1':
      if (active.is_knocked_out()) {
        battle_pause_message("You must switch Pokemon first.");
        break;
      }
      while (1) {
        clear();
        mvprintw(0, 0, "Choose a move for %s", active.name.c_str());
        mvprintw(1, 0, "%s", move_prompt_for_count(active.moves.size()).c_str());

        for (size_t i = 0; i < active.moves.size(); i++) {
          mvprintw(static_cast<int>(i) + 3, 0,
                   "%zu. %s (Power %d, Accuracy %d, Priority %d)",
                   i + 1,
                   move_name(active.moves[i]).c_str(),
                   active.moves[i].power == INT_MAX ? 0 : active.moves[i].power,
                   active.moves[i].accuracy == INT_MAX ? 100 : active.moves[i].accuracy,
                   active.moves[i].priority == INT_MAX ? 0 : active.moves[i].priority);
        }

        mvprintw(9, 0, "Available moves: %zu", active.moves.size());
        refresh();

        int key = getch();

        if (key == 27) {
          break;
        }

        if (key >= '1' && key < ('1' + static_cast<int>(active.moves.size()))) {
          int idx = key - '1';
          action.type = battle_action_move;
          action.move_index = idx;
          return action;
        }
      }
      break;
    case '2':
      while (1) {
        clear();
        mvprintw(0, 0, "Bag");
        mvprintw(1, 0, "1. Potion   2. Revive   3. Pokeball   ESC. Back");
        mvprintw(3, 0, "Potions: %d  Revives: %d  Pokeballs: %d",
                 world.pc.potions, world.pc.revives, world.pc.pokeballs);
        refresh();
        int key = getch();
        if (key == 27) {
          break;
        }
        if (key == '1') {
          if (world.pc.potions <= 0) {
            battle_pause_message("You do not have any potions.");
            continue;
          }
          int target = choose_party_index(world.pc.party, false, -1,
                                          "Use Potion on which Pokemon?");
          if (target >= 0) {
            action.type = battle_action_potion;
            action.target_index = target;
            return action;
          }
        } else if (key == '2') {
          if (world.pc.revives <= 0) {
            battle_pause_message("You do not have any revives.");
            continue;
          }
          int target = choose_party_index(world.pc.party, true, -1,
                                          "Use Revive on which Pokemon?");
          if (target >= 0) {
            action.type = battle_action_revive;
            action.target_index = target;
            return action;
          }
        } else if (key == '3') {
          if (!is_wild) {
            battle_pause_message("Pokeballs can only be used against wild Pokemon.");
            continue;
          }
          if (world.pc.pokeballs <= 0) {
            battle_pause_message("You do not have any Pokeballs.");
            continue;
          }
          action.type = battle_action_pokeball;
          return action;
        }
      }
      break;
    case '3':
      if (!is_wild) {
        battle_pause_message("You cannot run from a trainer battle.");
        break;
      }
      action.type = battle_action_run;
      return action;
    case '4':
      {
        int target = choose_party_index(world.pc.party, false,
                                        world.pc.active_pokemon,
                                        "Switch to which Pokemon?");
        if (target >= 0) {
          action.type = battle_action_switch;
          action.target_index = target;
          return action;
        }
      }
      break;
    default:
      break;
    }
  }
}

static battle_action_t choose_enemy_action(npc *trainer,
                                           std::vector<PokemonInstance> &enemy_party,
                                           int enemy_active)
{
  battle_action_t action;
  action.type = battle_action_none;
  action.move_index = -1;
  action.target_index = -1;

  if (enemy_active < 0 || enemy_active >= static_cast<int>(enemy_party.size())) {
    return action;
  }

  if (enemy_party[enemy_active].is_knocked_out()) {
    int replacement = first_usable_pokemon(enemy_party);
    if (replacement >= 0) {
      action.type = battle_action_switch;
      action.target_index = replacement;
    }
    return action;
  }

  if (enemy_party[enemy_active].moves.empty()) {
    enemy_party[enemy_active].moves.push_back(get_fallback_move());
  }

  action.type = battle_action_move;
  action.move_index = rand() % enemy_party[enemy_active].moves.size();
  (void) trainer;
  return action;
}

static int player_goes_first(const battle_action_t &player_action,
                             const battle_action_t &enemy_action,
                             const PokemonInstance &player_pokemon,
                             const PokemonInstance &enemy_pokemon)
{
  if (player_action.type != battle_action_move &&
      enemy_action.type == battle_action_move) {
    return 1;
  }

  if (player_action.type == battle_action_move &&
      enemy_action.type != battle_action_move) {
    return 0;
  }

  if (player_action.type != battle_action_move &&
      enemy_action.type != battle_action_move) {
    return 1;
  }

  const Move &player_move = player_pokemon.moves[player_action.move_index];
  const Move &enemy_move = enemy_pokemon.moves[enemy_action.move_index];
  int player_priority = player_move.priority == INT_MAX ? 0 : player_move.priority;
  int enemy_priority = enemy_move.priority == INT_MAX ? 0 : enemy_move.priority;

  if (player_priority != enemy_priority) {
    return player_priority > enemy_priority;
  }

  if (player_pokemon.speed != enemy_pokemon.speed) {
    return player_pokemon.speed > enemy_pokemon.speed;
  }

  return rand() % 2;
}

typedef struct battle_state {
  int battle_over;
  int player_won;
  int player_ran;
  int wild_caught;
  int wild_fled;
} battle_state_t;

static void apply_player_action(const battle_action_t &action,
                                std::vector<PokemonInstance> &enemy_party,
                                int &enemy_active,
                                int is_wild,
                                battle_state_t &state)
{
  PokemonInstance &player_pokemon = world.pc.party[world.pc.active_pokemon];

  switch (action.type) {
  case battle_action_move:
    if (player_pokemon.is_knocked_out()) {
      return;
    }
    if (!attempt_move_hit(player_pokemon.moves[action.move_index])) {
      battle_pause_message("Potion used on " +
                     world.pc.party[action.target_index].name +
                     "! HP is now " +
                     std::to_string(world.pc.party[action.target_index].current_hp) +
                     "/" +
                     std::to_string(world.pc.party[action.target_index].max_hp) + ".");
      return;
    }
    {
      PokemonInstance &enemy_pokemon = enemy_party[enemy_active];
      int damage = compute_damage(player_pokemon,
                                  enemy_pokemon,
                                  player_pokemon.moves[action.move_index]);
      enemy_pokemon.current_hp -= damage;
      if (enemy_pokemon.current_hp < 0) {
        enemy_pokemon.current_hp = 0;
      }
      std::string msg = player_pokemon.name + " used " +
                        move_name(player_pokemon.moves[action.move_index]) +
                        " for " + std::to_string(damage) + " damage!";
      if (enemy_pokemon.is_knocked_out()) {
        msg += " " + enemy_pokemon.name + " fainted!";
      }
      battle_pause_message(msg);
    }
    break;
  case battle_action_potion:
    if (world.pc.potions <= 0) {
      return;
    }
    if (action.target_index < 0 ||
        action.target_index >= static_cast<int>(world.pc.party.size())) {
      return;
    }
    if (world.pc.party[action.target_index].is_knocked_out()) {
      battle_pause_message("Potions cannot be used on knocked out Pokemon.");
      return;
    }
    world.pc.potions--;
    world.pc.party[action.target_index].current_hp += 20;
    if (world.pc.party[action.target_index].current_hp >
        world.pc.party[action.target_index].max_hp) {
      world.pc.party[action.target_index].current_hp =
          world.pc.party[action.target_index].max_hp;
    }
    battle_pause_message("Potion used on " + world.pc.party[action.target_index].name + "!");
    break;
  case battle_action_revive:
    if (world.pc.revives <= 0) {
      return;
    }
    if (action.target_index < 0 ||
        action.target_index >= static_cast<int>(world.pc.party.size())) {
      return;
    }
    if (!world.pc.party[action.target_index].is_knocked_out()) {
      battle_pause_message("That Pokemon does not need a revive.");
      return;
    }
    world.pc.revives--;
    world.pc.party[action.target_index].current_hp =
        world.pc.party[action.target_index].max_hp / 2;
    if (world.pc.party[action.target_index].current_hp < 1) {
      world.pc.party[action.target_index].current_hp = 1;
    }
   battle_pause_message(world.pc.party[action.target_index].name +
                     " was revived! HP is now " +
                     std::to_string(world.pc.party[action.target_index].current_hp) +
                     "/" +
                     std::to_string(world.pc.party[action.target_index].max_hp) + ".");
    break;
  case battle_action_pokeball:
    if (!is_wild || world.pc.pokeballs <= 0) {
      return;
    }
    world.pc.pokeballs--;
    if (world.pc.party.size() < 6) {
      world.pc.party.push_back(enemy_party[enemy_active]);
      state.battle_over = 1;
      state.player_won = 1;
      state.wild_caught = 1;
      battle_pause_message("You caught the wild Pokemon!");
    } else {
      state.battle_over = 1;
      state.wild_fled = 1;
      battle_pause_message("Your party is full. The wild Pokemon fled!");
    }
    break;
  case battle_action_run:
    if (rand() % 2 == 0) {
      state.battle_over = 1;
      state.player_ran = 1;
      battle_pause_message("You got away safely!");
    } else {
      battle_pause_message("Couldn't escape!");
    }
    break;
  case battle_action_switch:
    if (action.target_index >= 0 &&
        action.target_index < static_cast<int>(world.pc.party.size())) {
      world.pc.active_pokemon = action.target_index;
      battle_pause_message("Go, " + world.pc.party[action.target_index].name + "!");
    }
    break;
  default:
    break;
  }
}

static void apply_enemy_action(const battle_action_t &action,
                               std::vector<PokemonInstance> &enemy_party,
                               int &enemy_active,
                               battle_state_t &state)
{
  (void) state;

  if (enemy_active < 0 || enemy_active >= static_cast<int>(enemy_party.size())) {
    return;
  }

  switch (action.type) {
  case battle_action_switch:
    if (action.target_index >= 0 &&
        action.target_index < static_cast<int>(enemy_party.size())) {
      enemy_active = action.target_index;
      battle_pause_message("The opposing trainer sent out " +
                           enemy_party[enemy_active].name + "!");
    }
    break;
  case battle_action_move:
    {
      PokemonInstance &enemy_pokemon = enemy_party[enemy_active];
      if (enemy_pokemon.is_knocked_out()) {
        return;
      }
      if (!attempt_move_hit(enemy_pokemon.moves[action.move_index])) {
        battle_pause_message(enemy_pokemon.name + " missed!");
        return;
      }
      PokemonInstance &player_pokemon = world.pc.party[world.pc.active_pokemon];
      int damage = compute_damage(enemy_pokemon,
                                  player_pokemon,
                                  enemy_pokemon.moves[action.move_index]);
      player_pokemon.current_hp -= damage;
      if (player_pokemon.current_hp < 0) {
        player_pokemon.current_hp = 0;
      }
      std::string msg = enemy_pokemon.name + " used " +
                        move_name(enemy_pokemon.moves[action.move_index]) +
                        " for " + std::to_string(damage) + " damage!";
      if (player_pokemon.is_knocked_out()) {
        msg += " " + player_pokemon.name + " fainted!";
      }
      battle_pause_message(msg);
    }
    break;
  default:
    break;
  }
}

static void resolve_battle(std::vector<PokemonInstance> &enemy_party,
                           int is_wild,
                           npc *trainer)
{
  int enemy_active = first_usable_pokemon(enemy_party);
  battle_state_t state;
  state.battle_over = 0;
  state.player_won = 0;
  state.player_ran = 0;
  state.wild_caught = 0;
  state.wild_fled = 0;

  if (world.pc.party.empty()) {
    return;
  }

  if (world.pc.active_pokemon < 0 ||
      world.pc.active_pokemon >= static_cast<int>(world.pc.party.size()) ||
      world.pc.party[world.pc.active_pokemon].is_knocked_out()) {
    world.pc.active_pokemon = first_usable_pokemon(world.pc.party);
  }

  while (!state.battle_over) {
    if (!party_has_usable_pokemon(world.pc.party)) {
      break;
    }
    if (!party_has_usable_pokemon(enemy_party)) {
      state.player_won = 1;
      break;
    }

    if (world.pc.party[world.pc.active_pokemon].is_knocked_out()) {
      int forced = choose_party_index(world.pc.party, false, -1,
                                      "Choose a replacement Pokemon.");
      if (forced >= 0) {
        world.pc.active_pokemon = forced;
      }
      continue;
    }

    if (enemy_party[enemy_active].is_knocked_out()) {
      enemy_active = first_usable_pokemon(enemy_party);
      if (enemy_active >= 0) {
        battle_pause_message(std::string(is_wild ? "Wild " : "Enemy ") +
                             enemy_party[enemy_active].name + " enters the battle!");
      }
      continue;
    }

    battle_action_t player_action =
      choose_player_action(is_wild, enemy_party[enemy_active]);
    battle_action_t enemy_action =
      choose_enemy_action(trainer, enemy_party, enemy_active);

    int player_first = player_goes_first(player_action,
                                         enemy_action,
                                         world.pc.party[world.pc.active_pokemon],
                                         enemy_party[enemy_active]);

    if (player_first) {
      apply_player_action(player_action, enemy_party, enemy_active, is_wild, state);
      if (!state.battle_over && party_has_usable_pokemon(enemy_party)) {
        apply_enemy_action(enemy_action, enemy_party, enemy_active, state);
      }
    } else {
      apply_enemy_action(enemy_action, enemy_party, enemy_active, state);
      if (!state.battle_over && party_has_usable_pokemon(world.pc.party)) {
        apply_player_action(player_action, enemy_party, enemy_active, is_wild, state);
      }
    }
  }

  if (trainer && state.player_won && !state.player_ran) {
    trainer->defeated = 1;
    if (trainer->ctype == char_hiker || trainer->ctype == char_rival) {
      trainer->mtype = move_wander;
    }
  }

  if (!state.player_won && !state.player_ran && !state.wild_caught && !state.wild_fled) {
    battle_pause_message("You are out of usable Pokemon!");
  } else if (trainer && state.player_won) {
    battle_pause_message("You defeated the trainer!");
  }

  io_display();
}

void io_battle(character *aggressor, character *defender)
{
  npc *n = (npc *) ((aggressor == &world.pc) ? defender : aggressor);

  if (!n) {
    return;
  }

  if (n->party.empty()) {
    assign_trainer_party(n, current_world_distance());
  }

  n->active_pokemon = first_usable_pokemon(n->party);
  if (n->active_pokemon < 0) {
    n->defeated = 1;
    return;
  }

  resolve_battle(n->party, 0, n);
}

void io_wild_battle(const PokemonInstance &wild)
{
  std::vector<PokemonInstance> enemy_party;
  enemy_party.push_back(wild);
  resolve_battle(enemy_party, 1, NULL);
}


static void io_bag_menu_outside_battle()
{
  while (1) {
    clear();
    mvprintw(0, 0, "Bag (outside battle)");
    mvprintw(1, 0, "1. Potion   2. Revive   ESC. Exit");
    mvprintw(3, 0, "Potions: %d  Revives: %d",
             world.pc.potions, world.pc.revives);
    refresh();

    int key = getch();
    if (key == 27) {
      return;
    }

    if (key == '1') {
      if (world.pc.potions <= 0) {
        battle_pause_message("You do not have any potions.");
        continue;
      }
      int target = choose_party_index(world.pc.party, false, -1,
                                      "Use Potion on which Pokemon?");
      if (target >= 0) {
        world.pc.potions--;
        world.pc.party[target].current_hp += 20;
        if (world.pc.party[target].current_hp > world.pc.party[target].max_hp) {
          world.pc.party[target].current_hp = world.pc.party[target].max_hp;
        }
        battle_pause_message("Potion used on " + world.pc.party[target].name + "!");
      }
    } else if (key == '2') {
      if (world.pc.revives <= 0) {
        battle_pause_message("You do not have any revives.");
        continue;
      }
      int target = choose_party_index(world.pc.party, true, -1,
                                      "Use Revive on which Pokemon?");
      if (target >= 0) {
        world.pc.revives--;
        world.pc.party[target].current_hp = world.pc.party[target].max_hp / 2;
        if (world.pc.party[target].current_hp < 1) {
          world.pc.party[target].current_hp = 1;
        }
        battle_pause_message(world.pc.party[target].name + " was revived!");
      }
    }
  }
}

uint32_t move_pc_dir(uint32_t input, pair_t dest)
{
  dest[dim_y] = world.pc.pos[dim_y];
  dest[dim_x] = world.pc.pos[dim_x];

  switch (input) {
  case 1:
  case 2:
  case 3:
    dest[dim_y]++;
    break;
  case 4:
  case 5:
  case 6:
    break;
  case 7:
  case 8:
  case 9:
    dest[dim_y]--;
    break;
  }
  switch (input) {
  case 1:
  case 4:
  case 7:
    dest[dim_x]--;
    break;
  case 2:
  case 5:
  case 8:
    break;
  case 3:
  case 6:
  case 9:
    dest[dim_x]++;
    break;
  case '>':
    if (world.cur_map->map[world.pc.pos[dim_y]][world.pc.pos[dim_x]] ==
        ter_mart) {
      io_pokemart();
    }
    if (world.cur_map->map[world.pc.pos[dim_y]][world.pc.pos[dim_x]] ==
        ter_center) {
      io_pokemon_center();
    }
    break;
  }

  if (world.cur_map->cmap[dest[dim_y]][dest[dim_x]]) {
    if (dynamic_cast<npc *> (world.cur_map->cmap[dest[dim_y]][dest[dim_x]]) &&
        ((npc *) world.cur_map->cmap[dest[dim_y]][dest[dim_x]])->defeated) {
      // Some kind of greeting here would be nice
      return 1;
    } else if ((dynamic_cast<npc *>
                (world.cur_map->cmap[dest[dim_y]][dest[dim_x]]))) {
      io_battle(&world.pc, world.cur_map->cmap[dest[dim_y]][dest[dim_x]]);
      // Not actually moving, so set dest back to PC position
      dest[dim_x] = world.pc.pos[dim_x];
      dest[dim_y] = world.pc.pos[dim_y];
    }
  }
  
  if (move_cost[char_pc][world.cur_map->map[dest[dim_y]][dest[dim_x]]] ==
      DIJKSTRA_PATH_MAX) {
    return 1;
  }

  return 0;
}

void io_teleport_world(pair_t dest)
{
  /* mvscanw documentation is unclear about return values.  I believe *
   * that the return value works the same way as scanf, but instead   *
   * of counting on that, we'll initialize x and y to out of bounds   *
   * values and accept their updates only if in range.                */
  int x = INT_MAX, y = INT_MAX;
  
  world.cur_map->cmap[world.pc.pos[dim_y]][world.pc.pos[dim_x]] = NULL;

  echo();
  curs_set(1);
  do {
    mvprintw(0, 0, "Enter x [-200, 200]:           ");
    refresh();
    mvscanw(0, 21, "%d", &x);
  } while (x < -200 || x > 200);
  do {
    mvprintw(0, 0, "Enter y [-200, 200]:          ");
    refresh();
    mvscanw(0, 21, "%d", &y);
  } while (y < -200 || y > 200);

  refresh();
  noecho();
  curs_set(0);

  x += 200;
  y += 200;

  world.cur_idx[dim_x] = x;
  world.cur_idx[dim_y] = y;

  new_map(1);
  io_teleport_pc(dest);
}

void io_handle_input(pair_t dest)
{
  uint32_t turn_not_consumed;
  int key;

  do {
    switch (key = getch()) {
    case '7':
    case 'y':
    case KEY_HOME:
      turn_not_consumed = move_pc_dir(7, dest);
      break;
    case '8':
    case 'k':
    case KEY_UP:
      turn_not_consumed = move_pc_dir(8, dest);
      break;
    case '9':
    case 'u':
    case KEY_PPAGE:
      turn_not_consumed = move_pc_dir(9, dest);
      break;
    case '6':
    case 'l':
    case KEY_RIGHT:
      turn_not_consumed = move_pc_dir(6, dest);
      break;
    case '3':
    case 'n':
    case KEY_NPAGE:
      turn_not_consumed = move_pc_dir(3, dest);
      break;
    case '2':
    case 'j':
    case KEY_DOWN:
      turn_not_consumed = move_pc_dir(2, dest);
      break;
    case '1':
    case 'b':
    case KEY_END:
      turn_not_consumed = move_pc_dir(1, dest);
      break;
    case '4':
    case 'h':
    case KEY_LEFT:
      turn_not_consumed = move_pc_dir(4, dest);
      break;
    case '5':
    case ' ':
    case '.':
    case KEY_B2:
      dest[dim_y] = world.pc.pos[dim_y];
      dest[dim_x] = world.pc.pos[dim_x];
      turn_not_consumed = 0;
      break;
    case '>':
      turn_not_consumed = move_pc_dir('>', dest);
      break;
    case 'B':
      io_bag_menu_outside_battle();
      turn_not_consumed = 1;
      break;
    case 'Q':
      dest[dim_y] = world.pc.pos[dim_y];
      dest[dim_x] = world.pc.pos[dim_x];
      world.quit = 1;
      turn_not_consumed = 0;
      break;
      break;
    case 't':
      io_list_trainers();
      turn_not_consumed = 1;
      break;
    case 'p':
      /* Teleport the PC to a random place in the map.              */
      io_teleport_pc(dest);
      turn_not_consumed = 0;
      break;
   case 'f':
      /* Fly to any map in the world.                                */
      io_teleport_world(dest);
      turn_not_consumed = 0;
      break;    
    case 'q':
      /* Demonstrate use of the message queue.  You can use this for *
       * printf()-style debugging (though gdb is probably a better   *
       * option.  Not that it matters, but using this command will   *
       * waste a turn.  Set turn_not_consumed to 1 and you should be *
       * able to figure out why I did it that way.                   */
      io_queue_message("This is the first message.");
      io_queue_message("Since there are multiple messages, "
                       "you will see \"more\" prompts.");
      io_queue_message("You can use any key to advance through messages.");
      io_queue_message("Normal gameplay will not resume until the queue "
                       "is empty.");
      io_queue_message("Long lines will be truncated, not wrapped.");
      io_queue_message("io_queue_message() is variadic and handles "
                       "all printf() conversion specifiers.");
      io_queue_message("Did you see %s?", "what I did there");
      io_queue_message("When the last message is displayed, there will "
                       "be no \"more\" prompt.");
      io_queue_message("Have fun!  And happy printing!");
      io_queue_message("Oh!  And use 'Q' to quit!");

      dest[dim_y] = world.pc.pos[dim_y];
      dest[dim_x] = world.pc.pos[dim_x];
      turn_not_consumed = 0;
      break;
    default:
      /* Also not in the spec.  It's not always easy to figure out what *
       * key code corresponds with a given keystroke.  Print out any    *
       * unhandled key here.  Not only does it give a visual error      *
       * indicator, but it also gives an integer value that can be used *
       * for that key in this (or other) switch statements.  Printed in *
       * octal, with the leading zero, because ncurses.h lists codes in *
       * octal, thus allowing us to do reverse lookups.  If a key has a *
       * name defined in the header, you can use the name here, else    *
       * you can directly use the octal value.                          */
      mvprintw(0, 0, "Unbound key: %#o ", key);
      turn_not_consumed = 1;
    }
    refresh();
  } while (turn_not_consumed);
}
