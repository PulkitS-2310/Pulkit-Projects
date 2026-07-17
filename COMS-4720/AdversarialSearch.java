package edu.iastate.cs472.proj2;

/**
 *
 * @author Pulkit Saxena
 *
 */

/**
 * This class is to be extended by the classes AlphaBetaSearch and MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {

    protected CheckersData board;
    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board) {

        this.board = board;
    }

    /**
     *
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves() {
        return board.getLegalMoves(CheckersData.BLACK);
    }

    /**
     * Return a move chosen by the search algorithm.
     *
     * @param legalMoves array of legal moves for the current player
     * @return the move chosen by the AI
     */

    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
