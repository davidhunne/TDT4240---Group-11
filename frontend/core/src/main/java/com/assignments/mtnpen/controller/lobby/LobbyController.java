package com.assignments.mtnpen.controller.lobby;

import com.assignments.mtnpen.model.lobby.LobbyModel;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.lobby.LobbyState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.assignments.mtnpen.view.states.game.GameState;
import com.badlogic.gdx.Gdx;

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
        // TODO: Notify backend here
    }

    public void onStartGameClicked() {
        if (model.isHost()) {
            gsm.getNetworkManager().startGame(model.getLobbyCode(), "temp-id-" + model.getPlayerName(), new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    Gdx.app.postRunnable(() -> gsm.set(new GameState(gsm)));
                }

                @Override
                public void onError(Throwable t) {
                    // TODO: Handle error
                }
            });
        }
    }

    public void onLeaveLobbyClicked() {
        gsm.set(new MenuState(gsm));
    }

    public LobbyModel getModel() {
        return model;
    }
}
