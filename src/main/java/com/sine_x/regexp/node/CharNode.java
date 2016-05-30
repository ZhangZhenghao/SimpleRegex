package com.sine_x.regexp.node;

public class CharNode extends SingleNode {

    private char c;

    public CharNode(char c) {
        this.c = c;
    }

    public boolean match(char c) {
        return this.c == c;
    }

    @Override
    public String toString() {
        return String.format("%d:%c", nodeID, c);
    }
}
