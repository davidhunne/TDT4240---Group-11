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

    public GameState(GameStateManager gsm, String gameId, String playerId, String playerName) {
        super(gsm);
        this.model = new GameModel(gameId, playerId, playerName);
        this.controller = new GameController(model, gsm);
    }

    @Override
    protected void update(float delta) {
        model.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.pauseGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            controller.onGameFinished();
        }
    }

    @Override
    public void enter() {
        controller.onGameEntered();
    }

    @Override
    public void leave() {
        controller.onGameLeft();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // TODO: Render ECS entities
    }
}
