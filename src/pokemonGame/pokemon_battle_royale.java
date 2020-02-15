package pokemonGame;
import java.util.*;


/*
 * Class for Pokemon Objects
 */
class Pokemon {
    public String name;
    public String attackName;
    public Integer attack;
    public String specialAttackName;
    public Integer specialAttack;
    public Integer health;
    public boolean specialUsed;

    public Pokemon(String name, String attackName, Integer attack, String specialAttackName, Integer specialAttack, Integer health, boolean specialUsed) {
        this.name = name;
        this.attackName = attackName;
        this.attack = attack;
        this.specialAttackName = specialAttackName;
        this.specialAttack = specialAttack;
        this.health = health;
        this.specialUsed = specialUsed;
    }
}


/*
 * Class for Pokemon Player Objects
 */
class PokePlayer {
    public String name;
    public boolean isHuman;
    public ArrayList<Pokemon> playerPokemons;
    public PokePlayer(String name, boolean isHuman, ArrayList<Pokemon> playerPokemons) {
        this.name = name;
        this.isHuman = isHuman;
        this.playerPokemons = playerPokemons;
    }
}


/*
 * Main Class for the game
 */
public class pokemon_battle_royale {
    static Random random = new Random();
    static Scanner input = new Scanner(System.in);

    /*
     * Main
     */
    public static void main(String[] args) {
        printInstructions();
        playGame();
    }

    
    public static void playGame() {
        // Get the number of players and choose each players pokemon
        ArrayList<PokePlayer> allPlayers = choosePokemon(getPlayersInput());

        // Keep playing until only 1 player is left
        while(allPlayers.size() != 1) {
            Integer numTotalPlayers = allPlayers.size();
            Integer numPlayerPairs = numTotalPlayers / 2;
            ArrayList<PokePlayer> nextRoundPlayers = new ArrayList<PokePlayer>();

            // Pair off players and make them fight rounds
 			for (int i = 0; i < numPlayerPairs; i++) {
 			    ArrayList<PokePlayer> roundPair = new ArrayList<PokePlayer>();
 			    PokePlayer player1 = allPlayers.get(i*2);
 			    PokePlayer player2 = allPlayers.get(i*2 + 1);
 			    System.out.println(player1.name + " is facing " + player2.name);
 			    roundPair.add(player1);
 			    roundPair.add(player2);
 			    PokePlayer winner = playSet(roundPair);
 			    nextRoundPlayers.add(winner);
 			}

 			// If there was an odd number of players, add the last player to the next round
 			if (numPlayerPairs*2 < numTotalPlayers) {
 			    nextRoundPlayers.add(allPlayers.get(numTotalPlayers - 1));
            }

 			// Reset all Players array list to only have winners
 			allPlayers = new ArrayList<PokePlayer>(nextRoundPlayers);
        }

        System.out.println(allPlayers.get(0).name + " IS THE CHAMPION! YOU ARE NOW THE POKEMON MASTER OF THE REGION!");
    }


