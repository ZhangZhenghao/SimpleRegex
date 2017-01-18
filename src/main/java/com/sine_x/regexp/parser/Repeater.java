package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.RegexException;
import com.sine_x.regexp.state.Fragment;

public class Repeater {

    private int a;
    private int b;

    public static final int NUM_INFINITY = -1;
    public static final int NUM_NONE = -2;

    public static final Repeater REP_NONE_OR_ONCE = new Repeater(0, 1);
    public static final Repeater REP_ANY_TIME = new Repeater(0, NUM_INFINITY);
    public static final Repeater REP_MORE_THAN_ONCE = new Repeater(1, NUM_INFINITY);

    public Repeater(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public Fragment repeat(Fragment fragment) throws RegexException {
        try {
            switch (b) {
                case NUM_NONE:
                    return fragment.repeatN(a);
                case NUM_INFINITY:
                    return fragment.repeatNToInf(a);
                default:
                    return fragment.repeatNtoM(a, b);
            }
        } catch (CloneNotSupportedException e) {
            throw new RegexException(e.getMessage());
        }
    }
}
