package hw4;

import api.Cell;
import api.Icon;
import api.Position;

public class DiagonalPiece extends AbstractPiece {
    private int transformState;

    public DiagonalPiece(Position position, Icon[] icons) {
        super(position, createCells(icons));
        transformState = 0;
    }

    private static Cell[] createCells(Icon[] icons) {
        if (icons.length != 2) {
            throw new IllegalArgumentException("DiagonalPiece requires exactly 2 icons.");
        }
        return new Cell[]{
            new Cell(icons[0], new Position(0, 0)),
            new Cell(icons[1], new Position(1, 1))
        };
    }

    @Override
    public void transform() {
        Cell[] cells = getCells();
        if (transformState == 0) {
            cells[0] = new Cell(cells[0].getIcon(), new Position(0, 1));
            cells[1] = new Cell(cells[1].getIcon(), new Position(1, 0));
        } else if (transformState == 1) {
            cells[0] = new Cell(cells[0].getIcon(), new Position(0, 0));
            cells[1] = new Cell(cells[1].getIcon(), new Position(1, 1));
        }
        setCells(cells);
        transformState = (transformState + 1) % 2;
    }
}