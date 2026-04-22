package com.assignments.mtnpen.model.lobby;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    private String playerName;
    private String lobbyCode;
    private boolean isHost;
    private List<String> players;
    private boolean isReady;

    public LobbyModel(String playerName, String lobbyCode, boolean isHost) {
        this.playerName = playerName;
        this.lobbyCode = lobbyCode;
        this.isHost = isHost;
        this.players = new ArrayList<>();
        this.isReady = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public boolean isHost() {
        return isHost;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
