package com.assignments.mtnpen.state.base;

import com.assignments.mtnpen.state.State;

public abstract class BaseState implements State {
    @Override
    public void create() {
        // Create state
    }

    @Override
    public void enter() {
        // Enter state
    }

    @Override
    public void render(float delta) {
        // Render state
    }

    @Override
    public void resize(int width, int height) {
        // Resize state
    }

    @Override
    public void pause() {
        // Pause state
    }

    @Override
    public void resume() {
        // Resume state
    }

    @Override
    public void leave() {
        // Leave state
    }
}
