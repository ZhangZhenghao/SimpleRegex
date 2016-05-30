package com.sine_x.regexp.exeception;

public class ExpressionException extends Exception {

    // Exception information
    private String regexp;
    private char operator;
    private int offset;

    public final static int MISSING_OPERANDS = 0;
    public final static int ILLEGAL_ESCAPE = 1;
    public final static int ILLEGAL_CLASS = 2;
    public final static int ILLEGAL_PARENTHESE = 3;
    public final static int ILLEGAL_REPEAT = 4;

    public ExpressionException(String regexp, int pos, int exception) {

    }
}
