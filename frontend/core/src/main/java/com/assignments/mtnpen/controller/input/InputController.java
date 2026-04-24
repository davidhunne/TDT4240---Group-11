package com.assignments.mtnpen.controller.input;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.utils.TimeUtils;

public class InputController extends InputAdapter {

  public enum InputState {
    IDLE, TOUCHING, DRAGGING, LOCKED
  }

  private InputState state = InputState.IDLE;


  private final Vector2 penguinPosition = new Vector2();
  private Vector2 touchPosition = new Vector2();
  private Vector2 relativeTouchPos = new Vector2();
  
  private long touchStartMillis;
  private final AimListener aimListener;


  public InputController(AimListener aimListener) {
    this.aimListener = aimListener;
  }

  public void setPenguinPosition(float x, float y) {
    penguinPosition.set(x, y);
  }


  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (pointer!=0 || state != InputState.IDLE) return false;

    touchPosition.set(screenX, screenY);

    float distance = penguinPosition.dst(screenX, screenY);
    if (distance > GameParameters.MAX_LAUNCH_RADIUS) return false; // Only start touch if close enough to

    state = InputState.TOUCHING;
    touchStartMillis = TimeUtils.millis();

    relativeTouchPos.set(touchPosition).sub(penguinPosition).limit(GameParameters.MAX_LAUNCH_RADIUS);

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
      relativeTouchPos.set(touchPosition).sub(penguinPosition).limit(GameParameters.MAX_LAUNCH_RADIUS);

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
    aimListener.onAimCancel();
  }

  public InputState getState() {
    return state;
  }

  public Vector2 getRelativeTouchPos() {
    return relativeTouchPos;
  }





  
}
