package com.assignments.mtnpen.controller.menu;

import com.assignments.mtnpen.model.menu.MenuModel;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.lobby.LobbyState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.settings.SettingsState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

public class MenuController {
    private final MenuModel model;
    private final GameStateManager gsm;

    public MenuController(MenuModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void onCreateLobbyClicked(String playerName) {
        if (playerName.isEmpty()) {
            model.setStatusMessage("Please enter a player name.");
            return;
        }

        model.setStatusMessage("Creating a lobby...");
        registerPlayer(playerName, new Runnable() {
            @Override
            public void run() {
                gsm.getNetworkManager().createGame(gsm.getPlayerId(), playerName, new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JsonValue game = gsm.getNetworkManager().parse(response);
                            String gameId = game.getString("id");
                            Gdx.app.postRunnable(() -> gsm.set(new LobbyState(gsm, playerName, gameId, true)));
                        } catch (Exception e) {
                            Gdx.app.postRunnable(() -> model.setStatusMessage("Created lobby, but could not read response."));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.postRunnable(() -> model.setStatusMessage("Failed to create lobby: " + t.getMessage()));
                    }
                });
            }
        });
    }

    public void onJoinLobbyClicked(String playerName, String lobbyCode) {
        if (playerName.isEmpty()) {
            model.setStatusMessage("Please enter a player name.");
            return;
        }

        if (lobbyCode.isEmpty()) {
            model.setStatusMessage("Please enter a lobby code.");
            return;
        }

        model.setStatusMessage("Joining lobby " + lobbyCode + "...");
        registerPlayer(playerName, new Runnable() {
            @Override
            public void run() {
                gsm.getNetworkManager().joinGame(lobbyCode, gsm.getPlayerId(), playerName, new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.postRunnable(() -> gsm.set(new LobbyState(gsm, playerName, lobbyCode, false)));
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.postRunnable(() -> model.setStatusMessage("Failed to join lobby: " + t.getMessage()));
                    }
                });
            }
        });
    }

    private void registerPlayer(String playerName, final Runnable onRegistered) {
        gsm.getNetworkManager().upsertPlayer(gsm.getPlayerId(), playerName, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                gsm.getNetworkManager().updateDisplayName(gsm.getPlayerId(), playerName, new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.postRunnable(onRegistered);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.postRunnable(onRegistered);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> model.setStatusMessage("Failed to register player: " + t.getMessage()));
            }
        });
    }

    public void onSettingsClicked() {
        gsm.set(new SettingsState(gsm));
    }

    public void onExitClicked() {
        Gdx.app.exit();
    }
}
