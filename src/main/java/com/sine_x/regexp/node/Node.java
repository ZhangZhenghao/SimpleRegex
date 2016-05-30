package com.sine_x.regexp.node;

import com.sine_x.regexp.Pattern;

/**
 * Abstract node
 */
public abstract class Node implements Cloneable {

    int nodeID;
    int copyID;
    private int lastSet;

    Node() {
        if (Pattern.DEBUG_MODE) nodeID = Pattern.nodeID++;
    }

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

    /**
     * setOut Set the nextPos node of current node
     * @param out Next node
     */
    public void setOut(Node out) {

    }

    /**
     * Set last set ID
     * @param lastSet Last set ID
     */
    public void setLastSet(int lastSet) {
        this.lastSet = lastSet;
    }

    /**
     * Get last set ID
     * @return Last set ID
     */
    public int getLastSet() {
        return lastSet;
    }

    public int getCopyID() {
        return copyID;
    }

    public Node clone(int copyID) throws CloneNotSupportedException {
        Node node = (Node) super.clone();
        node.nodeID = Pattern.nodeID++;
        this.copyID = copyID;
        return node;
    }
}