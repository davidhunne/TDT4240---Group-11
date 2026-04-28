package com.assignments.mtnpen.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.assignments.mtnpen.model.game.GamePhase;

public class GameUI {
    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera hudCamera;

    public GameUI() {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(1.5f);
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
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
            int score, String playerName, float velocityInput, float angleInput,
            boolean moveSubmitted) {
        updateCamera();

        float screenWidth = hudCamera.viewportWidth;
        float screenHeight = hudCamera.viewportHeight;

        if (phase == GamePhase.INPUT && timeout > 0) {
            float timeRemaining = Math.max(0, timeout - phaseTimer);
            float fraction = timeRemaining / timeout;

            shapeRenderer.setProjectionMatrix(hudCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.6f);
            shapeRenderer.rect(0, screenHeight - 12, screenWidth, 12);

            float r = Math.min((1f - fraction) * 2f, 1f);
            float g = Math.min(fraction * 2f, 1f);
            shapeRenderer.setColor(r, g, 0f, 0.9f);
            shapeRenderer.rect(0, screenHeight - 12, screenWidth * fraction, 12);

            shapeRenderer.end();
        }

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String phaseText;
        if (moveSubmitted && phase == GamePhase.RESOLVING) {
            phaseText = "Move submitted! Waiting for results...";
            titleFont.setColor(Color.GREEN);
        } else {
            phaseText = phase.getDisplayText();
            titleFont.setColor(Color.WHITE);
        }
        titleFont.draw(batch, phaseText, 20, screenHeight - 24);

        if (phase == GamePhase.INPUT && timeout > 0) {
            float timeRemaining = Math.max(0, timeout - phaseTimer);
            font.setColor(timeRemaining < 10f ? Color.RED : Color.WHITE);
            String timeText = String.format("Time: %.1fs", timeRemaining);
            font.draw(batch, timeText, screenWidth - 120, screenHeight - 28);
        }

        font.setColor(Color.WHITE);
        font.draw(batch, "Player: " + playerName, 10, 50);
        font.draw(batch, "Score: " + score, 10, 30);

        if (velocityInput > 0) {
            font.setColor(Color.YELLOW);
            String inputText = String.format("Angle: %.1f\u00B0 | Power: %.1f",
                    Math.toDegrees(angleInput), velocityInput);
            font.draw(batch, inputText, screenWidth - 280, 30);
        }

        batch.end();
    }

    public void dispose() {
        font.dispose();
        titleFont.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}
