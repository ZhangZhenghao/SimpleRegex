package com.sine_x.regexp.state;

/**
 * Matched state state
 */
public class MatchedState extends AbstractState {

    @Override
    public boolean isMatched() {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode());
    }
}