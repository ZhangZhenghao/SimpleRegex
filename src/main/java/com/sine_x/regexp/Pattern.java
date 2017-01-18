package com.sine_x.regexp;

import com.sine_x.regexp.exeception.RegexException;
import com.sine_x.regexp.parser.Lexer;
import com.sine_x.regexp.parser.Parser;
import com.sine_x.regexp.state.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Pattern {

    private int setID;
    private AbstractState start;

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
        Set<AbstractState> set1 = startSet(start);
        Set<AbstractState> set2 = new HashSet<AbstractState>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            step(set1, ch, set2);
            Set<AbstractState> temp = set1;
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
    boolean isMatch(Set<AbstractState> set) {
        for (AbstractState abstractState : set)
            if (abstractState.isMatched())
                return true;
        return false;
    }

    /**
     * Construct start state set
     * @param abstractState Start abstractState
     * @return Start state set
     */
    private Set<AbstractState> startSet(AbstractState abstractState) {
        setID++;
        Set<AbstractState> set = new HashSet<AbstractState>();
        addState(set, abstractState);
        return set;
    }

    /**
     * Add a abstractState to set and do split automatically
     * @param set State set
     * @param abstractState AbstractState need to be added
     */
    private void addState(Set<AbstractState> set, AbstractState abstractState) {
        if (abstractState == null || abstractState.getLastSet() == setID)
            return;
        abstractState.setLastSet(setID);
        if (abstractState.isAlter()) {
            SplitState split = (SplitState) abstractState;
            addState(set, split.getOut1());
            addState(set, split.getOut2());
            return;
        }
        set.add(abstractState);
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
}
