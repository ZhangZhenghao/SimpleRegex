package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.ExpressionException;

public class RepeatParser {

    // DFA state
    private static final int INIT = 0x0;
    private static final int START = 0x1;
    private static final int LOWER = 0x2;
    private static final int COMMA = 0x3;
    private static final int UPPER = 0x4;
    private static final int END = 0x5;

    public static final int INFINITY = -1;
    public static final int NONE = -2;

    private int nextPos;
    private int lower;
    private int upper;

    public static RepeatParser parser(String regexp, int pos) throws ExpressionException {
        RepeatParser parser = new RepeatParser();
        int state = INIT;
        while (pos < regexp.length() && state != END) {
            char ch = regexp.charAt(pos);
            switch (state) {
                case INIT:
                    if (ch == '{') {
                        parser.lower = 0;
                        parser.upper = 0;
                        state = START;
                    } else {
                        throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
                    }
                    break;
                case START:
                    if (Character.isDigit(ch)) {
                        parser.lower *= 10;
                        parser.lower += ch - '0';
                        state = LOWER;
                    } else {
                        throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
                    }
                    break;
                case LOWER:
                    if (Character.isDigit(ch)) {
                        parser.lower *= 10;
                        parser.lower += ch - '0';
                    } else if (ch == ',') {
                        state = COMMA;
                    } else if (ch == '}') {
                        parser.upper = NONE;
                        state = END;
                    } else {
                        throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
                    }
                    break;
                case COMMA:
                    if (Character.isDigit(ch)) {
                        parser.upper *= 10;
                        parser.upper += ch - '0';
                        state = UPPER;
                    } else if (ch == '}') {
                        parser.upper = INFINITY;
                        state = END;
                    } else {
                        throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
                    }
                    break;
                case UPPER:
                    if (Character.isDigit(ch)) {
                        parser.upper *= 10;
                        parser.upper += ch - '0';
                    } else if (ch == '}') {
                        state = END;
                    } else {
                        throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
                    }
                    break;
                default:
            }
            pos++;
        }
        if (state != END) {
            throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_REPEAT);
        }
        parser.nextPos = pos;
        return parser;
    }

    public int getNextPos() {
        return nextPos;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    @Override
    public String toString() {
        switch (upper) {
            case NONE:
                return String.format("{%d}", lower);
            case INFINITY:
                return String.format("{%d,}", lower);
            default:
                return String.format("{%d,%d}", lower, upper);
        }
    }

    public static void main(String[] args) throws ExpressionException {
        // {1}
        String exp1 = "{1}";
        RepeatParser parser1 = RepeatParser.parser(exp1, 0);
        System.out.println(parser1);
        // {1,}
        String exp2 = "{1,}";
        RepeatParser parser2 = RepeatParser.parser(exp2, 0);
        System.out.println(parser2);
        // {1,2}
        String exp3 = "{1,2}";
        RepeatParser parser3 = RepeatParser.parser(exp3, 0);
        System.out.println(parser3);
    }
}
