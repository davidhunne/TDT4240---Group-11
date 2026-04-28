package com.assignments.mtnpen.model.game;
import com.assignments.mtnpen.model.parameters.GameParameters;

import com.badlogic.gdx.math.Vector2;

public class GameModel {
    private String gameId;
    private String playerId;
    private String playerName;
    private boolean isPaused = false;
    private float gameTime = 0;

    private float penguinPositionX = GameParameters.WORLD_WIDTH / 2; // Start in the middle of the world
    private float penguinPositionY = GameParameters.WORLD_HEIGHT / 2;


    private float launchVelocityX = 0;
    private float launchVelocityY = 0;



    public GameModel(String gameId, String playerId, String playerName) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerName = playerName;
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

    public void update(float delta) {
        if (!isPaused) {
            gameTime += delta;
        }
    }

    public float getPenguinPositionX() {
        return penguinPositionX;
    }
    public float getPenguinPositionY() {
        return penguinPositionY;
    }
    public Vector2 getPenguinPosition() {
        return new Vector2(penguinPositionX, penguinPositionY);
    }

    public void setPenguinPosition(float x, float y) {
        this.penguinPositionX = x;
        this.penguinPositionY = y;
    }

    public float getLaunchVelocityX() {
        return launchVelocityX;
    }
    public float getLaunchVelocityY() {
        return launchVelocityY;
    }
    public void setLaunchVelocity(float vx, float vy) {
        this.launchVelocityX = vx;
        this.launchVelocityY = vy;
    }



}
