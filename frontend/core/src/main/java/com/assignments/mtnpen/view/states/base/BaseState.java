package com.assignments.mtnpen.view.states.base;

import com.assignments.mtnpen.view.states.State;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseState implements State {

    protected final GameStateManager gsm;
    protected Stage stage;

    public BaseState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    protected abstract void update(float delta);

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void enter() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        //TODO: Pause state
    }

    @Override
    public void resume() {
        //TODO: Resume state
    }

    @Override
    public void leave() {
        if(Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
        stage.dispose();
    }
}
