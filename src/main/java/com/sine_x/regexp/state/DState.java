package com.sine_x.regexp.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DState {

    private Set<AbstractState> set;
    private Map<Character, DState> next = new HashMap<Character, DState>();

    public DState(Set<AbstractState> set) {
        this.set = set;
    }

    public Set<AbstractState> getSet() {
        return set;
    }

    public DState getNext(char c) {
        return next.get(c);
    }

    public void setNext(char c, DState state) {
        this.next.put(c, state);
    }
}
