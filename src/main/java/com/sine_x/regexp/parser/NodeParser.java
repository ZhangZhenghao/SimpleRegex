package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.ExpressionException;
import com.sine_x.regexp.node.CharClassNode;
import com.sine_x.regexp.node.CharNode;
import com.sine_x.regexp.node.SingleNode;

import java.util.Arrays;

public class NodeParser {

    // Escape character
    private static final char[] ESCAPE_CHARS = {'.', '\\', '[', ']', '(', ')', '{', '}', '+', '*', '?', '|', '^', '$'};
    private static final char[] CHARARCTER_CLASS = {'d', 'D'};

    private int nextPos;
    private SingleNode node;

    public int getNextPos() {
        return nextPos;
    }

    public SingleNode getNode() {
        return node;
    }

    public static NodeParser parseNode(String regexp, int pos) throws ExpressionException {
        NodeParser parser = new NodeParser();
        char ch = regexp.charAt(pos);
        switch (ch) {
            case '\\':
                pos++;
                if (pos >= regexp.length())
                    throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_ESCAPE);
                char escapeChar = regexp.charAt(pos);
                // Escape characters
                for (char c : ESCAPE_CHARS)
                    if (escapeChar == c) {
                        parser.node = new CharNode(escapeChar);
                        parser.nextPos = pos + 1;
                        break;
                    }
                // Character class

                if (escapeChar == 'd') {
                    CharClassNode node = new CharClassNode();
                    node.addRange('0', '9');
                    parser.node = node;
                    parser.nextPos = pos + 1;
                    break;
                }
                // Assertion
                break;
            case '^':
            case '$':
            case '[':
                // parse set
                int offset = pos + 1;
                // parse complement
                CharClassNode node = new CharClassNode();
                if (regexp.charAt(offset) == '^') {
                    node.setComplement(true);
                    offset++;
                }
                while (regexp.charAt(offset) != ']') {
                    node.addChar(regexp.charAt(offset));
                    offset++;
                }
                parser.node = node;
                parser.nextPos = offset+1;
                break;
            default:
                // Normal character
                parser.node = new CharNode(ch);
                parser.nextPos = pos + 1;
        }
        return parser;
    }
}
