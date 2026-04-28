package com.assignments.mtnpen.controller.input;


import com.assignments.mtnpen.model.game.GameModel;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.badlogic.gdx.InputAdapter;



import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.graphics.OrthographicCamera;


  

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class InputController implements InputProcessor {
    private final InputCallback callback;
    
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();
    private Vector2 penguinPosition = new Vector2();
    private GameModel model;
    
    private static final float MAX_DRAG_DISTANCE = 200f;
    private static final float VELOCITY_SCALE = 50f;
    
    public interface InputCallback {
        void onDragStart(float screenX, float screenY);
        void onDragUpdate(float screenX, float screenY, float angle, float velocity);
        void onDragEnd(float screenX, float screenY, float angle, float velocity);
        void onMenuButtonPressed();
    }
    
    public InputController(GameModel model,InputCallback callback) {
        this.callback = callback;
        this.model = model;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        
        isDragging = true;
        dragStart.set(model.getCurrentPlayer().positionX, model.getCurrentPlayer().positionY);
        dragCurrent.set(screenX, screenY);
        callback.onDragStart(screenX, screenY);
        return true;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer != 0 || !isDragging) return false;

        dragCurrent.set(screenX, screenY);

        float deltaX = dragCurrent.x - dragStart.x;
        float deltaY = dragCurrent.y - dragStart.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        float launchAngle = (float) Math.atan2(deltaY, -deltaX);
        float velocity = Math.min(distance / MAX_DRAG_DISTANCE, 1f) * VELOCITY_SCALE;

        callback.onDragUpdate(screenX, screenY, launchAngle, velocity);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0 || !isDragging) return false;

        isDragging = false;

        float deltaX = dragCurrent.x - dragStart.x;
        float deltaY = dragCurrent.y - dragStart.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        float launchAngle = (float) Math.atan2(deltaY, -deltaX);
        float velocity = Math.min(distance / MAX_DRAG_DISTANCE, 1f) * VELOCITY_SCALE;

        if (distance > 10) {
            callback.onDragEnd(screenX, screenY, launchAngle, velocity);
        }

        return true;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            callback.onMenuButtonPressed();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0 && isDragging) {
            isDragging = false;
        }
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    public boolean isDragging() {
        return isDragging;
    }
    
    public Vector2 getDragStartScreen() {
        return dragStart.cpy();
    }
    
    public Vector2 getDragCurrentScreen() {
        return dragCurrent.cpy();
    }
    
    public float getDragDistance() {
        return dragStart.dst(dragCurrent);
    }

}
