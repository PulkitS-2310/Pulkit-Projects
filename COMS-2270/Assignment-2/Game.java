package hw2;

public class Game {
	/*
	 * It creates an array of type players.
	 */
    private Player[] players;
    /*
     * It creates a new object of wheel class.
     */
    private Wheel wheel = new Wheel();
    /*
     * It creates an object of hidden class
     */
    private HiddenText hidden;
    /*
     * it stores the value of the current player
     */
    private int currentPlayerIndex;
    /*
     * it tells whether the round is over or not.
     */
    private boolean roundOver;
    /*
     * It tells us whether spin is needed or not 
     */
    private boolean spinNeeded;
    /*
     * It tells us about how many times has a letter occurred in a given text 
     */
    private int occurrences;
    /*
     * it stores the value of the vowel cost
     */
    public static final int VOWEL_COST = 250;
    /*
     * @param playerNames: It is an array 
     * of type string through new objects are created for the player class in order to store values for different players 
     */
    public Game(String[] playerNames) {
        players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i]);
        }
    }
    /*
     * @param ch:It gets a character of from the main method.
     * If this method is called, then it first checks whether the character is a vowel or not, is the Round balance for that 
     * player less than the vowel cost or not.Then, the Vowel_Cost gets subtracted from the round balance and we check the number of occurrences the vowel has made.
     * @return: It returns the number of times a vowel has occurred in the phrase.  
     */
    public int buyVowel(char ch) {
        
        if (!isVowel(ch)) {
            System.out.println("Character is not a vowel.");
            return 0;
        }
        if (getRoundBalance(currentPlayerIndex) < VOWEL_COST) {
            System.out.println("Insufficient balance to buy a vowel.");
            return 0;
        }
        System.out.println(getRoundBalance(currentPlayerIndex) + " "+ players[currentPlayerIndex]);
        players[currentPlayerIndex].subtractFromRoundBalance(VOWEL_COST);

       
       
        int beforeBalance = getRoundBalance(currentPlayerIndex);
        System.out.println("Player " + players[currentPlayerIndex].getName() + " balance before subtracting vowel cost: " + beforeBalance);
        
        int afterBalance = getRoundBalance(currentPlayerIndex);
        System.out.println("Player " + players[currentPlayerIndex].getName() + " balance after subtracting vowel cost: " + afterBalance);

       
        occurrences = hidden.letterCount(ch);
        System.out.println("Occurrences of " + ch + ": " + occurrences);
        
        if (occurrences > 0) {
            hidden.update(ch);
            spinNeeded = true;
        } else {
        	occurrences++;
            changePlayer();
        }

        
        System.out.println("Displayed text after updating: " + new String(hidden.getDisplayedText()));
        return occurrences;
    }
    /*
     * @return: it return the correct answer.
     */
    public String getAnswer() {
        return hidden.getHiddenText();
    }
    /*
     * @return: it displays the displayed text.
     */
    public char[] getDisplay() {
        return hidden.getDisplayedText();
    }
   /*
    * @param player: It tells us which player is playing right now.
    * @return: It returns the game balance of that player.
    */
    public int getGameBalance(int player) {
        return players[player].getGameBalance();
    }
    /*
     * @return: it returns the length of the array players
     */
    public int getNumPlayers() {
        return players.length;
    }
    /*
     *@param player:It tells us which player is playing right now.
     *@return: it returns the player's name. 
     */
     public String getPlayerName(int player) {
        return players[player].getName();
    }
    /*
     * @param player: It tells us which player is playing right now.
     * @return: it returns the current player's balance 
     */
    public int getRoundBalance(int player) {
       int balance = players[player].getRoundBalance();
        System.out.println("Player " + players[player].getName() + " round balance: " + balance);  // Debugging statement
        return balance;
    }
    /*
     * @return: it returns the value of rotation of the wheel
     */
    public int getWheelRotation() {
        return wheel.getRotation();
    }
    /*
     * @return: it returns the value of the segment.
     */
    public int getWheelValue() {
        return wheel.getSegmentValue();
    }
    /*
     * @param ch: it gets a character from the main method.
     * @return: It returns the number of occurrences a consonant made in the phrase
     */
    public int guessConsonant(char ch) {
        if (isVowel(ch)) {
            return 0;
        }
        occurrences = hidden.letterCount(ch);
        hidden.update(ch);
        players[currentPlayerIndex].addToRoundBalance(occurrences * wheel.getSegmentValue());
        System.out.println("Player " + players[currentPlayerIndex].getName() + " balance after guessing consonant: " + players[currentPlayerIndex].getRoundBalance());
        if (occurrences == 0) {
            changePlayer();
        } else {
            spinNeeded = false;
        }
        return occurrences;
    }
    /*
     * @param guess: it gets a phrase from the main method.
     * @return: it returns a boolean value whether the phrase has been guessed correctly or not.
     * If it is correct then the that particular player gets the points and he wins the round as well. 
     */
    public boolean guessPhrase(String guess) {
        if (guess.equalsIgnoreCase(hidden.getHiddenText())) {
            int hiddenConsonants = hidden.countHiddenConsonants();
            int wheelValue = wheel.getSegmentValue();
            int balanceIncrease = hiddenConsonants * wheelValue;

            players[currentPlayerIndex].addToRoundBalance(balanceIncrease);
            players[currentPlayerIndex].winRound();
            System.out.println("Player " + players[currentPlayerIndex].getName() + " balance after winning round: " + players[currentPlayerIndex].getGameBalance());

            hidden.updateAllRemaining();
            roundOver = true;
            return true;
        } else {
            changePlayer();
            return false;
        }
    }

    public boolean needsSpin() {
        return spinNeeded;
    }

    public boolean roundOver() {
        return roundOver;
    }

    public void spinWheel(int degrees) {
        wheel.spin(degrees);
        int segment = wheel.getSegmentValue();
        System.out.println("Wheel landed on: " + segment);

        if (segment == Wheel.BANKRUPT) {
            players[currentPlayerIndex].clearRoundBalance();
            System.out.println("Player " + players[currentPlayerIndex].getName() + " balance after bankrupt: " + players[currentPlayerIndex].getRoundBalance());
            changePlayer();
        } else if (segment == Wheel.LOSE_A_TURN) {
            changePlayer();
        } else {
            spinNeeded = false;
        }
    }

    public void startRound(int whoseTurn, HiddenText secretPhrase) {
        hidden = secretPhrase;
        currentPlayerIndex = whoseTurn;
        roundOver = false;
        spinNeeded = true;
        for (Player player : players) {
            player.clearRoundBalance();
            System.out.println("Player " + player.getName() + " balance at start of round: " + player.getRoundBalance());
        }
    }
    /*
     * @return: it tells us which player is playing right now.
     */
    public int whoseTurn() {
        return currentPlayerIndex;
    }
    /*
     * This method is called to change the player.It ensures that the number of players does go pass the length of the array.
     */
    private void changePlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        spinNeeded = true;
        System.out.println("Next player: " + players[currentPlayerIndex].getName());
    }
    /*
     * It checks whether a given character is a vowel or not.
     */
    private boolean isVowel(char ch) {
        return "AEIOUaeiou".indexOf(ch) != -1;
    }


}
