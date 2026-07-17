
package hw1;

public class Bowling {
	/*
	 * It takes the name of player 1
	 */
	private String p1;
	/*
	 * It takes the name of player 2
	 */
	   private String p2;
	   /*
	    * It stores the frame count
	    */
	   private int FrameCount;
	   /*
	    * it stores the roll number
	    */
	   private int RollNumber;
	   /*
	    * it stores the total number of frames
	    */
	   private int TotalFrames;
	   /*
	    * it stores the number of pins
	    */
	   private int numPins;
	   /*
	    * it stores the score of player 1
	    */
	   private int p1_Score;
	   /*
	    * it stores the score of player 2
	    */
	   private int p2_Score;
	   /*
	    * it tells us whether the game has finished or not.
	    */
	   private boolean GameOver;
	   /*
	    * it tells us which player is rolling the ball right now
	    */
	   private int currentPlayer;
	   /*
        * it stores the number of pins that are used in every roll
        */

	   public static final int PINS = 10;
	   /*
	    * it tells whether player 1 had scored a spare in his previous roll or not.
	    */
	   private boolean previousSparePlayer1;
	   /*
	    *  it tells whether player 2 had scored a spare in his previous roll or not.
	    */
	   private boolean previousSparePlayer2;
	   /*
	    * it tells whether player 1 had scored a strike in his previous roll or not.
	    */
	   private boolean previousStrikePlayer1;
	   /*
	    * it tells whether player 2 had scored a strike in his previous roll or not.
	    */
	   private boolean previousStrikePlayer2;
	   /*
	    * It tells us whether a strike was scored 2 rolls before the current one or not for player 1.
	    */
	   private boolean secondPreviousStrikePlayer1;
	   /*
	    * It tells us whether a strike was scored 2 rolls before the current one or not for player 1.
	    */
	   private boolean secondPreviousStrikePlayer2;
	   /*
	    * it initializes all the variables
	    */
	   public Bowling(String player1, String player2, int numFrames) {
	       p1 = player1;
	       p2 = player2;
	       TotalFrames = numFrames;
	       FrameCount = 1;
	       p1_Score = 0;
	       p2_Score = 0;
	       RollNumber = 1;
	       currentPlayer = 1;
	       numPins = PINS;
	   }
	   /*
	    * it initializes the total number of frames to 10
	    */
	   public Bowling(String player1, String player2) {
	       this(player1, player2, 10);
	   }

	   /*
        * @return: it returns the current frame count. It returns -1 if the game is over.
        */
	   public int getCurrentFrame() {
	       if(isGameOver())
	       {
	    	   return -1;
	       }
	       else
	       {
	    	   return FrameCount;
	       }
	   }
       /*
        * @return: it returns the string p1
        */
	   public String getPlayer1Name() {
	       return p1;
	   }
	   /*
	    * @return:it returns the string p2.
	    */

	   public String getPlayer2Name() {
	       return p2;
	   }
       /*
        * @return: it returns the current roll number.It returns -1, if the game has already finished.
        */
	   public int getCurrentRoll() {

	       if(isGameOver())
	       {
	    	   return -1;
	       }
	       else
	       {
	    	   return RollNumber;
	       }
	   

	   }
	   /*
	    * @return: it returns the number of pins remaining.
	    */
	   public int getRemainingPins() {
	       if(isGameOver())
	       {
	    	   return -1;
	       }
	       else {
	    	   return numPins;
	       }

	   }

	   /*
	    * @return: it tells us whether a game has finished or not.
	    */
	   public boolean isGameOver() {
	       GameOver = FrameCount > TotalFrames;
	       return GameOver;
	   }
	   /*
	    * @param: it takes in the name of the player.
	    * @return it returns the score of the chosen player.
	    */

