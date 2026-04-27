package com.assignments.mtnpen.view.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.assignments.mtnpen.model.game.GamePhase;

public class GameUI {
    private final BitmapFont font;
    private float screenWidth;
    private float screenHeight;
    
    public GameUI(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
    }
    
    public void render(Batch batch, GamePhase phase, float phaseTimer, float timeout,
                      int score, String playerName, float velocityInput, float angleInput) {
        batch.begin();
        
        String phaseText = phase.getDisplayText();
        font.setColor(Color.WHITE);
        font.draw(batch, phaseText, screenWidth / 2 - 60, screenHeight - 30);
        
        if (timeout > 0) {
            float timeRemaining = Math.max(0, timeout - phaseTimer);
            String timeText = String.format("%.1f", timeRemaining);
            font.draw(batch, timeText, screenWidth / 2 - 20, screenHeight - 60);
        }
        
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Player: " + playerName, 10, 30);
        font.draw(batch, "Score: " + score, 10, 50);
        
        if (velocityInput > 0) {
            font.setColor(Color.YELLOW);
            String inputText = String.format("Angle: %.1f° | Velocity: %.1f", 
                Math.toDegrees(angleInput), velocityInput);
            font.draw(batch, inputText, screenWidth - 300, 30);
        }
        
        batch.end();
    }
    
    public void dispose() {
        font.dispose();
    }
}
