package com.assignments.mtnpen.controller.game;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.results.ResultsState;
import com.assignments.mtnpen.controller.input.InputController;

import com.assignments.mtnpen.controller.input.AimListener;


public class GameController implements AimListener {
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

    @Override
    public void onAimLock(float deltaX, float deltaY) {
        float launchVx = - deltaX * GameParameters.LAUNCH_VELOCITY_SCALE; // Scale down for game world
        float launchVy = - deltaY * GameParameters.LAUNCH_VELOCITY_SCALE;
        model.setLaunchVelocity(launchVx, launchVy);

    }

    @Override
    public void onAimCancel() {
        model.setLaunchVelocity(0, 0);
    }

}
