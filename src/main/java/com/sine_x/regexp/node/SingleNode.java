package com.sine_x.regexp.node;

public class SingleNode extends Node {

    private char c;
    private Node out;

    public SingleNode(char c) {
        this.c = c;
    }

    public void setOut(Node out) {
        this.out = out;
    }

    public Node getOut() {
        return out;
    }

    @Override
    public boolean match(char c) {
        return this.c == c;
    }
}
