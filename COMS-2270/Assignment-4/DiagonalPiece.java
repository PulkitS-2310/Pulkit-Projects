
package hw4;

import api.Position;
import api.Cell;
import api.Icon;

public class DiagonalPiece extends AbstractPiece {
    public DiagonalPiece(Position position, Icon[] icons) {
        
        super(position, createCells(icons));
    }

    private static Cell[] createCells(Icon[] icons) {
        
        if (icons.length != 2) {
            throw new IllegalArgumentException();
        }

       
        return new Cell[] {
            new Cell(icons[0], new Position(0, 0)),
            new Cell(icons[1], new Position(1, 1))
        };
    }

    @Override
    public void transform() {
        // Retrieve a copy of the current cells
        Cell[] cells = getCells();

        // Ensure cells array has 2 elements
        if (cells.length != 2) {
            throw new IllegalArgumentException();
        }

        // Flip the cells across the vertical centerline
        cells[0] = new Cell(cells[0].getIcon(), new Position(0, 1));
        cells[1] = new Cell(cells[1].getIcon(), new Position(1, 0));

        // Set the updated cells back to the piece
        setCells(cells);
    }
}