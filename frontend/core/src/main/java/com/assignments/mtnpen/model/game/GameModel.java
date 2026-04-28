package com.assignments.mtnpen.model.game;

import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModel {
    private static final int BOARD_LIMIT = 99;
    private static final float MAX_INPUT_VELOCITY = 50f;
    private static final float MAX_MOVE_DISTANCE_CELLS = 20f;

    private String gameId;
    private String playerId;
    private String playerName;
    private boolean isPaused = false;
    private float gameTime = 0;
    
    private GamePhase currentPhase = GamePhase.INPUT;
    private String gameStatus = "lobby";
    private int currentTurnIndex = 0;
    private List<String> turnOrder = new ArrayList<>();
    
    private List<Map<String, Integer>> obstacles = new ArrayList<>();
    private List<Map<String, ?>> boosts = new ArrayList<>();
    
    private List<PlayerData> players = new ArrayList<>();
    private Map<String, PlayerData> playersByIdCache = new HashMap<>();
    
    private float currentMoveAngle = 0f;
    private float currentMoveVelocity = 0f;
    private boolean hasPendingMove = false;
    
    private float phaseTimer = 0f;
    private static final float INPUT_PHASE_TIMEOUT = 30f;
    private static final float LAUNCH_PHASE_DURATION = 1f;
    private static final float COUNTDOWN_DURATION = 3f;
    
    public GameModel(String gameId, String playerId, String playerName) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerName = playerName;
    }
    
    public void updateFromServerState(JsonValue gameJson) {
        if (gameJson == null) return;
        
        gameStatus = gameJson.getString("status", "in_progress");
        currentTurnIndex = gameJson.getInt("currentTurnIndex", 0);
        
        players.clear();
        playersByIdCache.clear();
        JsonValue playersArray = gameJson.get("players");
        if (playersArray != null) {
            for (JsonValue player : playersArray) {
                PlayerData pd = new PlayerData();
                pd.playerId = player.getString("playerId", "");
                pd.displayName = player.getString("displayName", "Player");
                pd.score = player.getInt("score", 0);
                pd.connected = player.getBoolean("connected", true);
                
                JsonValue posObj = player.get("position");
                if (posObj != null) {
                    pd.positionX = posObj.getInt("x", 0);
                    pd.positionY = posObj.getInt("y", 0);
                }
                
                players.add(pd);
                playersByIdCache.put(pd.playerId, pd);
            }
        }
        
        turnOrder.clear();
        JsonValue turnArray = gameJson.get("turnOrder");
        if (turnArray != null) {
            for (JsonValue turn : turnArray) {
                turnOrder.add(turn.asString());
            }
        }
        
        parseBoard(gameJson.get("boardState"));
        updatePhaseFromStatus();
    }
    
    private void parseBoard(JsonValue boardJson) {
        if (boardJson == null) return;
        
        obstacles.clear();
        boosts.clear();
        
        JsonValue obstaclesArray = boardJson.get("obstacles");
        if (obstaclesArray != null) {
            for (JsonValue obs : obstaclesArray) {
                Map<String, Integer> obstacle = new HashMap<>();
                obstacle.put("x", obs.getInt("x", 0));
                obstacle.put("y", obs.getInt("y", 0));
                obstacles.add(obstacle);
            }
        }
        
        JsonValue boostsArray = boardJson.get("boosts");
        if (boostsArray != null) {
            for (JsonValue boost : boostsArray) {
                Map<String, Object> boostMap = new HashMap<>();
                boostMap.put("x", boost.getInt("x", 0));
                boostMap.put("y", boost.getInt("y", 0));
                boostMap.put("amount", boost.getInt("amount", 10));
                boosts.add(boostMap);
            }
        }
    }
    
    private void updatePhaseFromStatus() {
        if ("finished".equals(gameStatus)) {
            currentPhase = GamePhase.FINISHED;
        } else if ("in_progress".equals(gameStatus)) {
            if (hasPendingMove) {
                currentPhase = GamePhase.RESOLVING;
            } else if (isCurrentPlayerTurn()) {
                if (currentPhase != GamePhase.INPUT) {
                    phaseTimer = 0f;
                }
                currentPhase = GamePhase.INPUT;
            } else {
                currentPhase = GamePhase.OPPONENT_INPUT;
            }
        } else {
            // Lobby / waiting state
            currentPhase = GamePhase.COUNTDOWN;
        }
    }
    
    private boolean isCurrentPlayerTurn() {
        if (turnOrder.isEmpty() || currentTurnIndex >= turnOrder.size()) {
            return false;
        }
        return playerId.equals(turnOrder.get(currentTurnIndex));
    }
    
    public void submitMove(float angle, float velocity) {
        currentMoveAngle = angle;
        currentMoveVelocity = velocity;
        hasPendingMove = true;
        phaseTimer = 0f;
        updatePhaseFromStatus();
    }
    
    public boolean hasMove() {
        return hasPendingMove;
    }
    
    public void clearMove() {
        hasPendingMove = false;
        currentMoveAngle = 0f;
        currentMoveVelocity = 0f;
        phaseTimer = 0f;
        updatePhaseFromStatus();
    }
    
    public int[] getTargetPosition(float angle, float velocity) {
        PlayerData current = getCurrentPlayer();
        int startX = current != null ? current.positionX : 0;
        int startY = current != null ? current.positionY : 0;

        float power = Math.max(0f, Math.min(velocity / MAX_INPUT_VELOCITY, 1f));
        int distance = Math.round(power * MAX_MOVE_DISTANCE_CELLS);
        int x = startX + Math.round(distance * (float) Math.cos(angle));
        int y = startY + Math.round(distance * (float) Math.sin(angle));

        x = Math.max(0, Math.min(BOARD_LIMIT, x));
        y = Math.max(0, Math.min(BOARD_LIMIT, y));

        return new int[]{x, y};
    }

    public void update(float delta) {
        if (!isPaused) {
            gameTime += delta;
            phaseTimer += delta;
        }
    }
    
    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }


    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public float getGameTime() {
        return gameTime;
    }

    public void setGameTime(float gameTime) {
        this.gameTime = gameTime;
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    public String getGameStatus() {
        return gameStatus;
    }
    
    public List<PlayerData> getPlayers() {
        return players;
    }
    
    public PlayerData getPlayer(String playerId) {
        return playersByIdCache.get(playerId);
    }
    
    public PlayerData getCurrentPlayer() {
        return getPlayer(playerId);
    }
    
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public String getCurrentTurnPlayerId() {
        if (turnOrder.isEmpty() || currentTurnIndex >= turnOrder.size()) {
            return null;
        }
        return turnOrder.get(currentTurnIndex);
    }
    
    public List<Map<String, Integer>> getObstacles() {
        return obstacles;
    }
    
    public List<Map<String, ?>> getBoosts() {
        return boosts;
    }
    
    public float getCurrentMoveAngle() {
        return currentMoveAngle;
    }
    
    public float getCurrentMoveVelocity() {
        return currentMoveVelocity;
    }
    
    public float getPhaseTimer() {
        return phaseTimer;
    }
    
    public float getInputPhaseTimeout() {
        return INPUT_PHASE_TIMEOUT;
    }
    
    public float getCountdownDuration() {
        return COUNTDOWN_DURATION;
    }
    
    public boolean isCurrentPlayerTurnForUI() {
        return isCurrentPlayerTurn();
    }
    
    public static class PlayerData {
        public String playerId;
        public String displayName;
        public int positionX;
        public int positionY;
        public int score;
        public boolean connected;
    }
}