	   public int getScore(String player) {

	       if(p1.equals(player))
	       {
	    	   return p1_Score;
	       }
	       else
	       {
	    	   return p2_Score;
	       }
	   }
	   /*
	    * @return: it tells which player is going to play after this method has been called and sets all the values accordingly.
	    */
	   public int changePlayer() {
	    if(currentPlayer == 1)
	    {
	    currentPlayer=2;
	    RollNumber=1;
	    numPins=PINS;
	    FrameCount=2;
	    }
	    else
	    {
	    currentPlayer=1;
	    RollNumber=1;
	    numPins=PINS;
	    FrameCount=1;
	    }
	    return currentPlayer;
	   }
	   /*
	    * @return: it tells us whether a game has finished or not.
	    */
	   public String endGame() {
	       if (p1_Score > p2_Score) {
	           return "Player 1";
	       } else if (p1_Score < p2_Score) {
	           return "Player 2";
	       } else {
	           return "tie";
	       }
	   }
	   /*
        * @param: it takes in the number of pins that fell down on a dingle roll.
        */
	   public void bowl(int pins) {
	       if (pins > numPins || GameOver) {
	           return;
	       }
	       numPins -= pins;

	       if (currentPlayer == 1) {
	           applySpareBonus(pins, true);
	           applyStrikeBonus(pins, true);
	           p1_Score += pins;

	           if (RollNumber == 2) {
	               if (numPins == 0) {
	                   handleSpare(true);
		           }
	               RollNumber ++;
	               numPins = PINS;
	               
	               
	               // changePlayer();
	           } else if (numPins == 0) {
	               handleStrike(true);
	               FrameCount=3;
	               RollNumber=1;
	               numPins=PINS;
	               
	           }
	           
	           else {
	               RollNumber++;
	              
	           }
	       } else {
	           applySpareBonus(pins, false);
	           applyStrikeBonus(pins, false);
	           p2_Score += pins;

	           if (RollNumber == 2) {
	               if (numPins == 0) {
	                   handleSpare(false);
	                   
	               }
	               RollNumber++;
	               numPins = PINS;
	               FrameCount++;
	               // changePlayer();
	           } else if (numPins == 0) {
	               handleStrike(false);
	               
	              // changePlayer();
	               FrameCount=2;
	               RollNumber=1;
	               numPins=PINS;
	                
	           } else {
	               RollNumber++;
	               
	           }
	       }
	       if(RollNumber > 2)
	       {
	        FrameCount++;
	        RollNumber=1;
	        
	       }
	   }
	   /*
        *  
        * it calculates the bonus points for a spare
        * @param pins: it takes in the number of pins through which the bonus will be calculated.
        * @param player1: it tells for which player the score needs to be evaluated
        */

	   private void applySpareBonus(int pins, boolean isPlayer1) {
	       if (isPlayer1) {
	           if (previousSparePlayer1) {
	               p1_Score += pins;
	               previousSparePlayer1 = false;
	           }
	       } else {
	           if (previousSparePlayer2) {
	               p2_Score += pins;
	               previousSparePlayer2 = false;
	           }
	       }
	   }
	   /*
        * it calculates the bonus points for a strike
        * @param pins: it takes in the number of pins through which the bonus will be calculated.
        * @param player1: it tells for which player the score needs to be evaluated.
        */
	   private void applyStrikeBonus(int pins, boolean isPlayer1) {
	        if (isPlayer1) {
	            if (previousStrikePlayer1) {
	                p1_Score += pins;
	            }
	            if (secondPreviousStrikePlayer1) {
	                p1_Score += pins;
	                secondPreviousStrikePlayer1 = false;
	            }
	        } else {
	            if (previousStrikePlayer2) {
	                p2_Score += pins;
	            }
	            if (secondPreviousStrikePlayer2) {
	                p2_Score += pins;
	                secondPreviousStrikePlayer2 = false;
	            }
	        }
	    }
	   /*
	    * @param isPlayer1: it tells which played had scored a strike in his previous turn.
	    */
	    private void handleStrike(boolean isPlayer1) {
	        if (isPlayer1) {
	            if (previousStrikePlayer1) {
	                secondPreviousStrikePlayer1 = true;
	            }
	            previousStrikePlayer1 = true;
	        } else {
	            if (previousStrikePlayer2) {
	                secondPreviousStrikePlayer2 = true;
	            }
	            previousStrikePlayer2 = true;
	        }
	    }
	   
	    /*
		 * @param isPlayer1: it tells which played had scored a spare in his previous turn.
		 */
	   private void handleSpare(boolean isPlayer1) {
	       if (isPlayer1) {
	           previousSparePlayer1 = true;
	       } else {
	           previousSparePlayer2 = true;
	       }
	   }
	   /*
	    * @param isFoul: it tells us whether a foul was occurred in the game or not.
	    * the method does not do anything if the game is over.
	    */
public void foul(boolean isFoul)
{
	if (GameOver) {
        return;
    }
    if (isFoul) {
        bowl(0);
    }
}
/*
 * it resets the game.
 */
public void resetGame()
{
	
     FrameCount = 1;
     p1_Score = 0;
     p2_Score = 0;
     RollNumber = 1;
     currentPlayer = 1;
     numPins = PINS;
     GameOver = false;
     previousSparePlayer1 = false;
     previousSparePlayer2 = false;
     previousStrikePlayer1 = false;
     previousStrikePlayer2 = false;
     
}
}