package com.assignments.mtnpen.model.game;

public enum GamePhase {
    INPUT("Waiting for your move..."),
    OPPONENT_INPUT("Waiting for other players..."),
    LAUNCH("Launching!"),
    RESOLVING("Movement resolving..."),
    COUNTDOWN("Round ending in..."),
    FINISHED("Race finished!");
    
    private final String displayText;
    
    GamePhase(String displayText) {
        this.displayText = displayText;
    }
    
    public String getDisplayText() {
        return displayText;
    }
}
