package com.sine_x.regexp;

import java.util.ArrayList;
import java.util.Stack;

public class Pattern {

    private Node start;

    /**
     * match Match the text string with this pattern
     * @param text Text string
     * @return Boolean
     */
    public boolean match(String text) {
        return true;
    }

    /**
     * compile Compile a regular expression string to a Thompson NFA
     * @param exp Regular expression string
     * @return Pattern
     */
    public static Pattern compile(String exp) {
        Pattern pattern = new Pattern();
        SingleNode node1 = new SingleNode('a');
        SingleNode node2 = new SingleNode('b');
        SingleNode node3 = new SingleNode('c');
        node1.out = node2;
        node2.out = node3;
        node3.out = new MatchedNode();
        return pattern;
    }

    /**
     * NFA fragment
     */
    static class Fragment {
        public Node start;
        public ArrayList<Node> out;
        public Fragment(Node start) {
            this.start = start;
            this.out = new ArrayList<>();
        }
        public Fragment(Node start, ArrayList<Node> out) {
            this.start = start;
            this.out = out;
        }
    }

    /**
     * Abstract node
     */
    static abstract class Node {
        /**
         * match Whether text can bypass the Node
         * @param c A Character in text
         * @return Boolean
         */
        public boolean match(char c) {
            return false;
        }
        /**
         * isMatched Whether text arrived matched state
         * @return Boolean
         */
        public boolean isMatched() {
            return false;
        }
        /**
         * isAlter Whether text have multiple choice
         * @return Boolean
         */
        public boolean isAlter() {
            return false;
        }
    }

    /**
     * Matched state node
     */
    static class MatchedNode extends Node {
        @Override
        public boolean isMatched() {
            return true;
        }
    }

    /**
     * Normal state node
     */
    static class SingleNode extends Node {
        private char c;
        public Node out;
        public SingleNode(char c) {
            this.c = c;
        }
        @Override
        public boolean match(char c) {
            return this.c == c;
        }
    }

    /**
     * Split state node
     */
    static class SplitNode extends Node {
        public Node out1, out2;
        public SplitNode(Node out1, Node out2) {
            this.out1 = out1;
            this.out2 = out2;
        }
    }


}
