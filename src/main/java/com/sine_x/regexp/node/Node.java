package com.sine_x.regexp.node;

public class Node {

    /**
     * match Whether text can bypass the Node
     * @param c A Character in text
     * @return Boolean
     */
    public boolean match(char c) {
        return false;
    }

    /**
     * isMatched Whether text arrived matched state
     * @return Boolean
     */
    public boolean isMatched() {
        return false;
    }

    /**
     * isAlter Whether text have multiple choice
     * @return Boolean
     */
    public boolean isAlter() {
        return false;
    }

}