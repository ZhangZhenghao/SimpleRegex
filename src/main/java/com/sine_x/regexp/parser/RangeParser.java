package com.sine_x.regexp.parser;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.sine_x.regexp.exeception.ExpressionException;

public class RangeParser {

    private int nextPos;
    private RangeSet<Character> range;

    public static RangeParser parseRange(String regexp, int pos) throws ExpressionException {
        RangeSet<Character> set = TreeRangeSet.create();
        char lower, upper;
        // parse lower
        if (regexp.charAt(pos) == '\\') {
            pos++;
            // bound check
            if (pos >= regexp.length())
                throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_CLASS);
            char escapeChar = regexp.charAt(pos);
            // meta character
            switch (escapeChar) {
                case 'D':
                case 'd':
                    set.add(Range.closed('0', '1'));
                    if (escapeChar == 'D')
                        set = set.complement();
                    break;
            }
        }
        // parse upper
        return new RangeParser(set, pos);
    }

    RangeParser(RangeSet<Character> range, int nextPos) {
        this.range = range;
        this.nextPos = nextPos;
    }

    public int getNextPos() {
        return nextPos;
    }

    public RangeSet<Character> getRange() {
        return range;
    }

    // character parser
    private static class CharParser {

        private static char[] ESACPE_CHARACTERS = {'^', '-', '[', ']', '\\'};

        private int nextPos;
        private int ch;

        public static CharParser parseChar(String regexp, int pos) {
            return null;
        }

        public int getNextPos() {
            return nextPos;
        }

        public int getCh() {
            return ch;
        }
    }
}
