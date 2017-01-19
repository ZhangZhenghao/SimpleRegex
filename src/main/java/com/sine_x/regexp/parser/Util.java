package com.sine_x.regexp.parser;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.sine_x.regexp.exeception.RegexException;
import com.sine_x.regexp.state.CharClassState;
import sun.reflect.generics.tree.Tree;

public class Util {

    private static RangeSet<Character> digit;
    private static RangeSet<Character> word;

    public static RangeSet<Character> get(char c) throws RegexException {
        switch (Character.toLowerCase(c)) {
            case 'd':
                if (digit == null) {
                    digit = TreeRangeSet.create();
                    digit.add(Range.closed('0','9'));
                }
                return Character.isUpperCase(c) ? digit.complement() : digit;
            case 'w':
                if (word == null) {
                    word = TreeRangeSet.create();
                    word.add(Range.closed('a','z'));
                    word.add(Range.closed('A','Z'));
                    word.add(Range.closed('0','9'));
                    word.add(Range.singleton('_'));
                }
                return Character.isUpperCase(c) ? word.complement() : word;
            default:
                throw new RegexException("Syntax error");
        }
    }

    public static char unicode(char a, char b, char c, char d) {
        char[] number = new char[4];
        number[0] = a;
        number[1] = b;
        number[2] = c;
        number[3] = d;
        return (char) Integer.parseInt(new String(number), 16);
    }

    public static char ascii(char a, char b) {
        return unicode('0','0',a,b);
    }
}
