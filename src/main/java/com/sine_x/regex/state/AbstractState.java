package com.sine_x.regex.state;

import java.util.logging.Logger;

/**
 * Abstract state
 */
public abstract class AbstractState implements Cloneable {

    protected static Logger logger = Logger.getGlobal();

    private int lastSet;

    public boolean match(char c) {
        return false;
    }

    public boolean isMatched() {
        return false;
    }

    public boolean isAlter() {
        return false;
    }

    public void setOut(AbstractState out) {
    }

    public int getLastSet() {
        return lastSet;
    }

    public void setLastSet(int lastSet) {
        this.lastSet = lastSet;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}