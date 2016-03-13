package com.sine_x.regexp;

import com.sine_x.regexp.node.MatchedNode;
import com.sine_x.regexp.node.Node;
import com.sine_x.regexp.node.SingleNode;

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
        node1.setOut(node2);
        node2.setOut(node3);
        node3.setOut(new MatchedNode());
        return pattern;
    }

    public static void main(String[] args) {

    }

}
