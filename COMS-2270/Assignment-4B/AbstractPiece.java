package hw4;

import api.Position;
import api.Cell;
import api.Icon;
import api.Piece;
public abstract class AbstractPiece implements Piece {
    private Position position;
    private Cell[] cells;

    public AbstractPiece(Position position, Cell[] cells) {
    	 if (cells == null || cells.length == 0) {
             throw new IllegalArgumentException("Cells array must not be null or empty.");
         }
        this.position = position;
        this.cells = new Cell[cells.length];
        for (int i = 0; i < cells.length; i++) 
        {
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
        int baseRow = position.row();
        int baseCol = position.col();
        for (int i = 0; i < cells.length; i++) {
            int absoluteRow = baseRow + cells[i].getRow();
            int absoluteCol = baseCol + cells[i].getCol();
            absoluteCells[i] = new Cell(cells[i].getIcon(),new Position(absoluteRow, absoluteCol));
        }
       // updateCellsAbsolute();
        return absoluteCells;
    }

    @Override
    public void setCells(Cell[] givenCells) {
    	 if (givenCells == null || givenCells.length != cells.length) {
             throw new IllegalArgumentException("Cells array must not be null and must have the same length.");
         }
        cells = new Cell[givenCells.length];
        for (int i = 0; i < givenCells.length; i++) {
            cells[i] = new Cell(givenCells[i]);
        }
    }

    @Override
    public void shiftDown() {
        position = getPosition();
        position=new Position(position.row()+1, position.col());
       // updateCellsAbsolute();
    }

    @Override
    public void shiftLeft() {
        position = new Position(position.row(), position.col() - 1);
       // updateCellsAbsolute();
    }

    @Override
    public void shiftRight() {
        position = new Position(position.row(), position.col() + 1);
      //  updateCellsAbsolute();
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
//        if (obj == null || !(obj instanceof AbstractPiece)) {
//            return false;
//        }
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
            return null;
        }
    }
    protected void updateCellsAbsolute() {
        Cell[] absoluteCells = getCellsAbsolute();
        setCells(absoluteCells);
    }
}