package com.sine_x.regex.state;

import com.sine_x.regex.BuildConfig;

/**
 * Split state state
 */
public class SplitState extends AbstractState {

    private AbstractState out1, out2;

    public SplitState(AbstractState out1) {
        this(out1, null);
    }

    public SplitState(AbstractState out1, AbstractState out2) {
        this.out1 = out1;
        this.out2 = out2;
        if (BuildConfig.LOG && out1 != null)
            logger.info(String.format("Add edge %s -> %s", this, out1));
        if (BuildConfig.LOG && out2 != null)
            logger.info(String.format("Add edge %s -> %s", this, out2));
    }

    @Override
    public boolean isAlter() {
        return true;
    }

    @Override
    public void setOut(AbstractState out) {
        this.out2 = out;
        if (BuildConfig.LOG && out != null)
            logger.info(String.format("Add edge %s -> %s", this, out));
    }

    public AbstractState getOut1() {
        return out1;
    }

    public void setOut1(AbstractState out1) {
        this.out1 = out1;
    }

    public AbstractState getOut2() {
        return out2;
    }

    public void setOut2(AbstractState out2) {
        this.out2 = out2;
    }

    @Override
    public String toString() {
        return String.format("SplitState@%x", hashCode());
    }
}