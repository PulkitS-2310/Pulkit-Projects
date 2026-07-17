
package hw4;
import api.Cell;
import api.Icon;
import api.Position;
public class LPiece extends AbstractPiece {
	    public LPiece(Position position, Icon[] icons) {
	        super(position, createCells(icons));
	    }

	    private static Cell[] createCells(Icon[] icons) {
	        // Ensure the icons array has 4 elements
	        if (icons.length != 4) {
	            throw new IllegalArgumentException();
	        }

	        // Initialize the piece with its position and an array of cells
	        return new Cell[] {
	            new Cell(icons[0], new Position(0, 0)),
	            new Cell(icons[1], new Position(0, 1)),
	            new Cell(icons[2], new Position(1, 1)),
	            new Cell(icons[3], new Position(2, 1))
	        };
	    }
	    @Override
	    public void transform() {
	   	}
}