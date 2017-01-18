package com.sine_x.regexp.state;

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
        if (out != null)
            logger.info(String.format("Add edge: %s -> %s", this, out));
    }
}
