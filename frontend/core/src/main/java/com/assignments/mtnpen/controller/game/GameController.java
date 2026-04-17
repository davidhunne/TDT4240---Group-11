package com.assignments.mtnpen.controller.game;

import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.results.ResultsState;

public class GameController {
    private final GameModel model;
    private final GameStateManager gsm;

    public GameController(GameModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void pauseGame() {
        model.setPaused(true);
    }

    public void resumeGame() {
        model.setPaused(false);
    }

    public void onGameFinished(String playerName) {
        gsm.set(new ResultsState(gsm, playerName, 1, model.getGameTime()));
    }
}
