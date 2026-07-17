package hw2;

public class Wheel
{
  /**
   * Constant representing the "bankrupt" wheel segment.
   */
  public static final int BANKRUPT = -1;
  
  /**
   * Constant representing the "free play" wheel segment.
   */
  public static final int FREE_PLAY = 0;
  
  /**
   * Constant representing the "lose a turn" wheel segment.
   */
  public static final int LOSE_A_TURN = 1;

  /**
   * Numeric values for the wheel segments.
   */
  private static final int[] SEGMENT_VALUES = 
    {
      500, // 0 degrees
      900, // 15 degrees
      700,
      300,
      800,
      550,
      400,
      500,
      600,
      350,
      500,
      900,
      BANKRUPT,
      650,
      FREE_PLAY,
      700,
      LOSE_A_TURN,
      800,
      500,
      450,
      500,
      300,
      BANKRUPT,
      5000  // 345 degrees
    };

  private int rotation;

  public void spin(int degrees)
  {
	  rotation = (rotation + degrees) % 360;
//	  if(rotation<0)
//	  {
//		  rotation+=360;
//	  }
  }
  public int getSegmentValue()
  {
	  return SEGMENT_VALUES[(rotation/15)%SEGMENT_VALUES.length];
  }
  public int getRotation()
  {
	  return rotation;
  }
  
}
