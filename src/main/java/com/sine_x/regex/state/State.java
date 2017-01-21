package com.sine_x.regex.state;

import com.sine_x.regex.BuildConfig;

/**
 * Normal state state
 */
public abstract class State extends AbstractState {

    private AbstractState out;

    public AbstractState getOut() {
        return out;
    }

    @Override
    public void setOut(AbstractState out) {
        this.out = out;
        if (BuildConfig.LOG && out != null)
            logger.info(String.format("Add edge: %s -> %s", this, out));
    }
}
