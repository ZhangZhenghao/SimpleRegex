package com.sine_x.regexp.parser;

import com.sine_x.regexp.exeception.ExpressionException;
import com.sine_x.regexp.node.CharClassNode;
import com.sine_x.regexp.node.CharNode;
import com.sine_x.regexp.node.SingleNode;
import com.sine_x.regexp.node.WildCardNode;

public class NodeParser {

    // Escape character
    private static final char[] ESCAPE_CHARS = {'.', '\\', '[', ']', '(', ')', '{', '}', '+', '*', '?', '|', '^', '$'};

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
            case '.':
                parser.node = new WildCardNode();
                parser.nextPos = pos + 1;
                return parser;
            case '\\':
                pos++;
                if (pos >= regexp.length())
                    throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_ESCAPE);
                char escapeChar = regexp.charAt(pos);
                parser.nextPos = pos + 1;
                // Escape characters
                for (char c : ESCAPE_CHARS)
                    if (escapeChar == c) {
                        parser.node = new CharNode(escapeChar);
                        break;
                    }
                // Character class
                boolean isClass = true;
                CharClassNode charClassNode = new CharClassNode();
                switch (escapeChar) {
                    case 'D':
                        charClassNode.setComplement(true);
                    case 'd':
                        charClassNode.addRange('0', '9');
                        break;
                    case 'S':
                        charClassNode.setComplement(true);
                    case 's':
                        charClassNode.addChar('\t');
                        charClassNode.addChar('\n');
                        charClassNode.addChar('\f');
                        charClassNode.addChar('\r');
                        break;
                    case 'W':
                        charClassNode.setComplement(true);
                    case 'w':
                        charClassNode.addRange('0', '9');
                        charClassNode.addRange('A', 'Z');
                        charClassNode.addRange('a', 'z');
                        charClassNode.addChar('_');
                        break;
                    default:
                        isClass = false;
                }
                if (isClass) {
                    parser.node = charClassNode;
                    return parser;
                }
                // Assertion
                throw new ExpressionException(regexp, pos, ExpressionException.ILLEGAL_ESCAPE);
            // case '^':
            // case '$':
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
                return parser;
            default:
                // Normal character
                parser.node = new CharNode(ch);
                parser.nextPos = pos + 1;
                return parser;
        }
    }
}
