package com.assignments.mtnpen.controller.game;

import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.model.game.GamePhase;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.results.ResultsState;
import com.badlogic.gdx.Gdx;

public class GameController {
    private final GameModel model;
    private final GameStateManager gsm;
    private boolean moveSubmitted = false;
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
        gsm.getNetworkManager().updateConnectionState(model.getGameId(), model.getPlayerId(), false, new NoopCallback());
    }

    public void submitMove(float angle, float velocity) {
        if (moveSubmitted) return;
        
        model.submitMove(angle, velocity);
        int[] targetPos = model.getTargetPosition(angle, velocity);
        
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
                }
            }
        );
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
                        model.updateFromServerState(gameJson);
                        
                        // Reset move submission flag on new turn
                        if (model.isCurrentPlayerTurnForUI() && !moveSubmitted) {
                            moveSubmitted = false;
                        }
                        
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
    
    private void handleGameFinished() {
        gsm.getNetworkManager().endGame(model.getGameId(), model.getPlayerId(), new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> {
                    GameModel.PlayerData current = model.getCurrentPlayer();
                    int finalScore = current != null ? current.score : 0;
                    gsm.set(new ResultsState(gsm, model.getPlayerName(), finalScore, model.getGameTime()));
                });
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    GameModel.PlayerData current = model.getCurrentPlayer();
                    int finalScore = current != null ? current.score : 0;
                    gsm.set(new ResultsState(gsm, model.getPlayerName(), finalScore, model.getGameTime()));
                });
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
}
