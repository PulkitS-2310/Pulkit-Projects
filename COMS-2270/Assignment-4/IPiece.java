package hw4;
import api.Position;
import api.Cell;
import api.Icon;
public class IPiece extends AbstractPiece{

	public IPiece(Position position, Icon[] icons) {
        super(position, createCells(icons));
    }

    private static Cell[] createCells(Icon[] icons) {
        // Ensure the icons array has 3 elements
        if (icons.length != 3) {
            throw new IllegalArgumentException();
        }

        // Initialize the piece with its position and an array of cells
        return new Cell[] {
            new Cell(icons[0], new Position(0, 1)),
            new Cell(icons[1], new Position(1, 1)),
            new Cell(icons[2], new Position(2, 1))
        };
  
    }  
    @Override
    public void transform() {
   	}
}