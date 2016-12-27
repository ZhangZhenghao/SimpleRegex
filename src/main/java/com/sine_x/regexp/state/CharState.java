package com.sine_x.regexp.state;

public class CharState extends State {

    private char c;

    public CharState(char c) {
        this.c = c;
    }

    @Override
    public boolean match(char c) {
        return this.c == c;
    }

}
