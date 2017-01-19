package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.RegexException;

import java.io.IOException;
import java.util.logging.Logger;

public class Lexer implements Parser.Lexer {

    private Logger logger = Logger.getLogger("Lexer");

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
            case '+': case '?': case '*': case '|':
            case '.': case '(': case ')': case '\\':
            case '[': case '^': case '-': case ']':
            case '{': case ',': case '}': case 'u':
            case 'x':
                  return yylval;
            default:
                if (Character.isDigit(yylval))
                    return DIGIT;
                return OTHER;
        }
    }

    public void yyerror(String msg) {
        logger.warning(msg);
    }

    public Object getLVal() {
        return yylval;
    }
}
