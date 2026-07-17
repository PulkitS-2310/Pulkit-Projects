package hw4;

import api.Position;
import api.Cell;
import api.Icon;
import java.awt.Color;

public class SnakePieceTest {
    public static void main(String[] args) {
        // Define icons
        Icon[] icons = {
            new Icon(Color.RED),
            new Icon(Color.GREEN),
            new Icon(Color.BLUE),
            new Icon(Color.YELLOW)
        };

        // Initialize the SnakePiece at position (3, 7)
        SnakePiece snake = new SnakePiece(new Position(3, 7), icons);

        // Print initial positions
        System.out.println("Initial positions:");
        printCellPositions(snake, new Position[]{
            new Position(3, 7), 
            new Position(3, 8), 
            new Position(4, 8), 
            new Position(5, 8)
        });

        // Shift left and check positions
        snake.shiftLeft();
        System.out.println("Positions after shiftLeft:");
        printCellPositions(snake, new Position[]{
            new Position(3, 6), 
            new Position(3, 7), 
            new Position(4, 7), 
            new Position(5, 7)
        });

        // Shift right and check positions
        snake.shiftRight();
        System.out.println("Positions after shiftRight:");
        printCellPositions(snake, new Position[]{
            new Position(3, 7), 
            new Position(3, 8), 
            new Position(4, 8), 
            new Position(5, 8)
        });

        // Shift down and check positions
        snake.shiftDown();
        System.out.println("Positions after shiftDown:");
        printCellPositions(snake, new Position[]{
            new Position(4, 7), 
            new Position(4, 8), 
            new Position(5, 8), 
            new Position(6, 8)
        });

        // Transform 15 times and check positions
        for (int i = 0; i < 15; i++) {
            snake.transform();
        }
        System.out.println("Positions after 15 transforms:");
        printCellPositions(snake, new Position[]{
            new Position(3, 9), 
            new Position(3, 10), 
            new Position(4, 10), 
            new Position(5, 10)
        });
    }

    private static void printCellPositions(SnakePiece snake, Position[] expectedPositions) {
        Cell[] cells = snake.getCellsAbsolute();
        for (int i = 0; i < cells.length; i++) {
            System.out.printf("Cell %d: (%d, %d), Expected: (%d, %d)%n", 
                i, cells[i].getRow(), cells[i].getCol(), 
                expectedPositions[i].row(), expectedPositions[i].col());
        }
    }
}
