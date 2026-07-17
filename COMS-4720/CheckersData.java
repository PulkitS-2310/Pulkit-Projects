package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

    /** The following constants represent the possible contents of a square
     * on the board.  The constants RED and BLACK also represent players
     * in the game.
    */

    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int RED_KING = 2;
    public static final int BLACK = 3;
    public static final int BLACK_KING = 4;

    int[][] board; // board[r][c] is the contents of row r, column c.

    /**
     * Constructor.  Create the board and set it up for a new game.
     */

    public CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */

    void setUpGame() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = EMPTY;


        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 8; c++)
                if ((r % 2) == (c % 2))
                    board[r][c] = BLACK;

        // RED on rows 5–7
        for (int r = 5; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if ((r % 2) == (c % 2))
                    board[r][c] = RED;
    }

    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }

    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */

    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for (int i = 0; i < l - 1; i++) {
            int fr = move.rows.get(i);
            int fc = move.cols.get(i);
            int tr = move.rows.get(i + 1);
            int tc = move.cols.get(i + 1);
            makeMove(fr, fc, tr, tc);
        }
    }

    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // TODO
        // Update the board for the given move. You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        // 2. if this move is a jump, remove the captured piece
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king

        int piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;

        if (Math.abs(fromRow - toRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            board[midRow][midCol] = EMPTY;
        }

        if (piece == RED && toRow == 0) board[toRow][toCol] = RED_KING;
        if (piece == BLACK && toRow == 7) board[toRow][toCol] = BLACK_KING;
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    public CheckersMove[] getLegalMoves(int player) {
        if (player != RED && player != BLACK) return null;

        ArrayList<CheckersMove> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = board[r][c];
                if (!belongsTo(player, piece)) continue;

                CheckersMove[] jumps = getLegalJumpsFrom(player, r, c);
                if (jumps != null)
                    moves.addAll(Arrays.asList(jumps));
            }
        }

        if (!moves.isEmpty())
            return moves.toArray(new CheckersMove[0]);  // must jump


        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = board[r][c];
                if (!belongsTo(player, piece)) continue;
                addNormalMoves(player, r, c, moves);
            }
        }

        return moves.isEmpty() ? null : moves.toArray(new CheckersMove[0]);
    }

    private boolean belongsTo(int player, int piece) {
        return (player == RED && (piece == RED || piece == RED_KING)) ||
                (player == BLACK && (piece == BLACK || piece == BLACK_KING));
    }

    private void addNormalMoves(int player, int r, int c, ArrayList<CheckersMove> moves) {
        int piece = board[r][c];
        boolean king = (piece == RED_KING || piece == BLACK_KING);
        int dir = (player == RED) ? -1 : 1;

        tryAddNormal(r, c, r + dir, c - 1, moves);
        tryAddNormal(r, c, r + dir, c + 1, moves);

        if (king) {
            tryAddNormal(r, c, r - dir, c - 1, moves);
            tryAddNormal(r, c, r - dir, c + 1, moves);
        }
    }

    private void tryAddNormal(int fr, int fc, int tr, int tc, ArrayList<CheckersMove> moves) {
        if (tr >= 0 && tr < 8 && tc >= 0 && tc < 8 && board[tr][tc] == EMPTY) {
            moves.add(new CheckersMove(fr, fc, tr, tc));
        }
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps.
     * Each move returned in the array represents a sequence of jumps
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */

    public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        int piece = board[row][col];
        if (!belongsTo(player, piece)) return null;

        ArrayList<CheckersMove> result = new ArrayList<>();

        CheckersMove start = new CheckersMove();
        start.rows.add(row);
        start.cols.add(col);

        CheckersData copy = copyBoard(this);
        findJumps(copy, player, start, result);

        if (result.isEmpty()) {
            return null;
        }

        else {
            return result.toArray(new CheckersMove[0]);
        }
    }

    private CheckersData copyBoard(CheckersData src) {
        CheckersData n = new CheckersData();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                n.board[r][c] = src.board[r][c];
        return n;
    }

    private void findJumps(CheckersData state, int player,
                           CheckersMove partial, ArrayList<CheckersMove> result) {

        int r = partial.rows.get(partial.rows.size() - 1);
        int c = partial.cols.get(partial.cols.size() - 1);
        int piece = state.board[r][c];
        boolean king = (piece == RED_KING || piece == BLACK_KING);

        int[][] dirs;

        if (player == RED) {
            if (king) {
                dirs = new int[][]{
                        {-1, -1}, {-1, 1},
                        {1, -1}, {1, 1}
                };
            }
            else {
                dirs = new int[][]{
                        {-1, -1}, {-1, 1}
                };
            }
        }
        else {
            if (king) {
                dirs = new int[][]{
                        {-1, -1}, {-1, 1},
                        {1, -1}, {1, 1}
                };
            }

            else {
                dirs = new int[][]{
                        {1, -1}, {1, 1}
                };
            }
        }

        boolean extended = false;

        for (int[] d : dirs) {
            int r1 = r + d[0];
            int c1 = c + d[1];
            int r2 = r + 2*d[0];
            int c2 = c + 2*d[1];

            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) continue;
            if (state.board[r2][c2] != EMPTY) continue;

            int mid = state.board[r1][c1];
            if (mid == EMPTY) continue;
            if (player == RED && !(mid == BLACK || mid == BLACK_KING)) continue;
            if (player == BLACK && !(mid == RED || mid == RED_KING)) continue;

            CheckersData next = copyBoard(state);
            CheckersMove step = new CheckersMove(r, c, r2, c2);
            next.makeMove(step);

            CheckersMove jump = partial.clone();
            jump.addMove(r2, c2);

            findJumps(next, player, jump, result);
            extended = true;
        }

        if (!extended && partial.rows.size() > 1) {
            result.add(partial);
        }
    }
}
