package com.sine_x.regexp;

import com.sine_x.regexp.node.MatchedNode;
import com.sine_x.regexp.node.Node;
import com.sine_x.regexp.node.SingleNode;
import com.sine_x.regexp.node.SplitNode;
import com.sine_x.regexp.parser.NodeParser;


import java.util.ArrayList;
import java.util.Stack;

public class Pattern {

    public static boolean DEBUG_MODE = true;   // Debug mode switch
    public static int ID = 0;                  // Node ID

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
        int i = 0;
        while (i < exp.length()) {
            char c = exp.charAt(i);
            int next = i + 1;
            switch (c) {
                case '|': case '(':
                    // explicit operator
                    opStack.push(c);
                    opPosStack.push(i);
                    break;
                case '+':
                    // Repeat once or more times
                    fragStack.push(repeatOneOrMore(fragStack.pop()));
                    break;
                case '?':
                    // Don't repeat or repeat once
                    fragStack.push(repeatNoneOrOnce(fragStack.pop()));
                    break;
                case '*':
                    // Repeat any time
                    fragStack.push(repeatAnyTime(fragStack.pop()));
                    break;
                case ')': {           // right parenthese
                    int lp = -1;
                    while (!opStack.empty() && opStack.peek() == '|') {
                        Fragment fragment1 = fragStack.pop();
                        Fragment fragment2 = fragStack.pop();
                        fragStack.push(alternates(fragment1, fragment2));
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
                        fragStack.push(concatenate((fragment1, fragment2)));
                    }
                    break;
                } default: {
                    // node
                    NodeParser nodeParser = NodeParser.parseNode(exp, i);
                    SingleNode node = nodeParser.getNode();
                    next = nodeParser.getNextPos();
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
            fragStack.push(new Fragment(fragment1.start, fragment2.ends));
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

    // NFA connect functions

    /**
     * Concatenate teo fragment
     * @param fragment1 Fragment 1
     * @param fragment2 Fragment 2
     * @return Concatenated fragment
     */
    private static Fragment concatenate(Fragment fragment1, Fragment fragment2) {
        patch(fragment1.ends, fragment2.start);
        return new Fragment(fragment1.start, fragment2.ends);
    }

    /**
     * Make alternates between fragment 1 and fragment 2
     * @param fragment1 Fragment 1
     * @param fragment2 Fragment 2
     * @return Alternates fragment
     */
    private static Fragment alternates(Fragment fragment1, Fragment fragment2) {
        SplitNode node = new SplitNode(fragment1.start, fragment2.start);
        ArrayList<Node> ends = new ArrayList<>();
        ends.addAll(fragment1.ends);
        ends.addAll(fragment2.ends);
        return new Fragment(node, ends);
    }

    /**
     * Don't repeat fragment or repeat once
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatNoneOrOnce(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.start, null);
        return new Fragment(node, append(fragment.ends, node));
    }

    /**
     * Repeat fragment once or more times
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatOneOrMore(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.start, null);
        patch(fragment.ends, node);
        return new Fragment(fragment.start, append(new ArrayList<>(), node));
    }

    /**
     * Repeat fragment any time
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatAnyTime(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.start, null);
        patch(fragment.ends, node);
        return new Fragment(node, append(new ArrayList<>(), node));
    }

    // helper functions for compiler

    /**
     * Set node "out" as the nextPos node of all nodes in exit nodes array
     * @param ends Exit nodes array
     * @param out Next exit node
     */
    private static void patch(ArrayList<Node> ends, Node out) {
        for (Node node : ends) {
            node.setOut(out);
        }
    }

    /**
     * Append node to a node array
     * @param nodes Node array
     * @param node A node
     * @return New node array
     */
    private static ArrayList<Node> append(ArrayList<Node> nodes, Node node) {
        nodes.add(node);
        return nodes;
    }

    // exception detector

    private static boolean assertStackSize() {

    }

    /**
     * Check stack size before pop
     * @param size Required size
     * @return If
     */
    private static boolean requireStackSize(int size) {

    }

    /**
     * NFA fragment
     */
    private static class Fragment {
        Node start;
        ArrayList<Node> ends = new ArrayList<>();
        Fragment(Node start) {
            this.start = start;
        }
        Fragment(Node start, ArrayList<Node> ends) {
            this(start);
            this.ends = ends;
        }
    }


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("a+|c*|b?");
        System.out.println(pattern.start);
    }

}