    /*
     * Method Function
     * Returns the winner of the round 1v1
     */
    public static PokePlayer playSet(ArrayList<PokePlayer> playerSet) {
        System.out.println("=========== LET THE BATTLE BEGIN! ===========\n");

        PokePlayer player1 = playerSet.get(0);
        PokePlayer player2 = playerSet.get(1);
        // Make a copy of that player's pokemon
        ArrayList<Pokemon> player1Pokemons = new ArrayList<Pokemon>(player1.playerPokemons);
        ArrayList<Pokemon> player2Pokemons = new ArrayList<Pokemon>(player2.playerPokemons);

        // Both players have at least one pokemon
        while(player1Pokemons.size() != 0 && player2Pokemons.size() != 0) {
            // Make Users pick 1 pokemon, fight, remove pokemon
            Pokemon player1Poke = pickPokemon(player1, player1Pokemons);
            Pokemon player2Poke = pickPokemon(player2, player2Pokemons);

            ArrayList<Pokemon> fightResult = playTurn(player1Poke, player2Poke, player1, player2);
            player1Poke = fightResult.get(0);
            player2Poke = fightResult.get(1);

            // Update both pokemons for each player, remove the old pokemon and if it did not lose all it's health, add it back in
            player1Pokemons.remove(player1Poke);
            if (player1Poke.health > 0) {
                player1Pokemons.add(player1Poke);
            }

            player2Pokemons.remove(player2Poke);
            if (player2Poke.health > 0) {
                player2Pokemons.add(player2Poke);
            }
        }

        PokePlayer winner;
        // Player 1 lost
        if (player1Pokemons.size() == 0) {
            winner = player2;
        } else {
            winner = player1;
        }

        System.out.println(winner.name + " has won the battle!\n\n\n"); // TODO: get player winner!
        return winner;
    }

    
    /*
     * Function Method
     * Player picks a pokemon for next turn
     */
    public static Pokemon pickPokemon (PokePlayer player, ArrayList<Pokemon> playerPokemons) {
        System.out.println(player.name + " turn! ^*^*^*^*^*^*^*^*^*^*^");
        System.out.println("Choose a Pokemon!");

        // output player Pokemon's
        for(int a = 0; a < playerPokemons.size(); a++) {
            System.out.println("(" + (a+1) + ")" + " Pokemon: " + playerPokemons.get(a).name + "(-o-):\n"
                    + "  Health -> " + playerPokemons.get(a).health + "\n  "
                    + "----Attacks----\n  "
                    + playerPokemons.get(a).attackName + ": " + playerPokemons.get(a).attack + "\n  "
                    + playerPokemons.get(a).specialAttackName + ": " + playerPokemons.get(a).specialAttack + "\n");
        }

        int playerChoice;

        if (player.isHuman) {
            System.out.println("Pick the Pokemon number: ");
            playerChoice = input.nextInt()-1;
            // TODO: Ensure that the number was valid
        } else {
            playerChoice = random.nextInt(playerPokemons.size());
        }

        Pokemon playerPoke = playerPokemons.get(playerChoice);
        System.out.print("\n" + player.name + " picked " + playerPoke.name + "\n\n\n");
        return playerPoke;
    }


    /*
     * Function Method
     * Player fights and returns both function
     */
    public static ArrayList<Pokemon> playTurn(Pokemon player1Poke, Pokemon player2Poke, PokePlayer player1, PokePlayer player2) {
        // Keep fighting until a pokemon lost all health
        while(player1Poke.health > 0 && player2Poke.health > 0) {
            Integer pokemon1AttackDamage = attackDamage(player1Poke, player1);
            player2Poke.health -= pokemon1AttackDamage;

            if (player2Poke.health <= 0) {
                System.out.println("-=-=-=-=-=-=-=-=- " + player2Poke.name + " is badly injured! Choose a different pokemon! -=-=-=-=-=-=-=-=-\n");
                break;
            }

            Integer pokemon2AttackDamage = attackDamage(player2Poke, player2);
            player1Poke.health -= pokemon2AttackDamage;

            if (player1Poke.health <= 0) {
                System.out.println("-=-=-=-=-=-=-=-=- " + player1Poke.name + " is badly injured! Choose a different pokemon! -=-=-=-=-=-=-=-=-\n");
                break;
            }
        }

        ArrayList<Pokemon> result = new ArrayList<Pokemon>();
        result.add(player1Poke);
        result.add(player2Poke);
        return result;
    }

    
    /*
     * Function Method
     * Pokemon Attack
     */
    public static Integer attackDamage(Pokemon pokemon, PokePlayer player) {
        int playerAttack;

        System.out.println("========= " + pokemon.name + " GO! =========");
        System.out.println(player.name + " choose an attack! ^*^*^*^*^*^*^*^*^*^*^");
        System.out.println("Health -> " + pokemon.health + "\n  "
                + "---- Attacks ----\n"
                + "(1) Attack: " + pokemon.attackName + ": " + pokemon.attack + "\n"
                + "(2) Special Attack (once you use it, you will not be able to use it for a turn): " + pokemon.specialAttackName + ": " + pokemon.specialAttack + "\n");

        if (player.isHuman) {
            System.out.println("Choose an attack: ");
            playerAttack = input.nextInt();
        } else {
            playerAttack = 1;
        }

        if (pokemon.specialUsed == true) {
            System.out.println("Special already used, selecting attack 1!");
            playerAttack = 1;
        }

        int pokeAttackDamage;
        if(playerAttack == 1) {
            pokeAttackDamage = pokemon.attack;
        } else {
            pokeAttackDamage = pokemon.specialAttack;
            pokemon.specialUsed = true;
        }

        return pokeAttackDamage;
    }


