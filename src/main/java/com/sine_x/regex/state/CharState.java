package com.sine_x.regex.state;

public class CharState extends State {

    private char c;

    public CharState(char c) {
        this.c = c;
    }

    @Override
    public boolean match(char c) {
        return this.c == c;
    }

    @Override
    public String toString() {
        return String.format("CharState('%c')@%x", c, hashCode());
    }
}
