package edu.iastate.cs472.proj2;

/**
 *
 * @author Pulkit Saxena
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best
 * move at current state.
 */
public class AlphaBetaSearch extends AdversarialSearch {
    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    private static final int MAX_DEPTH = 4;

    @Override
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        if (legalMoves == null || legalMoves.length == 0) {
            return null;
        }

        int bestScore = Integer.MIN_VALUE;
        CheckersMove bestMove = legalMoves[0];

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (CheckersMove move : legalMoves) {
            CheckersData next = copyState(board);
            next.makeMove(move);
            int score = minValue(next, MAX_DEPTH - 1, alpha, beta);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestScore);
        }

        return bestMove;
    }

    // TODO
    // Implement your helper methods here.

    private int maxValue(CheckersData state, int depth, int alpha, int beta) {
        if (depth == 0) return evaluate(state);

        CheckersMove[] moves = state.getLegalMoves(CheckersData.BLACK);
        if (moves == null) return -10_000; // BLACK has no moves → very bad

        int value = Integer.MIN_VALUE;

        for (CheckersMove m : moves) {
            CheckersData next = copyState(state);
            next.makeMove(m);
            int score = minValue(next, depth - 1, alpha, beta);
            value = Math.max(value, score);
            if (value >= beta) return value;    // beta cut
            alpha = Math.max(alpha, value);
        }

        return value;
    }

    private int minValue(CheckersData state, int depth, int alpha, int beta) {
        if (depth == 0) return evaluate(state);

        CheckersMove[] moves = state.getLegalMoves(CheckersData.RED);
        if (moves == null) return 10_000; // RED has no moves → very good

        int value = Integer.MAX_VALUE;

        for (CheckersMove m : moves) {
            CheckersData next = copyState(state);
            next.makeMove(m);
            int score = maxValue(next, depth - 1, alpha, beta);
            value = Math.min(value, score);
            if (value <= alpha) return value;   // alpha cut
            beta = Math.min(beta, value);
        }

        return value;
    }

    // Simple evaluation: positive if BLACK is better.
    private int evaluate(CheckersData state) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int p = state.board[r][c];
                switch (p) {
                    case CheckersData.BLACK:      score += 3; break;
                    case CheckersData.BLACK_KING: score += 5; break;
                    case CheckersData.RED:        score -= 3; break;
                    case CheckersData.RED_KING:   score -= 5; break;
                    default: break;
                }
            }
        }
        return score;
    }

    private CheckersData copyState(CheckersData src) {
        CheckersData n = new CheckersData();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                n.board[r][c] = src.board[r][c];
        return n;
    }
}
