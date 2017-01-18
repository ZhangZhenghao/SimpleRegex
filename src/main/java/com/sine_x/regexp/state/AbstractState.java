package com.sine_x.regexp.state;

/**
 * Abstract state
 */
public abstract class AbstractState implements Cloneable {

    private int lastSet;

    AbstractState() {}

    public boolean match(char c) {
        return false;
    }

    public boolean isMatched() {
        return false;
    }

    public boolean isAlter() {
        return false;
    }

    public void setOut(AbstractState out) {}

    public void setLastSet(int lastSet) {
        this.lastSet = lastSet;
    }

    public int getLastSet() {
        return lastSet;
    }
}