package hw4;

import api.Position;
import api.Cell;
import api.Icon;
import api.Piece;
public abstract class AbstractPiece implements Piece {
    private Position position;
    private Cell[] cells;

    public AbstractPiece(Position position, Cell[] cells) {
        this.position = position;
        this.cells = new Cell[cells.length];
        for (int i = 0; i < cells.length; i++) {
            this.cells[i] = new Cell(cells[i]);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Cell[] getCells() {
        Cell[] copy = new Cell[cells.length];
        for (int i = 0; i < cells.length; i++) {
            copy[i] = new Cell(cells[i]);
        }
        return copy;
    }

    @Override
    public Cell[] getCellsAbsolute() {
        Cell[] absoluteCells = new Cell[cells.length];
        for (int i = 0; i < cells.length; i++) {
            int absoluteRow = position.row() + cells[i].getRow();
            int absoluteCol = position.col() + cells[i].getCol();
            absoluteCells[i] = new Cell(cells[i].getIcon(),new Position(absoluteRow, absoluteCol));
        }
        return absoluteCells;
    }

    @Override
    public void setCells(Cell[] givenCells) {
        cells = new Cell[givenCells.length];
        for (int i = 0; i < givenCells.length; i++) {
            cells[i] = new Cell(givenCells[i]);
        }
    }

    @Override
    public void shiftDown() {
        position = new Position(position.row() + 1, position.col());
    }

    @Override
    public void shiftLeft() {
        position = new Position(position.row(), position.col() - 1);
    }

    @Override
    public void shiftRight() {
        position = new Position(position.row(), position.col() + 1);
    }

    @Override
    public void transform() {
	}

    @Override
    public void cycle() {
        Icon temp = cells[cells.length - 1].getIcon();
        for (int i = cells.length - 1; i > 0; i--) {
            cells[i].setIcon(cells[i - 1].getIcon());
        }
        cells[0].setIcon(temp);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbstractPiece)) {
            return false;
        }
        AbstractPiece other = (AbstractPiece) obj;
        if (!position.equals(other.position)) {
            return false;
        }
        Cell[] otherCells = other.getCells();
        if (cells.length != otherCells.length) {
            return false;
        }
        for (int i = 0; i < cells.length; i++) {
            if (!cells[i].equals(otherCells[i])) {
                return false;
            }
        }
        return true;
    }


    @Override
    public AbstractPiece clone() {
        try {
            AbstractPiece copy = (AbstractPiece) super.clone();
            copy.cells = new Cell[cells.length];
            for (int i = 0; i < cells.length; i++) {
                copy.cells[i] = new Cell(cells[i]);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }
}
