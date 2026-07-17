package hw4;
import api.Cell;
import api.Icon;
import api.Position;
public class LPiece extends AbstractPiece {
	    private int transformState;
	    public LPiece(Position position, Icon[] icons) {
	        super(position, createCells(icons));
	        transformState=0;
	    }

	    private static Cell[] createCells(Icon[] icons) {
	        // Ensure the icons array has 4 elements
	        if (icons.length != 4) {
	            throw new IllegalArgumentException();
	        }

	        // Initialize the piece with its position and an array of cells
	        return new Cell[] {
		            new Cell(icons[0], new Position(0, 2)),
		            new Cell(icons[1], new Position(0, 1)),
		            new Cell(icons[2], new Position(1, 1)),
		            new Cell(icons[3], new Position(2, 1))
		        };
		    }
	    
	    @Override
	    public void transform() {
	    	        Cell[] cells = getCells();
	    	        if (transformState == 0) {
	    	            cells[0] = new Cell(cells[0].getIcon(), new Position(0, 2));
	    	            cells[1] = new Cell(cells[1].getIcon(), new Position(1, 2));
	    	            cells[2] = new Cell(cells[2].getIcon(), new Position(1, 1));
	    	            cells[3] = new Cell(cells[3].getIcon(), new Position(1, 0));
	    	        } else if (transformState == 1) {
	    	            cells[0] = new Cell(cells[0].getIcon(), new Position(2, 2));
	    	            cells[1] = new Cell(cells[1].getIcon(), new Position(2, 1));
	    	            cells[2] = new Cell(cells[2].getIcon(), new Position(1, 1));
	    	            cells[3] = new Cell(cells[3].getIcon(), new Position(0, 1));
	    	        } else if (transformState == 2) {
	    	            cells[0] = new Cell(cells[0].getIcon(), new Position(2, 0));
	    	            cells[1] = new Cell(cells[1].getIcon(), new Position(1, 0));
	    	            cells[2] = new Cell(cells[2].getIcon(), new Position(1, 1));
	    	            cells[3] = new Cell(cells[3].getIcon(), new Position(1, 2));
	    	        } else if (transformState == 3) {
	    	            cells[0] = new Cell(cells[0].getIcon(), new Position(0, 0));
	    	            cells[1] = new Cell(cells[1].getIcon(), new Position(0, 1));
	    	            cells[2] = new Cell(cells[2].getIcon(), new Position(1, 1));
	    	            cells[3] = new Cell(cells[3].getIcon(), new Position(2, 1));
	    	        }
	    	       
	    	        
	   	}
}