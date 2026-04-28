package com.assignments.mtnpen.view.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.assignments.mtnpen.model.game.GameModel.PlayerData;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.assignments.mtnpen.model.game.GameModel;

import java.util.List;
import java.util.Map;

public class GameRenderer {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final BitmapFont worldFont;

    private static final float CELL_SIZE = 5f;
    private static final int BOARD_WIDTH = 100;
    private static final int BOARD_HEIGHT = 100;
    private static final float BOARD_PIXEL_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final float BOARD_PIXEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    private static final float PENGUIN_BODY = 10f;
    private static final float OBSTACLE_RADIUS = 3.5f;
    private static final float BOOST_SIZE = 4f;
    private static final float FLAG_WIDTH = 20f;
    private static final float FLAG_HEIGHT = 14f;
    private static final float POLE_HEIGHT = 30f;

    private float animationTime = 0f;

    public GameRenderer(int screenWidth, int screenHeight) {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(screenWidth, screenHeight);
        this.worldFont = new BitmapFont();
        this.worldFont.getData().setScale(0.15f);
        updateCamera(screenWidth, screenHeight);
    }

    public void updateCamera(int screenWidth, int screenHeight) {
        float boardAspect = BOARD_PIXEL_WIDTH / BOARD_PIXEL_HEIGHT;
        float screenAspect = (float) screenWidth / screenHeight;

        if (screenAspect > boardAspect) {
            camera.viewportHeight = BOARD_PIXEL_HEIGHT;
            camera.viewportWidth = BOARD_PIXEL_HEIGHT * screenAspect;
        } else {
            camera.viewportWidth = BOARD_PIXEL_WIDTH;
            camera.viewportHeight = BOARD_PIXEL_WIDTH / screenAspect;
        }
        camera.position.set(BOARD_PIXEL_WIDTH / 2, BOARD_PIXEL_HEIGHT / 2, 0);
        camera.update();
    }

