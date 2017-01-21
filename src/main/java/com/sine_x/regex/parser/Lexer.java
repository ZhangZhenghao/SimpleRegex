package com.sine_x.regex.parser;

import com.sine_x.regex.BuildConfig;

import java.io.IOException;
import java.util.logging.Logger;

public class Lexer implements Parser.Lexer {

    private Logger logger = Logger.getGlobal();

    private String exp;
    private int i = 0;
    private char yylval;

    public Lexer(String exp) {
        this.exp = exp;
    }

    public int yylex() throws IOException {
        if (i >= exp.length())
            return EOF;
        yylval = exp.charAt(i++);
        switch (yylval) {
            case '+':
            case '?':
            case '*':
            case '|':
            case '.':
            case '(':
            case ')':
            case '\\':
            case '[':
            case '^':
            case '-':
            case ']':
            case '{':
            case ',':
            case '}':
            case 'u':
            case 'x':
                return yylval;
            default:
                if (Character.isDigit(yylval))
                    return DIGIT;
                return OTHER;
        }
    }

    public void yyerror(String msg) {
        if (BuildConfig.LOG)
            logger.warning(msg);
    }

    public Object getLVal() {
        return yylval;
    }
}
