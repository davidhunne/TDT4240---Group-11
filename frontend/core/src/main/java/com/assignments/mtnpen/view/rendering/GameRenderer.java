package com.assignments.mtnpen.view.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;

import java.util.List;
import java.util.Map;

public class GameRenderer {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    
    private static final float CELL_SIZE = 5f;
    private static final int BOARD_WIDTH = 100;
    private static final int BOARD_HEIGHT = 100;
    private static final float BOARD_PIXEL_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final float BOARD_PIXEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;
    
    private static final float PENGUIN_RADIUS = 2f;
    private static final float OBSTACLE_SIZE = 2f;
    private static final float BOOST_RADIUS = 1.5f;
    private static final float GOAL_SIZE = 3f;
    
    private float animationTime = 0f;
    
    public GameRenderer(int screenWidth, int screenHeight) {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(screenWidth, screenHeight);
        updateCamera(screenWidth, screenHeight);
    }
    
    public void updateCamera(int screenWidth, int screenHeight) {
        camera.viewportWidth = screenWidth;
        camera.viewportHeight = screenHeight;
        float boardAspect = BOARD_PIXEL_WIDTH / BOARD_PIXEL_HEIGHT;
        float screenAspect = (float) screenWidth / screenHeight;
        
        if (boardAspect > screenAspect) {
            // Board is wider: fit to width
            camera.position.set(BOARD_PIXEL_WIDTH / 2, BOARD_PIXEL_HEIGHT / 2, 0);
        } else {
            // Screen is wider: fit to height
            camera.position.set(BOARD_PIXEL_WIDTH / 2, BOARD_PIXEL_HEIGHT / 2, 0);
        }
        camera.update();
    }
    
    public void beginRender() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }
    
    public void renderBoard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(0.9f, 0.95f, 1f, 1f);
        shapeRenderer.rect(0, 0, BOARD_PIXEL_WIDTH, BOARD_PIXEL_HEIGHT);
        
        shapeRenderer.setColor(1f, 0.84f, 0f, 0.6f);
        float goalX = BOARD_PIXEL_WIDTH - GOAL_SIZE * CELL_SIZE;
        float goalY = BOARD_PIXEL_HEIGHT - GOAL_SIZE * CELL_SIZE;
        shapeRenderer.rect(goalX, goalY, GOAL_SIZE * CELL_SIZE, GOAL_SIZE * CELL_SIZE);
        
        shapeRenderer.setColor(0.7f, 0.8f, 0.95f, 0.3f);
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
    
    public void renderObstacles(List<Map<String, Integer>> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 1f);
        
        for (Map<String, Integer> obstacle : obstacles) {
            Integer x = obstacle.get("x");
            Integer y = obstacle.get("y");
            if (x != null && y != null) {
                float px = x * CELL_SIZE + CELL_SIZE / 2;
                float py = y * CELL_SIZE + CELL_SIZE / 2;
                shapeRenderer.rect(px - OBSTACLE_SIZE / 2, py - OBSTACLE_SIZE / 2,
                        OBSTACLE_SIZE, OBSTACLE_SIZE);
            }
        }
        
        shapeRenderer.end();
    }
    
    public void renderBoosts(List<Map<String, ?>> boosts) {
        if (boosts == null || boosts.isEmpty()) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Map<String, ?> boost : boosts) {
            Object xObj = boost.get("x");
            Object yObj = boost.get("y");
            Object amountObj = boost.get("amount");
            
            if (xObj != null && yObj != null) {
                int x = ((Number) xObj).intValue();
                int y = ((Number) yObj).intValue();
                float amount = amountObj != null ? ((Number) amountObj).floatValue() : 1f;
                
                float intensity = Math.min(amount / 100f, 1f);
                shapeRenderer.setColor(0.2f, intensity, 0.2f, 0.8f);
                
                float px = x * CELL_SIZE + CELL_SIZE / 2;
                float py = y * CELL_SIZE + CELL_SIZE / 2;
                shapeRenderer.circle(px, py, BOOST_RADIUS);
            }
        }
        
        shapeRenderer.end();
    }
    
    public void renderPlayers(List<PlayerRenderData> players, String currentPlayerId) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (PlayerRenderData player : players) {
            if (!player.connected) {
                shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            } else if (currentPlayerId.equals(player.playerId)) {
                shapeRenderer.setColor(1f, 0.2f, 0.2f, 1f);
            } else {
                shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
            }
            
            float px = player.position.x * CELL_SIZE + CELL_SIZE / 2;
            float py = player.position.y * CELL_SIZE + CELL_SIZE / 2;
            shapeRenderer.circle(px, py, PENGUIN_RADIUS);
        }
        
        shapeRenderer.end();
    }
    
    public void renderDragPreview(boolean isDragging, Vector2 dragStart, Vector2 dragCurrent,
                                   float angle, float velocity, float maxVelocity) {
        if (!isDragging) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 0.8f);
        
        shapeRenderer.line(dragStart.x, dragStart.y, dragCurrent.x, dragCurrent.y);
        
        float radius = (velocity / maxVelocity) * 30f;
        shapeRenderer.circle(dragStart.x, dragStart.y, radius);
        
        shapeRenderer.end();
    }
    
    public void renderCountdown(float timeRemaining, float totalTime) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f);
        shapeRenderer.rect(0, BOARD_PIXEL_HEIGHT - 20, BOARD_PIXEL_WIDTH, 20);
        shapeRenderer.end();
    }
    
    public void endRender() {
        batch.flush();
    }
    
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
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
        
        float boardX = worldCoords.x / CELL_SIZE;
        float boardY = worldCoords.y / CELL_SIZE;
        
        return new Vector2(boardX, boardY);
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