    public void beginRender() {
        animationTime += Gdx.graphics.getDeltaTime();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void renderBoard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Snow/ice background
        shapeRenderer.setColor(0.88f, 0.93f, 0.98f, 1f);
        shapeRenderer.rect(0, 0, BOARD_PIXEL_WIDTH, BOARD_PIXEL_HEIGHT);

        // Subtle snow gradient at bottom
        shapeRenderer.setColor(0.95f, 0.97f, 1f, 0.4f);
        shapeRenderer.rect(0, 0, BOARD_PIXEL_WIDTH, BOARD_PIXEL_HEIGHT * 0.15f);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.75f, 0.82f, 0.92f, 0.25f);
        for (int i = 0; i <= BOARD_WIDTH; i += 10) {
            float x = i * CELL_SIZE;
            shapeRenderer.line(x, 0, x, BOARD_PIXEL_HEIGHT);
        }
        for (int i = 0; i <= BOARD_HEIGHT; i += 10) {
            float y = i * CELL_SIZE;
            shapeRenderer.line(0, y, BOARD_PIXEL_WIDTH, y);
        }
        shapeRenderer.end();
    }

    public void renderFinishFlag() {
        float flagX = BOARD_PIXEL_WIDTH / 2f;
        float flagBaseY = BOARD_PIXEL_HEIGHT - 5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Pole
        shapeRenderer.setColor(0.3f, 0.2f, 0.1f, 1f);
        shapeRenderer.rect(flagX - 1f, flagBaseY - POLE_HEIGHT, 2f, POLE_HEIGHT);

        // Flag background (white)
        float fX = flagX + 1f;
        float fY = flagBaseY - 5f;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(fX, fY - FLAG_HEIGHT, FLAG_WIDTH, FLAG_HEIGHT);

        // Checkered pattern
        float sq = FLAG_HEIGHT / 4f;
        shapeRenderer.setColor(Color.BLACK);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < (int) (FLAG_WIDTH / sq); col++) {
                if ((row + col) % 2 == 0) {
                    shapeRenderer.rect(fX + col * sq, fY - FLAG_HEIGHT + row * sq, sq, sq);
                }
            }
        }

        // Pole cap
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(flagX, flagBaseY, 2.5f);

        shapeRenderer.end();
    }

    public void renderObstacles(List<Map<String, Integer>> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Map<String, Integer> obstacle : obstacles) {
            Integer x = obstacle.get("x");
            Integer y = obstacle.get("y");
            if (x == null || y == null) continue;

            float px = x * CELL_SIZE + CELL_SIZE / 2;
            float py = y * CELL_SIZE + CELL_SIZE / 2;
            boolean isWater = ((x * 7 + y * 13) % 3) == 0;

            if (isWater) {
                drawWater(px, py);
            } else {
                drawRock(px, py);
            }
        }

        shapeRenderer.end();
    }

    private void drawRock(float px, float py) {
        shapeRenderer.setColor(0.45f, 0.42f, 0.40f, 1f);
        shapeRenderer.circle(px, py, OBSTACLE_RADIUS);
        shapeRenderer.setColor(0.55f, 0.52f, 0.50f, 1f);
        shapeRenderer.circle(px - 0.5f, py + 0.5f, OBSTACLE_RADIUS * 0.7f);
        shapeRenderer.setColor(0.65f, 0.62f, 0.58f, 1f);
        shapeRenderer.circle(px - 0.8f, py + 1f, OBSTACLE_RADIUS * 0.3f);
    }

    private void drawWater(float px, float py) {
        float wave = (float) Math.sin(animationTime * 2f + px * 0.5f) * 0.5f;
        shapeRenderer.setColor(0.2f, 0.45f, 0.85f, 0.8f);
        shapeRenderer.circle(px, py + wave, OBSTACLE_RADIUS);
        shapeRenderer.setColor(0.35f, 0.6f, 0.95f, 0.6f);
        shapeRenderer.circle(px + 0.5f, py + wave + 0.5f, OBSTACLE_RADIUS * 0.5f);
    }

    public void renderBoosts(List<Map<String, ?>> boosts) {
        if (boosts == null || boosts.isEmpty()) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Map<String, ?> boost : boosts) {
            Object xObj = boost.get("x");
            Object yObj = boost.get("y");
            if (xObj == null || yObj == null) continue;

            int x = ((Number) xObj).intValue();
            int y = ((Number) yObj).intValue();
            float px = x * CELL_SIZE + CELL_SIZE / 2;
            float py = y * CELL_SIZE + CELL_SIZE / 2;

            drawIcePatch(px, py);
        }

        shapeRenderer.end();
    }

    private void drawIcePatch(float px, float py) {
        float s = BOOST_SIZE;
        shapeRenderer.setColor(0.6f, 0.85f, 1f, 0.7f);
        shapeRenderer.triangle(px, py + s, px + s, py, px, py - s);
        shapeRenderer.triangle(px, py + s, px - s, py, px, py - s);
        shapeRenderer.setColor(0.8f, 0.95f, 1f, 0.5f);
        shapeRenderer.circle(px, py, s * 0.35f);
    }

    public void renderPlayers(List<PlayerRenderData> players, String highlightedPlayerId) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (PlayerRenderData player : players) {
            float px = player.position.x * CELL_SIZE + CELL_SIZE / 2;
            float py = player.position.y * CELL_SIZE + CELL_SIZE / 2;
            boolean isCurrent = highlightedPlayerId != null && highlightedPlayerId.equals(player.playerId);
            drawPenguin(px, py, player.connected, isCurrent);
        }
        shapeRenderer.end();

    public void renderPlayers(List<PlayerRenderData> players, String currentPlayerId) {
        if (GameAssetManager.getPenguin1() == null) return;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (PlayerRenderData player : players) {
            if (!player.connected) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            } else if (currentPlayerId.equals(player.playerId)) {
                batch.setColor(1f, 0.2f, 0.2f, 1f);
            } else {
                batch.setColor(0.2f, 0.6f, 1f, 1f);
            }
            batch.draw(GameAssetManager.getPenguin1(), player.position.x - PENGUIN_BODY, player.position.y - PENGUIN_BODY,
                    PENGUIN_BODY * 2, PENGUIN_BODY * 2);
            



        }
        batch.setColor(Color.WHITE);
            float px = player.position.x * CELL_SIZE + CELL_SIZE / 2;
            float py = player.position.y * CELL_SIZE + CELL_SIZE / 2;
            boolean isCurrent = highlightedPlayerId != null && highlightedPlayerId.equals(player.playerId);
            worldFont.setColor(isCurrent ? Color.RED : Color.DARK_GRAY);
            worldFont.draw(batch, player.displayName, px - 8, py + PENGUIN_BODY + 6);
        }
        batch.end();
        
    }
    
    public void renderDragPreview(boolean isDragging, Vector2 dragStart, Vector2 dragCurrent,
                                   float angle, float velocity, float maxVelocity, GameModel model) {
        if (!isDragging) return;

        Vector3 startWorld = new Vector3(dragStartScreen.x, dragStartScreen.y, 0);
        camera.unproject(startWorld);
        Vector3 currentWorld = new Vector3(dragCurrentScreen.x, dragCurrentScreen.y, 0);
        camera.unproject(currentWorld);

        float power = Math.min(velocity / maxVelocity, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float r = Math.min(power * 2f, 1f);
        float g = Math.min((1f - power) * 2f, 1f);
        shapeRenderer.setColor(r, g, 0f, 0.7f);

        float radius = 3f + power * 20f;
        shapeRenderer.circle(startWorld.x, startWorld.y, radius);

        float dx = startWorld.x - currentWorld.x;
        float dy = startWorld.y - currentWorld.y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 1f) {
            float tipX = startWorld.x + dx;
            float tipY = startWorld.y + dy;
            float lineWidth = 1.5f + power * 3f;
            float nx = -dy / len * lineWidth / 2f;
            float ny = dx / len * lineWidth / 2f;
            shapeRenderer.triangle(
                    startWorld.x + nx, startWorld.y + ny,
                    startWorld.x - nx, startWorld.y - ny,
                    tipX, tipY);
            shapeRenderer.triangle(
                    startWorld.x - nx, startWorld.y - ny,
                    tipX + nx, tipY + ny,
                    tipX, tipY);

            float arrowSize = 4f + power * 6f;
            float adx = dx / len;
            float ady = dy / len;
            shapeRenderer.triangle(
                    tipX + adx * arrowSize,
                    tipY + ady * arrowSize,
                    tipX - adx * arrowSize * 0.3f + ny,
                    tipY - ady * arrowSize * 0.3f - nx,
                    tipX - adx * arrowSize * 0.3f - ny,
                    tipY - ady * arrowSize * 0.3f + nx);
        }

        shapeRenderer.end();
    }

    public void endRender() {
        batch.flush();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        worldFont.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getBoardPixelWidth() {
        return BOARD_PIXEL_WIDTH;
    }

    public float getBoardPixelHeight() {
        return BOARD_PIXEL_HEIGHT;
    }

    public Vector2 screenToBoardCoords(float screenX, float screenY) {
        Vector3 worldCoords = new Vector3(screenX, screenY, 0);
        camera.unproject(worldCoords);
        return new Vector2(worldCoords.x / CELL_SIZE, worldCoords.y / CELL_SIZE);
    }

    public static class PlayerRenderData {
        public String playerId;
        public String displayName;
        public Vector2 position;
        public int score;
        public boolean connected;

        public PlayerRenderData(String playerId, String displayName, int x, int y, int score, boolean connected) {
            this.playerId = playerId;
            this.displayName = displayName;
            this.position = new Vector2(x, y);
            this.score = score;
            this.connected = connected;
        }
    }
}
