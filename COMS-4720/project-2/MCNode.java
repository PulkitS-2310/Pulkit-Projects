package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */

public class MCNode {

    public CheckersData state;
    public int player;
    public CheckersMove move;

    public MCNode parent;
    public ArrayList<MCNode> children = new ArrayList<>();

    public int visits = 0;
    public double wins = 0.0;

    public MCNode(CheckersData s, int p, CheckersMove m) {
        state = s;
        player = p;
        move = m;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
