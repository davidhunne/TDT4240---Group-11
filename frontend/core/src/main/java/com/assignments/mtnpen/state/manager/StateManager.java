package com.assignments.mtnpen.state.manager;

import com.assignments.mtnpen.state.State;

public class StateManager {
    private State currentState;

    public State getCurrentState() {
        return currentState;
    }

    public void setState(State state) {
        if (currentState != null) {
            currentState.leave();
        }

        currentState = state;

        if (currentState != null) {
            currentState.create();
            currentState.enter();
        }
    }

    public void render(float delta) {
        if (currentState != null) {
            currentState.render(delta);
        }
    }

    public void resize(int width, int height) {
        if (currentState != null) {
            currentState.resize(width, height);
        }
    }

    public void pause() {
        if (currentState != null) {
            currentState.pause();
        }
    }

    public void resume() {
        if (currentState != null) {
            currentState.resume();
        }
    }
}
