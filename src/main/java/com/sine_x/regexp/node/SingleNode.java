package com.sine_x.regexp.node;

import com.sine_x.regexp.Pattern;

/**
 * Normal state node
 */
public class SingleNode extends Node {

    private Node out;

    @Override
    public void setOut(Node out) {
        this.out = out;
        if (Pattern.DEBUG_MODE && out != null)
            System.out.printf("%s -> %s\n", this, out);
    }

    public Node getOut() {
        return out;
    }
}
