package edu.iastate.cs472.proj2;

import java.util.Random;

/**
 *
 * @author Pulkit Saxena
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

    private static final int ITERATIONS = 300;

    private final Random rand = new Random();
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
    @Override
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is an 2D array of the following integers defined below:
        //
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        if (legalMoves == null || legalMoves.length == 0) {
            return null;
        }

        MCNode root = new MCNode(copyState(board), CheckersData.BLACK, null);

        for (int i = 0; i < ITERATIONS; i++) {

            MCNode leaf = select(root);

            MCNode expanded = expand(leaf);

            double result = simulate(copyState(expanded.state), expanded.player);

            backpropagate(expanded, result);
        }

        MCNode bestChild = null;
        int bestVisits = -1;

        for (MCNode child : root.children) {
            if (child.visits > bestVisits) {
                bestVisits = child.visits;
                bestChild = child;
            }
        }

        if (bestChild == null) {
            return legalMoves[0];
        }

        return bestChild.move;
    }

// TODO
    //
    // Implement your helper methods here. They include at least the methods for selection,
    // expansion, simulation, and back-propagation.
    //
    // For representation of the search tree, you are suggested (but limited) to use a
    // child-sibling tree already implemented in the two classes CSTree and CSNode (which
    // you may feel free to modify).  If you decide not to use the child-sibling tree, simply
    // remove these two classes.
    //

    private MCNode select(MCNode node) {

        while (!node.isLeaf()) {

            MCNode bestChild = null;
            double bestUCB = Double.NEGATIVE_INFINITY;

            for (MCNode child : node.children) {

                if (child.visits == 0) {
                    bestChild = child;
                    break;
                }

                double exploitation = child.wins / (double) child.visits;
                double exploration = Math.sqrt(2.0 * Math.log(node.visits) / child.visits);

                double ucb = exploitation + exploration;

                if (ucb > bestUCB) {
                    bestUCB = ucb;
                    bestChild = child;
                }
            }

            if (bestChild == null) {
                break;
            }

            node = bestChild;
        }

        return node;
    }

    private MCNode expand(MCNode node) {

        CheckersMove[] moves = node.state.getLegalMoves(node.player);

        if (moves == null || moves.length == 0) {
            return node;
        }

        if (!node.children.isEmpty()) {
            return node.children.get(rand.nextInt(node.children.size()));
        }

        for (CheckersMove move : moves) {

            CheckersData nextState = copyState(node.state);
            nextState.makeMove(move);

            MCNode child = new MCNode(nextState, opposite(node.player), move);
            child.parent = node;

            node.children.add(child);
        }

        return node.children.get(0);
    }

    private double simulate(CheckersData state, int playerToMove) {

        int current = playerToMove;

        while (true) {

            CheckersMove[] moves = state.getLegalMoves(current);

            if (moves == null || moves.length == 0) {
                if (current == CheckersData.BLACK) {
                    return 0.0;
                } else {
                    return 1.0;
                }
            }
            CheckersMove move = moves[rand.nextInt(moves.length)];
            state.makeMove(move);
            current = opposite(current);
        }
    }

    private void backpropagate(MCNode node, double result) {

        while (node != null) {
            node.visits++;
            node.wins += result;
            node = node.parent;
        }
    }

    private int opposite(int p) {
        return (p == CheckersData.BLACK) ? CheckersData.RED : CheckersData.BLACK;
    }

    private CheckersData copyState(CheckersData original) {

        CheckersData copy = new CheckersData();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                copy.board[r][c] = original.board[r][c];
            }
        }
        return copy;
    }
}
