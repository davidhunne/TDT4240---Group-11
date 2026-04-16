package com.assignments.mtnpen;

import com.assignments.mtnpen.state.manager.GameStateManager;
import com.assignments.mtnpen.state.menu.MenuState;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MountainPenguins extends ApplicationAdapter {
    private GameStateManager gsm;

    @Override
    public void create() {
        gsm = new GameStateManager();
        gsm.set(new MenuState(gsm));
    }

    @Override
    public void render() {
        gsm.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width, height);
    }

    @Override
    public void pause() {
        gsm.pause();
    }

    @Override
    public void resume() {
        gsm.resume();
    }

    @Override
    public void dispose() {
        while (true) {
            try {
                gsm.pop();
            } catch (Exception e) {
                break;
            }
        }
    }
}