    /*
     * Function Method
     * Each player picks 4 pokemons
     */
    public static ArrayList<PokePlayer> choosePokemon(ArrayList<PokePlayer> allPlayers) {
        int numPokemons = 4;
        int numPlayers = allPlayers.size();
        ArrayList<Pokemon> allPokemons = createPokemon();

        // Loop through all the players 4 times and make them pick a pokemon
        for(int a = 0; a < numPokemons; a++) {
            for (int b = 0; b < numPlayers; b++) {
                PokePlayer currPlayer = allPlayers.get(b);
                // Select 2 random pokemons from the list
                int randPoke1 = random.nextInt(allPokemons.size());
                int randPoke2 = random.nextInt(allPokemons.size());

                while(randPoke1 == randPoke2) {
                    if(randPoke1 != randPoke2) {
                        break;
                    }
                    randPoke1 = random.nextInt(allPokemons.size());
                }

                int myChoice;
                // Ask player to pick one of the two pokemons
                if (currPlayer.isHuman) {
                    System.out.println(currPlayer.name + " choose 1 of the Pokemons (1/2):\n"
                            + allPokemons.get(randPoke1).name + "   OR   " + allPokemons.get(randPoke2).name);
                    myChoice = input.nextInt();
                } else {
                    myChoice = random.nextInt(2);
                }

                // Add that pokemon to that players list of pokemon and then remove the pokemon from the available list
                if(myChoice == 1) {
                    currPlayer.playerPokemons.add(allPokemons.get(randPoke1));
                    allPokemons.remove(randPoke1);
                } else {
                    currPlayer.playerPokemons.add(allPokemons.get(randPoke2));
                    allPokemons.remove(randPoke2);
                }
            }
        } // end of parent for loop

        // Print out all the Players pokemons
        for (int i = 0; i < numPlayers; i ++) {
            PokePlayer player = allPlayers.get(i);
            System.out.println(player.name + " got the following Pokemons: ");
            for (int j = 0; j < 4; j ++) {
                System.out.println(player.playerPokemons.get(j).name);
            }
        }

        System.out.print("\n");
        return allPlayers;
    }


    /*
     * Function method
     * Returns a list of players
     * Called in the playGame void method
     */
    public static ArrayList<PokePlayer> getPlayersInput() {
        System.out.println("Enter number of human players: ");
        int numHuman = input.nextInt();
        System.out.println("Enter number of computer players: ");
        int numComputer = input.nextInt();
        input.nextLine();
        int totalPlayers = numHuman + numComputer;

        // In the case too many players are created
        if (totalPlayers > 5) {
            System.out.println("There can only be a max of 5 players total. Please try again!");
            return getPlayersInput();
        }

        ArrayList<PokePlayer> playersInGame = new ArrayList<PokePlayer>();
        System.out.print("\n");

        for(int i = 0; i < numHuman; i++) {
            System.out.println("Enter player name: ");
            String name = input.nextLine();
            playersInGame.add(new PokePlayer(name, true, new ArrayList<Pokemon>()));
        }

        for(int i = numHuman; i < totalPlayers; i++) {
            String compName = "computer" +  (i+1 - numHuman);
            playersInGame.add(new PokePlayer(compName, false, new ArrayList<Pokemon>()));
        }

        System.out.print("\n");

        return playersInGame;
    }


