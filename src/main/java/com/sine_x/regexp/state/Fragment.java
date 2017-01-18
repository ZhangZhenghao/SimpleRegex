package com.sine_x.regexp.state;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Fragment implements Cloneable {

    private AbstractState start;
    private ArrayList<AbstractState> ends = new ArrayList<AbstractState>();

    public Fragment(AbstractState start) {
        this.start = start;
        this.ends.add(start);
    }

    public Fragment(AbstractState start, ArrayList<AbstractState> ends) {
        this.start = start;
        this.ends = ends;
    }

    public AbstractState getStart() {
        return start;
    }

    public ArrayList<AbstractState> getEnds() {
        return ends;
    }

    /**
     * Set the state "out" as the out state of all nodes in the end nodes array
     * @param out  Out state
     */
    public void patch(AbstractState out) {
        for (AbstractState abstractState : ends)
            abstractState.setOut(out);
    }

    /**
     * Append a state to a state array
     * @param abstractStates State array
     * @param abstractState A state
     * @return New state array
     */
    private static ArrayList<AbstractState> append(ArrayList<AbstractState> abstractStates, AbstractState abstractState) {
        abstractStates.add(abstractState);
        return abstractStates;
    }

    /**
     * Concatenate two fragments
     * @param fragment2 Fragment 2
     * @return Concatenated fragment
     */
    public Fragment concatenate(Fragment fragment2) {
        patch(fragment2.getStart());
        return new Fragment(getStart(), fragment2.getEnds());
    }

    /**
     * Make alternates between two fragments
     * @param fragment2 Fragment 2
     * @return Alternates fragment
     */
    public Fragment alternates(Fragment fragment2) {
        SplitState node = new SplitState(getStart(), fragment2.getStart());
        ArrayList<AbstractState> tempEnds = new ArrayList<AbstractState>();
        tempEnds.addAll(getEnds());
        tempEnds.addAll(fragment2.getEnds());
        return new Fragment(node, tempEnds);
    }

    /**
     * Repeat fragment once or more times
     * @return Repeated fragment
     */
    public Fragment repeatOnceOrMore() {
        SplitState node = new SplitState(getStart());
        patch(node);
        return new Fragment(getStart(), append(new ArrayList<AbstractState>(), node));
    }

    /**
     * Don't repeat fragment or repeat once
     * @return Repeated fragment
     */
    public Fragment repeatNoneOrOnce() {
        SplitState node = new SplitState(getStart());
        return new Fragment(node, append(getEnds(), node));
    }

    /**
     * Repeat fragment any time
     * @return Repeated fragment
     */
    public Fragment repeatAnyTimes() {
        SplitState node = new SplitState(getStart());
        patch(node);
        return new Fragment(node, append(new ArrayList<AbstractState>(), node));
    }

    /**
     * Repeat fragment for n times
     * @param n Repeat time
     * @return Repeated fragment
     */
    public Fragment repeatN(int n) throws CloneNotSupportedException {
        Fragment fragment = this;
        Fragment oldFragment = (Fragment) this.clone();
        for (int i = 1; i < n; i++)
            fragment = fragment.concatenate((Fragment) oldFragment.clone());
        return fragment;
    }

    /**
     * Repeat fragment for at least n times
     * @param n At least n times
     * @return Repeated fragment
     */
    public Fragment repeatNToInf(int n) throws CloneNotSupportedException {
        if (n == 0)
            return repeatAnyTimes();
        if (n == 1)
            return repeatOnceOrMore();
        Fragment fragment = this;
        Fragment oldFragment = (Fragment) this.clone();
        for (int i = 2; i < n; i++)
            fragment = fragment.concatenate((Fragment) oldFragment.clone());
        return fragment.concatenate(oldFragment.repeatOnceOrMore());
    }

    /**
     * Repeat fragment
     * @param n At least n times
     * @param m At most m times
     * @return Repeated fragment
     */
    public Fragment repeatNtoM(int n, int m) throws CloneNotSupportedException {
        if (n == m) {
            return repeatN(n);
        } else if (n == 0) {
            Fragment oldFragment = (Fragment) this.clone();
            Fragment fragment = repeatNoneOrOnce();
            for (int i = 1; i < m; i++)
                fragment = fragment.concatenate(((Fragment) oldFragment.clone()).repeatNoneOrOnce());
            return fragment;
        } else {
            Fragment fragment = this;
            Fragment oldFragment = (Fragment) fragment.clone();
            fragment = fragment.repeatN(n);
            for (int i = n; i < m; i++)
                fragment = fragment.concatenate(((Fragment) oldFragment.clone()).repeatNoneOrOnce());
            return fragment;
        }
    }

    @Override
    public String toString() {
        // Set for states that has been added in queue already
        Set<AbstractState> set = new HashSet<AbstractState>();
        // Queue for states that has not been printed
        Queue<AbstractState> queue = new LinkedBlockingQueue<AbstractState>();
        set.add(start);
        queue.add(start);
        // Print edges
        StringBuilder builder = new StringBuilder();
        while (!queue.isEmpty()) {
            AbstractState abstractState = queue.remove();
            if (abstractState.isMatched()) {
                // Do nothing
            } else if (abstractState.isAlter()) {
                SplitState splitState = (SplitState) abstractState;
                AbstractState out1 = splitState.getOut1();
                if (out1 != null) {
                    builder.append(String.format("%s -> %s\n", abstractState, out1));
                    if (!set.contains(out1)) {
                        set.add(out1);
                        queue.add(out1);
                    }
                }
                AbstractState out2 = splitState.getOut2();
                if (out2 != null) {
                    builder.append(String.format("%s -> %s\n", abstractState, out2));
                    if (!set.contains(out2)) {
                        set.add(out2);
                        queue.add(out2);
                    }
                }
            } else {
                State state = (State) abstractState;
                AbstractState out = state.getOut();
                if (out != null) {
                    builder.append(String.format("%s -> %s\n", abstractState, out));
                    if (!set.contains(out)) {
                        set.add(out);
                        queue.add(out);
                    }
                }
            }
        }
        return builder.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Map<AbstractState, AbstractState> map = new HashMap<AbstractState, AbstractState>();
        Queue<AbstractState> queue = new LinkedBlockingQueue<AbstractState>();
        // Clone start state
        AbstractState tempStart = (AbstractState) getStart().clone();
        map.put(getStart(), tempStart);
        queue.add(tempStart);
        // Clone states and redirect edges
        while (!queue.isEmpty()) {
            AbstractState abstractState = queue.remove();
            if (abstractState.isMatched()) {
                // Do nothing
            } else if (abstractState.isAlter()) {
                SplitState splitState = (SplitState) abstractState;
                AbstractState out1 = splitState.getOut1();
                if (out1 != null) {
                    if (map.get(out1) == null) {
                        AbstractState tempState = (AbstractState) out1.clone();
                        map.put(out1, tempState);
                        queue.add(tempState);
                    }
                    splitState.setOut1(map.get(out1));
                }
                AbstractState out2 = splitState.getOut2();
                if (out2 != null) {
                    if (map.get(out2) == null) {
                        AbstractState tempState = (AbstractState) out2.clone();
                        map.put(out2, tempState);
                        queue.add(tempState);
                    }
                    splitState.setOut2(map.get(out2));
                }
            } else {
                State state = (State) abstractState;
                AbstractState out = state.getOut();
                if (out != null) {
                    if (map.get(out) == null) {
                        AbstractState tempState = (AbstractState) out.clone();
                        map.put(out, tempState);
                        queue.add(tempState);
                    }
                    state.setOut(map.get(out));
                }
            }
        }
        // Redirect end states
        ArrayList<AbstractState> tempEnds = new ArrayList<AbstractState>();
        for (AbstractState state : getEnds())
            tempEnds.add(map.get(state));
        return new Fragment(tempStart, tempEnds);
    }
}