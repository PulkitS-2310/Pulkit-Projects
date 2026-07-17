
package hw2;

public class Player
{
private int RoundBalance;
private int GameBalance;
private String Player;
public Player(String givenName)
{
	Player=givenName;
	RoundBalance=0;
	GameBalance=0;
}
  public void addToRoundBalance(int toAdd)
  {
	  RoundBalance+=toAdd;
  }
  public void clearRoundBalance()
  {
	  RoundBalance=0;
  }
  public int getGameBalance()
  {
	  return GameBalance;
  }
  public String getName()
  {
	  return Player;
  }
  public int getRoundBalance()
  {
	  return RoundBalance;
  }
  public void subtractFromRoundBalance(int toSubtract)
  {
	  RoundBalance-=toSubtract;
  }
  public void winRound()
  {
	  GameBalance+=RoundBalance;
	 
  }
	//TODO
	
}