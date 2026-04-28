package com.assignments.mtnpen.controller.input;

<<<<<<< HEAD
import com.assignments.mtnpen.model.game.GameModel;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputController extends InputAdapter {

  public enum InputState {
    IDLE, TOUCHING, DRAGGING, LOCKED
  }

  private InputState state = InputState.IDLE;
  private GameModel model;


  private Vector2 touchPosition = new Vector2();
  private Vector2 relativeTouchPos = new Vector2();
  
  private long touchStartMillis;
  private final AimListener aimListener;

  private final OrthographicCamera camera;


  public InputController(GameModel model, AimListener aimListener, OrthographicCamera camera) {
    this.aimListener = aimListener;
    this.camera = camera;
    this.model = model;
  }



  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (pointer!=0 || state != InputState.IDLE) return false;

    touchPosition.set(screenX, screenY);

    float distance = model.getPenguinPosition().dst(screenX, screenY);
    if (distance > GameParameters.MAX_LAUNCH_RADIUS) return false; // Only start touch if close enough to

    state = InputState.TOUCHING;
    touchStartMillis = TimeUtils.millis();

    relativeTouchPos.set(touchPosition).sub(model.getPenguinPosition()).limit(GameParameters.MAX_LAUNCH_RADIUS);

    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (pointer!=0 || state == InputState.IDLE) return false;
  
    if (state == InputState.TOUCHING) {
      float timeSinceTouchStart = (TimeUtils.millis() - touchStartMillis);
      if (timeSinceTouchStart > GameParameters.TAP_OR_DRAG_THRESHOLD) {
        state = InputState.DRAGGING;
      }
    }
    if (state == InputState.DRAGGING) {
      touchPosition.set(screenX, screenY);
      relativeTouchPos.set(touchPosition).sub(model.getPenguinPosition()).limit(GameParameters.MAX_LAUNCH_RADIUS);

    }


    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (pointer!=0 || state == InputState.LOCKED) return false;

    state = InputState.IDLE;

    return true;
  }

  public void lockAim() {
    if (state == InputState.LOCKED) return;

    state = InputState.LOCKED;
    aimListener.onAimLock(relativeTouchPos.x, relativeTouchPos.y);
    return;
  }

  public void cancelAim() {
    if (state != InputState.LOCKED) return;

    state = InputState.IDLE;
    relativeTouchPos.setZero();
    aimListener.onAimCancel();
  }

  public InputState getState() {
    return state;
  }

  public Vector2 getRelativeTouchPos() {
    return relativeTouchPos;
  }





  
=======
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class InputController implements InputProcessor {
    private final InputCallback callback;
    
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();
    
    private static final float MAX_DRAG_DISTANCE = 200f;
    private static final float VELOCITY_SCALE = 50f;
    
    public interface InputCallback {
        void onDragStart(float screenX, float screenY);
        void onDragUpdate(float screenX, float screenY, float angle, float velocity);
        void onDragEnd(float screenX, float screenY, float angle, float velocity);
        void onMenuButtonPressed();
    }
    
    public InputController(InputCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        
        isDragging = true;
        dragStart.set(screenX, screenY);
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
        
        float angle = (float) Math.atan2(-deltaY, deltaX);
        float velocity = Math.min(distance / MAX_DRAG_DISTANCE, 1f) * VELOCITY_SCALE;
        
        callback.onDragUpdate(screenX, screenY, angle, velocity);
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0 || !isDragging) return false;
        
        isDragging = false;
        
        float deltaX = dragCurrent.x - dragStart.x;
        float deltaY = dragCurrent.y - dragStart.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        float angle = (float) Math.atan2(-deltaY, deltaX);
        float velocity = Math.min(distance / MAX_DRAG_DISTANCE, 1f) * VELOCITY_SCALE;
        
        if (distance > 10) {
            callback.onDragEnd(screenX, screenY, angle, velocity);
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
>>>>>>> origin/feat-implement-input-rendering
}
