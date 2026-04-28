package com.assignments.mtnpen.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.assignments.mtnpen.model.game.GamePhase;

public class GameUI {
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final OrthographicCamera hudCamera;

    public GameUI() {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.batch = new SpriteBatch();
        this.hudCamera = new OrthographicCamera();
        updateCamera();
    }

    private void updateCamera() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();
    }

    public void render(GamePhase phase, float phaseTimer, float timeout,
            int score, String playerName, float velocityInput, float angleInput) {
        updateCamera();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        float screenWidth = hudCamera.viewportWidth;
        float screenHeight = hudCamera.viewportHeight;

        String phaseText = phase.getDisplayText();
        font.setColor(Color.WHITE);
        font.draw(batch, phaseText, screenWidth / 2 - 60, screenHeight - 30);

        if (timeout > 0) {
            float timeRemaining = Math.max(0, timeout - phaseTimer);
            String timeText = String.format("Time: %.1f", timeRemaining);
            font.draw(batch, timeText, screenWidth / 2 - 30, screenHeight - 60);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Player: " + playerName, 10, 30);
        font.draw(batch, "Score: " + score, 10, 50);

        if (velocityInput > 0) {
            font.setColor(Color.RED);
            String inputText = String.format("Angle: %.1f° | Velocity: %.1f",
                    Math.toDegrees(angleInput), velocityInput);
            font.draw(batch, inputText, screenWidth - 300, 30);
        }

        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
