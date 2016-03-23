package com.sine_x.regexp;

import java.util.ArrayList;
import java.util.Stack;

public class Pattern {

    private static boolean DEBUG_MODE = true;   // Debug mode switch
    private static int ID = 0;                  // Node ID

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
        if (DEBUG_MODE) {
            ID = 0;
        }
        Stack<Fragment> fragStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();
        Stack<Integer> opPosStack = new Stack<>();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            switch (c) {
                case '|': case '(': {   // explicit operator
                    opStack.push(c);
                    opPosStack.push(i);
                    break;
                } case '+': {           // repeats one or more
                    Fragment fragment = fragStack.pop();
                    SplitNode node = new SplitNode(fragment.start, null);
                    patch(fragment.ends, node);
                    fragStack.push(new Fragment(fragment.start, append(new ArrayList<>(), node)));
                    break;
                } case '?': {           // repeats zero or one
                    Fragment fragment = fragStack.pop();
                    SplitNode node = new SplitNode(fragment.start, null);
                    fragStack.push(new Fragment(node, append(fragment.ends, node)));
                    break;
                } case '*': {           // repeats any time
                    Fragment fragment = fragStack.pop();
                    SplitNode node = new SplitNode(fragment.start, null);
                    patch(fragment.ends, node);
                    fragStack.push(new Fragment(node, append(new ArrayList<>(), node)));
                    break;
                } case ')': {           // right parenthese
                    int lp = -1;
                    while (!opStack.empty() && opStack.peek() == '|') {
                        Fragment fragment1 = fragStack.pop();
                        Fragment fragment2 = fragStack.pop();
                        SplitNode node = new SplitNode(fragment1.start, fragment2.start);
                        ArrayList<Node> ends = new ArrayList<>();
                        ends.addAll(fragment1.ends);
                        ends.addAll(fragment2.ends);
                        fragStack.push(new Fragment(node, ends));
                        opStack.pop();
                        opPosStack.pop();
                    }
                    if (!opStack.empty() && opStack.peek() == '(') {
                        opStack.pop();
                        lp = opPosStack.pop();
                    } else {
                        throw new IllegalArgumentException("Wrong regular expression: " + exp);
                    }
                    // try to connect with last fragment
                    if (fragStack.size() > 1 && (opPosStack.empty() || opPosStack.peek() != lp-1)) {
                        Fragment fragment2 = fragStack.pop();
                        Fragment fragment1 = fragStack.pop();
                        patch(fragment1.ends, fragment2.start);
                        fragStack.push(new Fragment(fragment1.start, fragment2.ends));
                    }
                    break;
                } default: {                // character
                    SingleNode node = new SingleNode(c);
                    if (fragStack.empty() || !opPosStack.empty() && opPosStack.peek() == i-1) { // no implicit operator
                        fragStack.push(new Fragment(node, append(new ArrayList<>(), node)));
                    } else {                // has implicit connect operator
                        Fragment fragment = fragStack.pop();
                        patch(fragment.ends, node);
                        fragStack.push(new Fragment(fragment.start, append(new ArrayList<>(), node)));
                    }
                }
            }
        }
        while (!opStack.empty() && opStack.peek() == '|') {
            Fragment fragment1 = fragStack.pop();
            Fragment fragment2 = fragStack.pop();
            SplitNode node = new SplitNode(fragment1.start, fragment2.start);
            ArrayList<Node> ends = new ArrayList<>();
            ends.addAll(fragment1.ends);
            ends.addAll(fragment2.ends);
            fragStack.push(new Fragment(node, ends));
            opStack.pop();
            opPosStack.pop();
        }
        Fragment fragment = fragStack.pop();
        MatchedNode node = new MatchedNode();
        patch(fragment.ends, node);
        Pattern pattern = new Pattern();
        pattern.start = fragment.start;
        return pattern;
    }

    /**
     * patch Set out node as the next node of each node in ends array
     * @param ends Ends array
     * @param out Next node
     */
    private static void patch(ArrayList<Node> ends, Node out) {
        for (Node node : ends) {
            node.setOut(out);
        }
    }

    /**
     * append Append node to a node array
     * @param nodes Node array
     * @param node A node
     * @return New node array
     */
    private static ArrayList<Node> append(ArrayList<Node> nodes, Node node) {
        nodes.add(node);
        return nodes;
    }

    /**
     * Lexer: Lexical analyse
     */
    static class Lexer {
        public enum Type {NODE, PLUS, QM, STAR, LP, RP, OR};
        public int next;
        public Type type;
        public SingleNode node;
        public Lexer parse(String exp, int i) {
            Lexer lexer = new Lexer();
            lexer.type = Type.NODE;
            return lexer;
        }
    }

    /**
     * NFA fragment
     */
    static class Fragment {
        public Node start;
        public ArrayList<Node> ends = new ArrayList<>();
        public Fragment(Node start) {
            this.start = start;
        }
        public Fragment(Node start, ArrayList<Node> ends) {
            this(start);
            this.ends = ends;
        }
    }

    /**
     * Abstract node
     */
    static abstract class Node {
        private int id;
        public Node() {
            if (DEBUG_MODE) {
                id = ID++;
            }
        }
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
        /**
         * setOut Set the next node of current node
         * @param out Next node
         */
        public void setOut(Node out) {

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
        @Override
        public String toString() {
            return "[" + super.id + "]Matched";
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
        @Override
        public void setOut(Node out) {
            this.out = out;
            if (DEBUG_MODE && out != null) {
                System.out.printf("%s -> %s\n", this, out);
            }
        }
        @Override
        public String toString() {
            return "[" + super.id + "]" + c;
        }
    }

    /**
     * Split state node
     */
    static class SplitNode extends Node {
        public Node out1, out2;
        @Override
        public boolean isAlter() {
            return true;
        }
        @Override
        public void setOut(Node out) {
            this.out1 = out;
            if (DEBUG_MODE && out != null) {
                System.out.printf("%s -> %s\n", this, out);
            }
        }
        public SplitNode(Node out1, Node out2) {
            this.out1 = out1;
            this.out2 = out2;
            if (DEBUG_MODE && out1 != null) {
                System.out.printf("%s -> %s\n", this, out1);
            }
            if (DEBUG_MODE && out2 != null) {
                System.out.printf("%s -> %s\n", this, out2);
            }
        }
        @Override
        public String toString() {
            return "[" + super.id + "]Split";
        }
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("a+|c*|b?");
        System.out.println(pattern.start);
    }

}
