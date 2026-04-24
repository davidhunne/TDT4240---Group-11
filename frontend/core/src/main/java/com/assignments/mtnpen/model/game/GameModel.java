package com.assignments.mtnpen.model.game;

public class GameModel {
    private boolean isPaused = false;
    private float gameTime = 0;

    private float launchVelocityX = 0;
    private float launchVelocityY = 0;

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
