package com.assignments.mtnpen.view.states.game;

import com.assignments.mtnpen.controller.game.GameController;
import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GameState extends BaseState {
    private final GameModel model;
    private final GameController controller;

    public GameState(GameStateManager gsm) {
        super(gsm);
        this.model = new GameModel();
        this.controller = new GameController(model, gsm);
    }

    @Override
    protected void update(float delta) {
        model.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.pauseGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            controller.onGameFinished("Player 1");
        }
    }

    @Override
    public void enter() {
        // TODO: Initialize ECS, Load map, etc.
    }

    @Override
    public void leave() {
        // TODO: Dispose assets
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // TODO: Render ECS entities
    }
}
