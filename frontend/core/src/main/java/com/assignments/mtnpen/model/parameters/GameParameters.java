package com.assignments.mtnpen.model.parameters;

public final class GameParameters {

  private GameParameters(){}

  public static final float WORLD_WIDTH = 800;
  public static final float WORLD_HEIGHT = 600;


  public static final float TAP_OR_DRAG_THRESHOLD = 200; // milliseconds

  public static final float LAUNCH_VELOCITY_SCALE = 0.01f; // Scale down touch velocity to game world velocity

  public static final float MAX_LAUNCH_RADIUS = 300; // Maximum distance from penguin to start touch
  
}
