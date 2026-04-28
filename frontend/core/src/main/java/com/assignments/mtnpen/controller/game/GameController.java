package com.assignments.mtnpen.controller.game;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.results.ResultsState;
import com.badlogic.gdx.Gdx;
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

    public void onGameEntered() {
        gsm.getNetworkManager().updateConnectionState(model.getGameId(), model.getPlayerId(), true, new NoopCallback());
    }

    public void onGameLeft() {
        gsm.getNetworkManager().updateConnectionState(model.getGameId(), model.getPlayerId(), false, new NoopCallback());
    }

    public void submitMove(int x, int y) {
        gsm.getNetworkManager().submitMovePosition(model.getGameId(), model.getPlayerId(), x, y, new NoopCallback());
    }

    public void onGameFinished() {
        gsm.getNetworkManager().endGame(model.getGameId(), model.getPlayerId(), new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> gsm.set(new ResultsState(gsm, model.getPlayerName(), 1, model.getGameTime())));
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> gsm.set(new ResultsState(gsm, model.getPlayerName(), 1, model.getGameTime())));
            }
        });
    }

    private static class NoopCallback implements NetworkManager.NetworkCallback {
        @Override
        public void onSuccess(String response) {
        }

        @Override
        public void onError(Throwable t) {
        }
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
