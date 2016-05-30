package com.sine_x.regexp.util;

import com.sine_x.regexp.Pattern;
import com.sine_x.regexp.node.Node;
import com.sine_x.regexp.node.SingleNode;
import com.sine_x.regexp.node.SplitNode;

import java.util.ArrayList;

public class Fragment implements Cloneable {

    private int left;
    private Node start;
    private ArrayList<Node> ends = new ArrayList<>();

    public Fragment(int left, Node start) {
        this.left = left;
        this.start = start;
        this.ends.add(start);
    }

    public Fragment(int left, Node start, ArrayList<Node> ends) {
        this.left = left;
        this.start = start;
        this.ends = ends;
    }

    public Node getStart() {
        return start;
    }

    public ArrayList<Node> getEnds() {
        return ends;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public Fragment clone() throws CloneNotSupportedException {
        Fragment fragment = (Fragment) super.clone();
        fragment.ends = new ArrayList<>();
        Pattern.copyID++;
        fragment.start = clone(start, ends, fragment.ends, Pattern.copyID);
        return fragment;
    }

    private Node clone(Node node, ArrayList<Node> oldEnds, ArrayList<Node> newEnds, int copyID) throws CloneNotSupportedException {
        if (node.isAlter()) {
            SplitNode split = (SplitNode) node;
            SplitNode split1 = (SplitNode) split.clone(copyID);
            if (split.getOut1() != null && split.getOut1().getCopyID() != copyID)
                split1.setOut1(clone(split.getOut1(), oldEnds, newEnds, copyID));
            if (split.getOut2() != null && split.getOut2().getCopyID() != copyID)
                split1.setOut2(clone(split.getOut2(), oldEnds, newEnds, copyID));
            if (oldEnds.contains(split))
                newEnds.add(split1);
            return split1;
        } else {
            SingleNode single = (SingleNode) node;
            SingleNode single1 = (SingleNode) single.clone(copyID);
            if (single.getOut() != null && single.getOut().getCopyID() != copyID)
                single1.setOut(clone(single.getOut(), oldEnds, newEnds, copyID));
            if (oldEnds.contains(single))
                newEnds.add(single1);
            return single1;
        }
    }
}