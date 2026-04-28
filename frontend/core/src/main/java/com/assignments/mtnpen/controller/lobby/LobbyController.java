package com.assignments.mtnpen.controller.lobby;

import com.assignments.mtnpen.model.lobby.LobbyModel;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.lobby.LobbyState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.assignments.mtnpen.view.states.game.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class LobbyController {
    private final LobbyModel model;
    private final GameStateManager gsm;

    public LobbyController(LobbyModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void onReadyClicked() {
        boolean newReadyState = !model.isReady();
        model.setReady(newReadyState);
    }

    public void onStartGameClicked() {
        if (model.isHost() && model.hasEnoughPlayersToStart()) {
            gsm.getNetworkManager().startGame(model.getLobbyCode(), model.getPlayerId(), new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    Gdx.app.postRunnable(() -> gsm.set(new GameState(gsm, model.getLobbyCode(), model.getPlayerId(), model.getPlayerName())));
                }

                @Override
                public void onError(Throwable t) {
                    // TODO: Handle error
                }
            });
        }
    }

    public void onLeaveLobbyClicked() {
        gsm.getNetworkManager().leaveGame(model.getLobbyCode(), model.getPlayerId(), new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> gsm.set(new MenuState(gsm)));
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> gsm.set(new MenuState(gsm)));
            }
        });
    }

    public void pollGame(final Runnable onUpdated) {
        gsm.getNetworkManager().getGame(model.getLobbyCode(), new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JsonValue game = gsm.getNetworkManager().parse(response);
                    applyGame(game);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            onUpdated.run();
                            if ("in_progress".equals(model.getStatus())) {
                                gsm.set(new GameState(gsm, model.getLobbyCode(), model.getPlayerId(), model.getPlayerName()));
                            }
                        }
                    });
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    private void applyGame(JsonValue game) {
        String hostId = game.getString("hostId", "");
        model.setStatus(game.getString("status", "lobby"));

        List<String> players = new ArrayList<>();
        JsonValue playerArray = game.get("players");
        if (playerArray != null) {
            for (JsonValue player : playerArray) {
                String name = player.getString("displayName", "Player");
                String id = player.getString("playerId", "");
                if (id.equals(hostId)) {
                    model.setHostName(name);
                    name += " (Host)";
                }
                if (!player.getBoolean("connected", true)) {
                    name += " (Disconnected)";
                }
                players.add(name);
            }
        }
        model.setPlayers(players);
    }

    public LobbyModel getModel() {
        return model;
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
