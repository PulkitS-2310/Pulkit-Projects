
package hw4;
import api.Cell;
import api.Icon;
import api.Position;
public class SnakePiece extends AbstractPiece {
    private int transformState;
    public SnakePiece(Position position, Icon[] icons) {
        super(position, createCells(icons));
        this.transformState = 0; // Initial state
    }

    private static Cell[] createCells(Icon[] icons) {
        // Ensure the icons array has 4 elements
        if (icons.length != 4) {
            throw new IllegalArgumentException();
        }

        // Initialize the piece with its position and an array of cells
        return new Cell[] {
            new Cell(icons[0], new Position(0, 0)),
            new Cell(icons[1], new Position(1, 0)),
            new Cell(icons[2], new Position(1, 1)),
            new Cell(icons[3], new Position(1, 2))
        };
    }
    
@Override
public void transform()
{
	Cell[] cells = getCells();
	if (cells.length != 4) {
        throw new IllegalStateException();
    }
	if(transformState==0)
	{
		cells[0] = new Cell(cells[0].getIcon(),new Position(0, 1));
        cells[1] = new Cell(cells[1].getIcon(),new Position(0, 0));
        cells[2] = new Cell( cells[2].getIcon(),new Position(1, 0));
        cells[3] = new Cell(cells[3].getIcon(),new Position(2, 0));
	}
	else if(transformState==1)
	{
		cells[0] = new Cell(cells[0].getIcon(),new Position(1, 0));
        cells[1] = new Cell(cells[1].getIcon(),new Position(0, 1));
        cells[2] = new Cell(cells[2].getIcon(),new Position(0, 0));
        cells[3] = new Cell(cells[3].getIcon(),new Position(0, 2));
	}
	else if(transformState==3)
	{
		 cells[0] = new Cell(cells[0].getIcon(),new Position(0, 2));
         cells[1] = new Cell(cells[1].getIcon(),new Position(0, 1));
         cells[2] = new Cell(cells[2].getIcon(),new Position(0, 0));
         cells[3] = new Cell(cells[3].getIcon(),new Position(1, 0));
	}
	setCells(cells);
	transformState = (transformState+1)%4;
}
}