package com.assignments.mtnpen.view.states;

public interface State {
    void create();

    void enter();

    void render(float delta);

    void resize(int width, int height);

    void pause();

    void resume();

    void leave();
}
