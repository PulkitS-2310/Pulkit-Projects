
package hw4;
import api.Cell;
import api.Icon;
import api.Position;

public class CornerPiece extends AbstractPiece
{
 private int transformState;

 public CornerPiece(Position position, Icon[] icons) {
     super(position, createCells(icons));
     this.transformState = 0; // Initial state
 }

 private static Cell[] createCells(Icon[] icons) {
     // Ensure the icons array has 3 elements
     if (icons.length != 3) {
         throw new IllegalArgumentException();
     }

     // Initialize the piece with its position and an array of cells
     return new Cell[] {
         new Cell(icons[0], new Position(0, 0)),
         new Cell(icons[1], new Position(1, 0)),
         new Cell(icons[2], new Position(1, 1))
     };
 }
 @Override
  public void transform()
  {
	  Cell[] cells = getCells();

	  if(this.transformState==0)
	  {
		  cells[0] = new Cell(cells[0].getIcon(),new Position(0,1));
		  cells[1] = new Cell(cells[1].getIcon(),new Position(0,0));
		  cells[2] = new Cell(cells[2].getIcon(),new Position(1,0));
	  }
	  else if(this.transformState==1)
	  {
		  cells[0] = new Cell(cells[0].getIcon(),new Position(1,1));
		  cells[1] = new Cell(cells[1].getIcon(),new Position(0,1));
		  cells[2] = new Cell(cells[2].getIcon(),new Position(0,0));  
	  }
	  else if(this.transformState==2)
	  {
		  cells[0] = new Cell(cells[0].getIcon(),new Position(1,0));
		  cells[1] = new Cell(cells[1].getIcon(),new Position(1,1));
		  cells[2] = new Cell(cells[2].getIcon(),new Position(0,1));
	  }
	  else if(this.transformState==3)
	  {
		  cells[0] = new Cell(cells[0].getIcon(),new Position(0,0));
		  cells[1] = new Cell(cells[1].getIcon(),new Position(1,0));
		  cells[2] = new Cell(cells[2].getIcon(),new Position(1,1));
	  }
	  setCells(cells);
	  this.transformState = (transformState + 1) % 4;
  }
  
 }