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
            throw new IllegalArgumentException("Icons array must have exactly 4 elements.");
        }

        // Initialize the piece with its position and an array of cells
        return new Cell[]{
            new Cell(icons[0], new Position(0, 0)),
            new Cell(icons[1], new Position(1, 0)),
            new Cell(icons[2], new Position(1, 1)),
            new Cell(icons[3], new Position(1, 2)),
        };
    }

    @Override
    public void transform() {
        Cell[] cells = getCells();
        if (cells.length != 4) {
            throw new IllegalStateException("SnakePiece should have exactly 4 cells.");
        }

        // Update the positions of the cells based on the current transform state
        switch (transformState) {
            case 0:
                cells[0] = new Cell(cells[0].getIcon(), new Position(0, 1));
                cells[1] = new Cell(cells[1].getIcon(), new Position(1, 0));
                cells[2] = new Cell(cells[2].getIcon(), new Position(1, 1));
                cells[3] = new Cell(cells[3].getIcon(), new Position(2, 1));
                break;
            case 1:
                cells[0] = new Cell(cells[0].getIcon(), new Position(0, 1));
                cells[1] = new Cell(cells[1].getIcon(), new Position(0, 2));
                cells[2] = new Cell(cells[2].getIcon(), new Position(1, 2));
                cells[3] = new Cell(cells[3].getIcon(), new Position(2, 2));
                break;
            case 2:
                cells[0] = new Cell(cells[0].getIcon(), new Position(1, 2));
                cells[1] = new Cell(cells[1].getIcon(), new Position(2, 2));
                cells[2] = new Cell(cells[2].getIcon(), new Position(2, 1));
                cells[3] = new Cell(cells[3].getIcon(), new Position(2, 0));
                break;
            case 3:
                cells[0] = new Cell(cells[0].getIcon(), new Position(1, 1));
                cells[1] = new Cell(cells[1].getIcon(), new Position(1, 0));
                cells[2] = new Cell(cells[2].getIcon(), new Position(0, 0));
                cells[3] = new Cell(cells[3].getIcon(), new Position(0, 1));
                break;
        }
        setCells(cells);
        transformState = (transformState + 1) % 4;
    }

//    @Override
//    public void cycle() {
//        Cell[] cells = getCells();
//        Icon temp = cells[cells.length - 1].getIcon();
//        for (int i = cells.length - 1; i > 0; i--) {
//            cells[i].setIcon(cells[i - 1].getIcon());
//        }
//        cells[0].setIcon(temp);
//        setCells(cells);
//    }
}
