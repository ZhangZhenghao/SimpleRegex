package com.sine_x.regexp.node;

/**
 * Matched state node
 */
public class MatchedNode extends Node {

    @Override
    public boolean isMatched() {
        return true;
    }

    @Override
    public String toString() {
        return super.nodeID + ":Matched";
    }
}