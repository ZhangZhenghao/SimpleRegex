package com.sine_x.regexp;

import com.sine_x.regexp.exeception.RegexException;
import com.sine_x.regexp.parser.Lexer;
import com.sine_x.regexp.parser.Parser;
import com.sine_x.regexp.state.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pattern {

    private int setID;
    private AbstractState start;
    private Map<Set<AbstractState>, DState> map = new HashMap<Set<AbstractState>, DState>();

    public void setStart(AbstractState start) {
        this.start = start;
    }

    public static Pattern compile(String exp) throws RegexException{
        Pattern pattern = new Pattern();
        Parser parser = new Parser(new Lexer(exp), pattern);
        try {
            if (!parser.parse())
                throw new RegexException("Parsing failed");
        } catch (RegexException e) {
            throw e;
        } catch (IOException e) {
            throw new RegexException(e.getMessage());
        }
        return pattern;
    }

    /**
     * match Match the text string with this pattern
     * @param text Text string
     * @return Boolean
     */
    public boolean match(String text) {
        DState state = determine(startSet());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            DState next = state.getNext(c);
            if (next == null) {
                Set<AbstractState> set = new HashSet<AbstractState>();
                step(state.getSet(), c, set);
                next = determine(set);
                state.setNext(c, next);
            }
            state = next;
        }
        return isMatch(state.getSet());
    }

    /**
     * Find matched state in state set
     * @param set state set
     * @return If matched
     */
    boolean isMatch(Set<AbstractState> set) {
        for (AbstractState abstractState : set)
            if (abstractState.isMatched())
                return true;
        return false;
    }

    /**
     * Construct start state set
     * @return Start state set
     */
    private Set<AbstractState> startSet() {
        setID++;
        Set<AbstractState> set = new HashSet<AbstractState>();
        addState(set, start);
        return set;
    }

    /**
     * Add a state to set and do split automatically
     * @param set State set
     * @param state AbstractState need to be added
     */
    private void addState(Set<AbstractState> set, AbstractState state) {
        if (state.getLastSet() == setID)
            return;
        state.setLastSet(setID);
        if (state.isAlter()) {
            SplitState split = (SplitState) state;
            addState(set, split.getOut1());
            addState(set, split.getOut2());
            return;
        }
        set.add(state);
    }

    /**
     * Advance NFA by matching character
     * @param set1 Current set
     * @param c Target character
     * @param set2 Next set
     */
    private void step(Set<AbstractState> set1, char c, Set<AbstractState> set2) {
        set2.clear();
        setID++;
        for (AbstractState abstractState : set1)
            if (abstractState.match(c))
                addState(set2, ((State) abstractState).getOut());
    }

    /**
     * Convert state set to DFA state
     * @param set State set
     * @return DFA state
     */
    private DState determine(Set<AbstractState> set) {
        if (map.get(set) == null)
            map.put(set, new DState(set));
        return map.get(set);
    }
}
