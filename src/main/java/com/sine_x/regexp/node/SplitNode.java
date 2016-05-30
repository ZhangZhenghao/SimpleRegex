package com.sine_x.regexp.node;

import com.sine_x.regexp.Pattern;

/**
 * Split state node
 */
public class SplitNode extends Node {

    private Node out1, out2;

    @Override
    public boolean isAlter() {
        return true;
    }

    @Override
    public void setOut(Node out) {
        this.out2 = out;
        if (Pattern.DEBUG_MODE && out != null)
            System.out.printf("%s -> %s\n", this, out);
    }

    public void setOut1(Node out1) {
        this.out1 = out1;
    }

    public void setOut2(Node out2) {
        this.out2 = out2;
    }

    public Node getOut1() {
        return out1;
    }

    public Node getOut2() {
        return out2;
    }

    public SplitNode(Node out1) {
        this.out1 = out1;
        if (Pattern.DEBUG_MODE && out1 != null)
            System.out.printf("%s -> %s\n", this, out1);
    }

    public SplitNode(Node out1, Node out2) {
        this.out1 = out1;
        this.out2 = out2;
        if (Pattern.DEBUG_MODE && out1 != null)
            System.out.printf("%s -> %s\n", this, out1);
        if (Pattern.DEBUG_MODE && out2 != null)
            System.out.printf("%s -> %s\n", this, out2);
    }

    @Override
    public String toString() {
        return + super.nodeID + ":Split";
    }
}