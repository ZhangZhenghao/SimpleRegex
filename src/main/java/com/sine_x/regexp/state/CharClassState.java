package com.sine_x.regexp.state;

import com.google.common.collect.RangeSet;

public class CharClassState extends State {

    private boolean complement = false;
    private RangeSet<Character> set;

    public CharClassState(RangeSet<Character> set) {
        this(set, false);
    }

    public CharClassState(RangeSet<Character> set, boolean complement) {
        this.set = set;
        this.complement = complement;
    }

    @Override
    public boolean match(char c) {
        return complement ^ set.contains(c);
    }

}
