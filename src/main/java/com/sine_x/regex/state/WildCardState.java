package com.sine_x.regex.state;

public class WildCardState extends State {

    @Override
    public boolean match(char c) {
        return true;
    }

    @Override
    public String toString() {
        return String.format("WildCardState@%x", hashCode());
    }
}
