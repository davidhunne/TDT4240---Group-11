package com.assignments.mtnpen.controller.game;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.model.game.GamePhase;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.results.ResultsState;
import com.badlogic.gdx.Gdx;
import com.assignments.mtnpen.controller.input.InputController;

import com.assignments.mtnpen.controller.input.AimListener;


public class GameController{
    private final GameModel model;
    private final GameStateManager gsm;
    private boolean moveSubmitted = false;
    private int lastTurnIndex = -1;
    private float pollTimer = 0f;
    private static final float POLL_INTERVAL = 0.5f;

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
        gsm.getNetworkManager().updateConnectionState(model.getGameId(), model.getPlayerId(), false,
                new NoopCallback());
    }

    public void submitMove(float angle, float velocity) {
        if (moveSubmitted)
            return;

        int[] targetPos = model.getTargetPosition(angle, velocity);
        model.submitMove(angle, velocity);
        moveSubmitted = true;

        gsm.getNetworkManager().submitMovePosition(
                model.getGameId(),
                model.getPlayerId(),
                targetPos[0],
                targetPos[1],
                new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.log("GameController", "Move submitted successfully");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.log("GameController", "Move submission failed: " + t.getMessage());
                        Gdx.app.postRunnable(() -> resetSubmittedMove());
                    }
                });
    }

    public void update(float delta) {
        pollTimer += delta;

        if (pollTimer >= POLL_INTERVAL) {
            pollTimer = 0f;
            pollGameState();
        }
    }

    private void pollGameState() {
        gsm.getNetworkManager().getGame(model.getGameId(), new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    com.badlogic.gdx.utils.JsonValue gameJson = gsm.getNetworkManager().parse(response);
                    Gdx.app.postRunnable(() -> {
                        String previousStatus = model.getGameStatus();
                        int previousTurnIndex = lastTurnIndex;
                        model.updateFromServerState(gameJson);

                        int currentTurnIndex = model.getCurrentTurnIndex();
                        if (previousTurnIndex >= 0 && currentTurnIndex != previousTurnIndex) {
                            resetSubmittedMove();
                        } else if (previousTurnIndex < 0) {
                            lastTurnIndex = currentTurnIndex;
                        }

                        lastTurnIndex = currentTurnIndex;

                        // Transition to results if game finished
                        if ("finished".equals(model.getGameStatus()) && !previousStatus.equals("finished")) {
                            handleGameFinished();
                        }
                    });
                } catch (Exception e) {
                    Gdx.app.log("GameController", "Failed to parse game state: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.log("GameController", "Poll failed: " + t.getMessage());
            }
        });
    }

    public void onGameFinished() {
        handleGameFinished();
    }

    public void onLocalPlayerReachedFlag() {
        handleGameFinishedWith(model.getPlayerId());
    }

    private void resetSubmittedMove() {
        moveSubmitted = false;
        model.clearMove();
    }

    private void handleGameFinished() {
        handleGameFinishedWith(null);
    }

    private boolean finishHandled = false;

    private void handleGameFinishedWith(String winnerId) {
        if (finishHandled) return;
        finishHandled = true;
        gsm.getNetworkManager().endGame(model.getGameId(), winnerId, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> goToResults(winnerId));
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> goToResults(winnerId));
            }
        });
    }

    private void goToResults(String winnerId) {
        java.util.List<ResultsState.PlayerResult> results = new java.util.ArrayList<>();
        java.util.List<GameModel.PlayerData> players = model.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            GameModel.PlayerData pd = players.get(i);
            boolean won = winnerId != null && winnerId.equals(pd.playerId);
            results.add(new ResultsState.PlayerResult(
                    pd.playerId, pd.displayName, pd.score, i, won));
        }
        gsm.set(new ResultsState(
                gsm,
                model.getPlayerId(),
                model.getPlayerName(),
                results,
                model.getGameTime()));
    }

    private static class NoopCallback implements NetworkManager.NetworkCallback {
        @Override
        public void onSuccess(String response) {
        }

        @Override
        public void onError(Throwable t) {
        }
    }


}
