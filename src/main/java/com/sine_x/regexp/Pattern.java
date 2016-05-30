package com.sine_x.regexp;

import com.sine_x.regexp.exeception.ExpressionException;
import com.sine_x.regexp.node.MatchedNode;
import com.sine_x.regexp.node.Node;
import com.sine_x.regexp.node.SingleNode;
import com.sine_x.regexp.node.SplitNode;
import com.sine_x.regexp.parser.NodeParser;
import com.sine_x.regexp.parser.RepeatParser;
import com.sine_x.regexp.util.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Pattern {

    public static boolean DEBUG_MODE = true;   // Debug mode switch
    public static int nodeID;                   // Node nodeID
    public static int copyID;                   // Node copyID;

    private int setID ;                         // Set nodeID
    private Node start;                         // NFA entry

    /**
     * match Match the text string with this pattern
     * @param text Text string
     * @return Boolean
     */
    public boolean match(String text) {
        Set<Node> set1 = startSet(start);
        Set<Node> set2 = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            step(set1, ch, set2);
            Set<Node> temp = set1;
            set1 = set2;
            set2 = temp;
            // if set is empty, failed
            if (set1.isEmpty()) {
                return false;
            }
        }
        return isMatch(set1);
    }

    /**
     * Find matched state in state set
     * @param set state set
     * @return If matched
     */
    boolean isMatch(Set<Node> set) {
        for (Node node : set)
            if (node.isMatched())
                return true;
        return false;
    }

    /**
     * Construct start state set
     * @param node Start node
     * @return Start state set
     */
    private Set<Node> startSet(Node node) {
        setID++;
        Set<Node> set = new HashSet<>();
        addState(set, node);
        return set;
    }

    /**
     * Add a node to set and do split automatically
     * @param set State set
     * @param node Node need to be added
     */
    private void addState(Set<Node> set, Node node) {
        if (node == null || node.getLastSet() == setID)
            return;
        node.setLastSet(setID);
        if (node.isAlter()) {
            SplitNode split = (SplitNode) node;
            addState(set, split.getOut1());
            addState(set, split.getOut2());
            return;
        }
        set.add(node);
    }

    /**
     * Advance NFA by matching character
     * @param set1 Current set
     * @param c Target character
     * @param set2 Next set
     */
    private void step(Set<Node> set1, char c, Set<Node> set2) {
        set2.clear();
        setID++;
        for (Node node : set1)
            if (node.match(c))
                addState(set2, ((SingleNode) node).getOut());
    }

    /**
     * compile Compile a regular expression string to a Thompson NFA
     * @param exp Regular expression string
     * @return Pattern
     */
    public static Pattern compile(String exp) throws ExpressionException, CloneNotSupportedException {
        // debug code
        if (DEBUG_MODE) nodeID = 0;

        Stack<Fragment> fragStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();
        Stack<Integer> opPosStack = new Stack<>();
        int offset = 0;
        while (offset < exp.length()) {
            char c = exp.charAt(offset);
            int next = offset + 1;
            switch (c) {
                case '|': case '(':
                    // explicit operator
                    opStack.push(c);
                    opPosStack.push(offset);
                    break;
                case '+':
                    // Repeat once or more times
                    requireStackSize(exp, offset, fragStack, 1);
                    fragStack.push(repeatOnceOrMore(fragStack.pop()));
                    break;
                case '?':
                    // Don't repeat or repeat once
                    requireStackSize(exp, offset, fragStack, 1);
                    fragStack.push(repeatNoneOrOnce(fragStack.pop()));
                    break;
                case '*':
                    // Repeat any time
                    requireStackSize(exp, offset, fragStack, 1);
                    fragStack.push(repeatAnyTimes(fragStack.pop()));
                    break;
                case '{':
                    // Repeat
                    RepeatParser repeat = RepeatParser.parser(exp, offset);
                    if (repeat.getUpper() == RepeatParser.NONE && repeat.getLower() > 0)
                        fragStack.push(repeatN(fragStack.pop(), repeat.getLower()));
                    else if (repeat.getUpper() == RepeatParser.INFINITY)
                        fragStack.push(repeatNToInfinity(fragStack.pop(), repeat.getLower()));
                    else
                        fragStack.push(repeatNtoM(fragStack.pop(), repeat.getLower(), repeat.getUpper()));
                    next = repeat.getNextPos();
                    break;
                case ')':
                    // right parenthesis
                    while (!opStack.empty() && opStack.peek() == '|') {
                        requireStackSize(exp, offset, fragStack, 2);
                        Fragment fragment1 = fragStack.pop();
                        Fragment fragment2 = fragStack.pop();
                        fragStack.push(alternates(fragment1, fragment2));
                        opStack.pop();
                        opPosStack.pop();
                    }
                    if (!opStack.empty() && opStack.peek() == '(') {
                        opStack.pop();
                        fragStack.peek().setLeft(opPosStack.pop());
                    } else {
                        throw new ExpressionException(exp, offset, ExpressionException.ILLEGAL_PARENTHESE);
                    }
                    break;
                default:
                    // node
                    NodeParser nodeParser = NodeParser.parseNode(exp, offset);
                    next = nodeParser.getNextPos();
                    fragStack.push(new Fragment(offset, nodeParser.getNode()));
            }
            autoConcatenate(fragStack, opPosStack, exp, next);
            // update offset
            offset = next;
        }
        while (!opStack.empty() && opStack.peek() == '|') {
            requireStackSize(exp, offset, fragStack, 2);
            Fragment fragment1 = fragStack.pop();
            Fragment fragment2 = fragStack.pop();
            fragStack.push(alternates(fragment1, fragment2));
            opStack.pop();
            opPosStack.pop();
        }
        // Make pattern
        Pattern pattern = new Pattern();
        pattern.start = concatenate(fragStack.pop(), new Fragment(exp.length(), new MatchedNode())).getStart();
        return pattern;
    }

    /**
     * Concatenate with previous fragment automatically
     * @param fragStack The stack save fragments
     * @param opPosStack The stack save the position of explicit operator
     * @param exp The regexp string
     * @param next The next position begin parsing
     */
    private static void autoConcatenate(Stack<Fragment> fragStack, Stack<Integer> opPosStack, String exp, int next) {
        // a postfix operator in the next position, don't concatenate
        if (next < exp.length() &&
                (exp.charAt(next) == '+'
                || exp.charAt(next) == '?'
                || exp.charAt(next) == '*'
                || exp.charAt(next) == '{'))
            return;
        // two few fragments
        if (fragStack.size() < 2)
            return;
        // no explicit operator in the previous position, do concatenate
        if (exp.charAt(fragStack.peek().getLeft()-1) != '|'
                && exp.charAt(fragStack.peek().getLeft()-1) != '(') {
            Fragment fragment2 = fragStack.pop();
            Fragment fragment1 = fragStack.pop();
            fragStack.push(concatenate(fragment1, fragment2));
        }
    }

    /**
     * Concatenate teo fragment
     * @param fragment1 Fragment 1
     * @param fragment2 Fragment 2
     * @return Concatenated fragment
     */
    private static Fragment concatenate(Fragment fragment1, Fragment fragment2) {
        patch(fragment1.getEnds(), fragment2.getStart());
        return new Fragment(fragment1.getLeft(), fragment1.getStart(), fragment2.getEnds());
    }

    /**
     * Make alternates between fragment 1 and fragment 2
     * @param fragment1 Fragment 1
     * @param fragment2 Fragment 2
     * @return Alternates fragment
     */
    private static Fragment alternates(Fragment fragment1, Fragment fragment2) {
        SplitNode node = new SplitNode(fragment1.getStart(), fragment2.getStart());
        ArrayList<Node> ends = new ArrayList<>();
        ends.addAll(fragment1.getEnds());
        ends.addAll(fragment2.getEnds());
        return new Fragment(Math.min(fragment1.getLeft(), fragment2.getLeft()), node, ends);
    }

    /**
     * Don't repeat fragment or repeat once
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatNoneOrOnce(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.getStart());
        return new Fragment(fragment.getLeft(), node, append(fragment.getEnds(), node));
    }

    /**
     * Repeat fragment once or more times
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatOnceOrMore(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.getStart());
        patch(fragment.getEnds(), node);
        return new Fragment(fragment.getLeft(), fragment.getStart(), append(new ArrayList<>(), node));
    }

    /**
     * Repeat fragment any time
     * @param fragment Fragment need repeating
     * @return Repeated fragment
     */
    private static Fragment repeatAnyTimes(Fragment fragment) {
        SplitNode node = new SplitNode(fragment.getStart());
        patch(fragment.getEnds(), node);
        return new Fragment(fragment.getLeft(), node, append(new ArrayList<>(), node));
    }

    /**
     * Repeat fragment for N times
     * @param fragment Fragment need repeating
     * @param N Repeat time
     * @return Repeated fragment
     */
    private static Fragment repeatN(Fragment fragment, int N) throws CloneNotSupportedException {
        Fragment fragment1 = fragment;
        for (int i = 1; i < N; i++)
            fragment1 = concatenate(fragment1, fragment.clone());
        return fragment1;
    }

    /**
     * Repeat fragment for at least N times
     * @param fragment Fragment need repeating
     * @param N At least N times
     * @return Repeated fragment
     */
    private static Fragment repeatNToInfinity(Fragment fragment, int N) throws CloneNotSupportedException {
        if (N == 0)
            return repeatAnyTimes(fragment);
        if (N == 1)
            return repeatOnceOrMore(fragment);
        Fragment fragment1 = fragment;
        for (int i = 2; i < N; i++)
            fragment1 = concatenate(fragment1, fragment.clone());
        return concatenate(fragment1, repeatOnceOrMore(fragment.clone()));
    }

    /**
     * Repeat fragment
     * @param fragment Fragment need repeating
     * @param N At least N times
     * @param M At most M times
     * @return Repeated fragment
     */
    private static Fragment repeatNtoM(Fragment fragment, int N, int M) throws CloneNotSupportedException {
        if (N == M)
            return repeatN(fragment, N);
        if (N == 0) {
            Fragment fragment1 = repeatNoneOrOnce(fragment.clone());
            for (int i = 1; i < M; i++)
                fragment1 = concatenate(fragment1, repeatNoneOrOnce(fragment.clone()));
            return fragment1;
        } else {
            Fragment fragment1 = fragment;
            for (int i = 1; i < N; i++)
                fragment1 = concatenate(fragment1, fragment.clone());
            for (int i = N; i < M; i++)
                fragment1 = concatenate(fragment1, repeatNoneOrOnce(fragment.clone()));
            return fragment1;
        }
    }

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

    /**
     * Check the size of opStack, otherwise throw exception
     * @param regexp Regexp string
     * @param pos Position in regexp string
     * @param stack OperationStack
     * @param size Require size
     * @throws ExpressionException
     */
    private static void requireStackSize(String regexp, int pos, Stack stack, int size) throws ExpressionException {
        if (stack.size() < size)
            throw new ExpressionException(regexp, pos, ExpressionException.MISSING_OPERANDS);
    }

    public static void main(String[] args) throws Exception {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
        System.out.println(pattern.match("172.0.0.1"));
    }

}
