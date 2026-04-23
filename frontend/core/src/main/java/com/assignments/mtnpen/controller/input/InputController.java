package com.assignments.mtnpen.controller.input;

import com.assignments.mtnpen.model.parameters.GameParameters;

import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.utils.TimeUtils;

public class InputController extends InputAdapter {

  private boolean dragging = false;
  private boolean touching = false;
  private Vector2 touchOrigin = new Vector2();
  private Vector2 currentTouchPos = new Vector2();

  

  public interface DragListener {
    void onDragReleased(float deltaX, float deltaY);
  }

  private final DragListener dragListener;

  private long touchStartMillis;
  private Vector2 velocityBuffer = new Vector2();

  public InputController(DragListener dragListener) {
    this.dragListener = dragListener;
  }


  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (pointer!=0) return false;
    touching = true;
    dragging = false;
    touchStartMillis = TimeUtils.millis();

    touchOrigin.set(screenX, screenY);

    currentTouchPos.set(screenX, screenY);
    velocityBuffer.setZero();
    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (pointer!=0 || !touching) return false;


    currentTouchPos.set(screenX, screenY);
    velocityBuffer.set(currentTouchPos).sub(touchOrigin);

    if (!dragging){
      float timeSinceTouchStart = (TimeUtils.millis() - touchStartMillis) / 1000f;
      if (timeSinceTouchStart > GameParameters.TAP_OR_DRAG_THRESHOLD){
        dragging = true;
      }
    }


    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (pointer!=0 || !touching) return false;

    touching = false;

    if (dragging){
      dragListener.onDragReleased(velocityBuffer.x, -velocityBuffer.y);
    }



    dragging = false;
    velocityBuffer.setZero();
    return true;
  }

  public boolean isDragging() {
    return dragging;
  }

  public boolean isTouching() {
    return touching;
  }

  public Vector2 getCurrentTouchPos() {
    return currentTouchPos;
  }

  public Vector2 getTouchOrigin() {
    return touchOrigin;
  }

  public Vector2 getVelocityBuffer() {
    return velocityBuffer;
  }


  
}
