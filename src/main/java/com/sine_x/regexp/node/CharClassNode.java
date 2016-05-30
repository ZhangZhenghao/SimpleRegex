package com.sine_x.regexp.node;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class CharClassNode extends SingleNode {

    private boolean complement = false;
    private RangeSet<Character> set;

    public CharClassNode() {
        set = TreeRangeSet.create();
    }

    public void setComplement(boolean complement) {
        this.complement = complement;
    }

    public void addRange(char a, char b) {
        set.add(Range.closed(a, b));
    }

    public void addChar(char c) {
        set.add(Range.closed(c, c));
    }

    public boolean match(char c) {
        return complement ^ set.contains(c);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
