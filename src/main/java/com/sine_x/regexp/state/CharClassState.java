package com.sine_x.regexp.state;

import com.google.common.collect.RangeSet;

public class CharClassState extends State {

    private RangeSet<Character> set;

    public CharClassState(RangeSet<Character> set) {
        this.set = set;
    }

    @Override
    public boolean match(char c) {
        return set.contains(c);
    }

    @Override
    public String toString() {
        return String.format("CharClassState(%s)@%x", set, hashCode());
    }
}
