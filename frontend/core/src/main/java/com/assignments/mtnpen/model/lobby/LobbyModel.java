package com.assignments.mtnpen.model.lobby;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    public static final int MIN_PLAYERS_TO_START = 2;

    private String playerName;
    private String playerId;
    private String lobbyCode;
    private String hostName;
    private boolean isHost;
    private List<String> players;
    private boolean isReady;
    private String status = "lobby";

    public LobbyModel(String playerName, String playerId, String lobbyCode, boolean isHost) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.lobbyCode = lobbyCode;
        this.hostName = isHost ? playerName : "";
        this.isHost = isHost;
        this.players = new ArrayList<>();
        this.isReady = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasEnoughPlayersToStart() {
        return getPlayerCount() >= MIN_PLAYERS_TO_START;
    }

    public int getPlayersNeededToStart() {
        return Math.max(0, MIN_PLAYERS_TO_START - getPlayerCount());
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