    /*
     * Function method
     * Creates Pokemon objects
     * Called in the main program
     */
    public static ArrayList<Pokemon> createPokemon() {
        ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();
        pokemons.add(new Pokemon("Pikachu", "Bolt", 20, "Plasma Charge", 40, 90, false));
        pokemons.add(new Pokemon("Eevee", "Quick Attack", 5, "Elemental Reign", 55, 60, false));
        pokemons.add(new Pokemon("Mewtwo", "Pulse", 35, "Energy Blast", 80, 130, false));
        pokemons.add(new Pokemon("Snorlax", "Stomp", 25, "Earthquake", 50, 150, false));
        pokemons.add(new Pokemon("Charizard", "Tail Whip", 30, "Heat Wave", 60, 110, false));
        pokemons.add(new Pokemon("Squirtle", "Water Pump", 10, "Wave", 20, 80, false));
        pokemons.add(new Pokemon("Giratina", "Force Punch", 40, "Shadow Claw", 100, 100, false));
        pokemons.add(new Pokemon("Lucario", "Drain", 35, "Inner Focus", 50, 140, false));
        pokemons.add(new Pokemon("Dragonite", "Charge", 25, "Meteor Shower", 55, 100, false));
        pokemons.add(new Pokemon("Magikarp", "Surprise Attack", 10, "Splash", 20, 60, false));
        pokemons.add(new Pokemon("Suicune", "Hydro Pump", 40, "Blizzard", 60, 100, false));
        pokemons.add(new Pokemon("Geodude", "Shied", 10, "Rock Throw", 30, 160, false));
        pokemons.add(new Pokemon("Kyogre", "Hydro Blast", 10, "Tsunami", 100, 90, false));
        pokemons.add(new Pokemon("Regigigas", "Regenerate", 10, "Continental Shift", 30, 180, false));
        pokemons.add(new Pokemon("Venusaur", "Vine Whip", 30, "Poison", 50, 110, false));
        pokemons.add(new Pokemon("Diglett", "Shake", 5, "Dig", 30, 100, false));
        pokemons.add(new Pokemon("Gengar", "Scream", 25, "Ghost Push", 40, 90, false));
        pokemons.add(new Pokemon("Rayquaza", "Tunnel Vision", 30, "Dragon Dance", 70, 120, false));
        pokemons.add(new Pokemon("Darkrai", "Scratch", 40, "Nightmare", 50, 130, false));
        pokemons.add(new Pokemon("Jigglypuff", "Hide", 10, "Sing", 30, 90, false));
        pokemons.add(new Pokemon("Blastroid", "Water Jet", 35, "Hurricane", 120, 80, false));

        return pokemons;
    }


    /*
     * Void method
     * Outputs the objective and instructions of the game
     * Called in the main program
     */
    public static void printInstructions() {
        System.out.println("|<---- Pokemon Battle Royale ---->|\n\n" + "Objective of the game: To battle and win Pokemon battles. \n"
                + "|--> Players will choose 1 pokemon from 2 sets of Pokemon options. The remaining Pokemon will\n"
                + "|--> go to their opponent. Each player will have exactly 4 Pokemons.\n"
                + "|--> Players are randomly put into sets of 2. If their is an odd number of players,\n"
                + "     then the odd player will face a computer player.\n"
                + "|--> Depending on the attack, the player will recieve an updated health score of their Pokemon.\n"
                + "|--> The current player during their turn, will be able to use an attack and or, switch out their Pokemon.\n"
                + "|--> If the current player's Pokemon health is replenished, they would need to"
                + "     select a different pokemon and, it is the opponents turn.\n"
                + "|--> The set ends when one of the player's have no more Pokemon to battle with. The next set begins.\n"
                + "|--> Once all sets are over, it goes on to the next round. Players are randomly put into sets\n"
                + "     and Pokemon health's are replenished.\n"
                + "|--> The game ends when there is only one player left.\n\n"
                + "Enjoy.\n\n");
    }
}

