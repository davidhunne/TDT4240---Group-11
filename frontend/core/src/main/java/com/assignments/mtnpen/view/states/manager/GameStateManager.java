package com.assignments.mtnpen.view.states.manager;

import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.State;
import java.util.Stack;

public class GameStateManager {
    private Stack<State> states;
    private NetworkManager networkManager;
    private String playerId;

    public GameStateManager(String playerId, String apiBaseUrl) {
        states = new Stack<>();
        this.playerId = sanitizePlayerId(playerId);
        networkManager = new NetworkManager(apiBaseUrl);
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public String getPlayerId() {
        return playerId;
    }

    private String sanitizePlayerId(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            return "unknown-device";
        }
        return playerId.trim();
    }

    public void push(State state) {
        states.push(state);
        state.create();
        state.enter();
    }

    public void pop() {
        State state = states.pop();
        state.leave();
    }

    public void set(State state) {
        if (!states.isEmpty()) {
            pop();
        }
        push(state);
    }

    public void render(float delta) {
        if (!states.isEmpty()) {
            states.peek().render(delta);
        }
    }

    public void resize(int width, int height) {
        if (!states.isEmpty()) {
            states.peek().resize(width, height);
        }
    }

    public void pause() {
        if (!states.isEmpty()) {
            states.peek().pause();
        }
    }

    public void resume() {
        if (!states.isEmpty()) {
            states.peek().resume();
        }
    }
}
