package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.ExpressionException;

public class Lexer {

    // Type of token
    public enum Type {
        OPEN_PAREN,
        CLOSE_PAREN,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        OPEN_BRACE,
        CLOSE_BRACE,
        VERTICAL_BAR,
        DOT,
        STAR,
        PLUS,
        MINUS,
        QUESTION_MARK,
        COMMA,
        DOLLAR,
        EXPONENT,
        CHARACTER,
        SET,
        EOF
    }

    private Type type;      // Type of this token
    private char lexeme;    // Lexeme of this token
    private int forward;    // Next position of this token

    /**
     * Lexical analyzer
     * @param str String
     * @param begin Begin position
     * @throws ExpressionException Exception in lexical analyze stage
     */
    Lexer(String str, int begin) throws ExpressionException {
        // EOF
        if (begin >= str.length()) {
            type = Type.EOF;
            return;
        }
        // Not EOF
        switch (str.charAt(begin)) {
            // Reserved character
            case '(':
                type = Type.OPEN_PAREN;
                break;
            case ')':
                type = Type.CLOSE_PAREN;
                break;
            case '[':
                type = Type.OPEN_BRACKET;
                break;
            case ']':
                type = Type.CLOSE_BRACKET;
                break;
            case '{':
                type = Type.OPEN_BRACE;
                break;
            case '}':
                type = Type.CLOSE_BRACE;
                break;
            case '|':
                type = Type.VERTICAL_BAR;
                break;
            case '.':
                type = Type.DOT;
                break;
            case '*':
                type = Type.STAR;
                break;
            case '+':
                type = Type.PLUS;
                break;
            case '-':
                type = Type.MINUS;
                break;
            case '?':
                type = Type.QUESTION_MARK;
                break;
            case ',':
                type = Type.COMMA;
                break;
            case '$':
                type = Type.DOLLAR;
                break;
            case '^':
                type = Type.EXPONENT;
                break;
            // Escape
            case '\\':
                begin++;
                // EOF
                if (begin == str.length())
                    throw new ExpressionException(str, begin-1, ExpressionException.ILLEGAL_ESCAPE);
                // Not EOF
                switch (str.charAt(begin)) {
                    // Escape character
                    case '[': case ']': case '(': case ')': case '{': case '}':
                    case '+': case '-': case '*': case '|': case '?': case '^':
                    case '$': case ',': case '.': case '\\':
                        type = Type.CHARACTER;
                        lexeme = str.charAt(begin);
                        break;
                    // Set
                    case 'b': case 'B': case 'w': case 'W':
                    case 's': case 'S': case 'd': case 'D':
                        type = Type.SET;
                        lexeme = str.charAt(begin);
                        break;
                    // Exception
                    default:
                        throw new ExpressionException(str, begin - 1, ExpressionException.ILLEGAL_ESCAPE);
                }
                break;
            // Common character
            default:
                type = Type.CHARACTER;
                lexeme = str.charAt(begin);
        }
        forward = begin+1;
    }

    /**
     * Get the lexeme of token
     * @return Lexeme of token
     */
    public char getLexeme() {
        return lexeme;
    }

    /**
     * Get the next position of token
     * @return Next position of token
     */
    public int getForward() {
        return forward;
    }

    /**
     * Get the type of token
     * @return Type of token
     */
    public Type getType() {
        return type;
    }
}
