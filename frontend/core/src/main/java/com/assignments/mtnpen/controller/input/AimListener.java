package com.assignments.mtnpen.controller.input;

public interface AimListener {

  void onAimLock(float velocityX, float velocityY);

  void onAimCancel();
  
}
