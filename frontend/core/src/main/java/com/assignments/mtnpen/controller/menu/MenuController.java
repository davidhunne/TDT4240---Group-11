package com.assignments.mtnpen.controller.menu;

import com.assignments.mtnpen.model.menu.MenuModel;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.lobby.LobbyState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.settings.SettingsState;
import com.badlogic.gdx.Gdx;

public class MenuController {
    private final MenuModel model;
    private final GameStateManager gsm;

    public MenuController(MenuModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void onCreateLobbyClicked(String playerName) {
        //TODO: Align this with the backend setup
        if (playerName.isEmpty()) {
            model.setStatusMessage("Please enter a player name.");
            return;
        }

        model.setStatusMessage("Creating a lobby...");
        gsm.getNetworkManager().createGame("temp-id-" + playerName, playerName, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> gsm.set(new LobbyState(gsm, playerName, "HOST12", true)));
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> model.setStatusMessage("Failed to create lobby: " + t.getMessage()));
            }
        });
    }

    public void onJoinLobbyClicked(String playerName, String lobbyCode) {
        //TODO: Align this with the backend setup
        if (playerName.isEmpty()) {
            model.setStatusMessage("Please enter a player name.");
            return;
        }

        if (lobbyCode.isEmpty()) {
            model.setStatusMessage("Please enter a lobby code.");
            return;
        }

        model.setStatusMessage("Joining lobby " + lobbyCode + "...");
        gsm.getNetworkManager().joinGame(lobbyCode, "temp-id-" + playerName, playerName, new NetworkManager.NetworkCallback() {
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

    public void onSettingsClicked() {
        gsm.set(new SettingsState(gsm));
    }

    public void onExitClicked() {
        Gdx.app.exit();
    }
}
