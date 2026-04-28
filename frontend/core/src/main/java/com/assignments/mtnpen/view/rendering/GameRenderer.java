package com.assignments.mtnpen.view.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

    // Number of board cells visible across the screen axis.
    private static final float VIEW_CELLS_NARROW = 44f;

    private static final float PENGUIN_BODY = 9f;
    private static final float OBSTACLE_RADIUS = 5.5f;
    private static final float BOOST_SIZE = 6.5f;
    private static final float FLAG_WIDTH = 28f;
    private static final float FLAG_HEIGHT = 18f;
    private static final float POLE_HEIGHT = 42f;

    private float animationTime = 0f;

    private final Vector2 followTarget = new Vector2(BOARD_PIXEL_WIDTH / 2f, BOARD_PIXEL_HEIGHT / 2f);
    private final Vector2 cameraCenter = new Vector2(BOARD_PIXEL_WIDTH / 2f, BOARD_PIXEL_HEIGHT / 2f);
    private boolean cameraInitialized = false;

    public GameRenderer(int screenWidth, int screenHeight) {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(screenWidth, screenHeight);
        this.worldFont = new BitmapFont();
        this.worldFont.getData().setScale(0.18f);
        updateCamera(screenWidth, screenHeight);
    }

    public void updateCamera(int screenWidth, int screenHeight) {
        float screenAspect = (float) screenWidth / Math.max(1, screenHeight);
        float narrowSize = VIEW_CELLS_NARROW * CELL_SIZE;

        if (screenAspect >= 1f) {
            camera.viewportHeight = narrowSize;
            camera.viewportWidth = narrowSize * screenAspect;
        } else {
            camera.viewportWidth = narrowSize;
            camera.viewportHeight = narrowSize / screenAspect;
        }
        applyCameraPosition();
    }

    public void setFollowTarget(float worldX, float worldY) {
        followTarget.set(worldX, worldY);
        if (!cameraInitialized) {
            cameraCenter.set(followTarget);
            cameraInitialized = true;
        }
    }

    public void setFollowTargetCell(int cellX, int cellY) {
        setFollowTarget(cellX * CELL_SIZE + CELL_SIZE / 2f, cellY * CELL_SIZE + CELL_SIZE / 2f);
    }

    private void applyCameraPosition() {
        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;

        // Interpolate the camera toward the follow target
        float lerp = Math.min(1f, Gdx.graphics.getDeltaTime() * 6f);
        cameraCenter.x += (followTarget.x - cameraCenter.x) * lerp;
        cameraCenter.y += (followTarget.y - cameraCenter.y) * lerp;

        float minX = halfW;
        float maxX = BOARD_PIXEL_WIDTH - halfW;
        float minY = halfH;
        float maxY = BOARD_PIXEL_HEIGHT - halfH;
        if (maxX < minX) {
            cameraCenter.x = BOARD_PIXEL_WIDTH / 2f;
        } else {
            cameraCenter.x = MathUtils.clamp(cameraCenter.x, minX, maxX);
        }
        if (maxY < minY) {
            cameraCenter.y = BOARD_PIXEL_HEIGHT / 2f;
        } else {
            cameraCenter.y = MathUtils.clamp(cameraCenter.y, minY, maxY);
        }

        camera.position.set(cameraCenter.x, cameraCenter.y, 0);
        camera.update();
    }

    public void beginRender() {
        animationTime += Gdx.graphics.getDeltaTime();
        applyCameraPosition();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void renderBoard() {
        float left = camera.position.x - camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float right = left + camera.viewportWidth;
        float top = bottom + camera.viewportHeight;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Snowy gradient background
        Color top1 = new Color(0.78f, 0.88f, 1f, 1f);
        Color bot1 = new Color(0.94f, 0.97f, 1f, 1f);
        shapeRenderer.rect(left, bottom, camera.viewportWidth, camera.viewportHeight,
                bot1, bot1, top1, top1);

        // Snow drifting
        shapeRenderer.setColor(1f, 1f, 1f, 0.55f);
        float driftHeight = camera.viewportHeight * 0.18f;
        for (int i = 0; i < 3; i++) {
            float cx = left + camera.viewportWidth * (0.2f + 0.3f * i);
            float cy = bottom + driftHeight * 0.4f;
            shapeRenderer.circle(cx, cy, driftHeight * 0.9f);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.62f, 0.74f, 0.92f, 0.35f);
        float gridStep = CELL_SIZE * 5f;
        float gridStartX = ((float) Math.floor(left / gridStep)) * gridStep;
        float gridStartY = ((float) Math.floor(bottom / gridStep)) * gridStep;
        for (float x = gridStartX; x <= right; x += gridStep) {
            shapeRenderer.line(x, bottom, x, top);
        }
        for (float y = gridStartY; y <= top; y += gridStep) {
            shapeRenderer.line(left, y, right, y);
        }
        shapeRenderer.end();

        // Frame the actual board so the player has a visible boundary when near the
        // edge
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.35f, 0.5f, 0.75f, 0.8f);
        shapeRenderer.rect(0, 0, BOARD_PIXEL_WIDTH, BOARD_PIXEL_HEIGHT);
        shapeRenderer.end();
    }

    public void renderFinishFlag() {
        float flagX = BOARD_PIXEL_WIDTH / 2f;
        float flagBaseY = BOARD_PIXEL_HEIGHT - 5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Pole shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.ellipse(flagX - 6f, flagBaseY - 1.5f, 14f, 4f);

        // Pole
        shapeRenderer.setColor(0.35f, 0.24f, 0.14f, 1f);
        shapeRenderer.rect(flagX - 1.4f, flagBaseY - POLE_HEIGHT, 2.8f, POLE_HEIGHT);

        // Flag background
        float fX = flagX + 1.4f;
        float fY = flagBaseY - 6f;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(fX, fY - FLAG_HEIGHT, FLAG_WIDTH, FLAG_HEIGHT);

        // Checkered pattern
        float sq = FLAG_HEIGHT / 4f;
        shapeRenderer.setColor(Color.BLACK);
        int cols = (int) (FLAG_WIDTH / sq);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < cols; col++) {
                if ((row + col) % 2 == 0) {
                    shapeRenderer.rect(fX + col * sq, fY - FLAG_HEIGHT + row * sq, sq, sq);
                }
            }
        }

        // Pole cap
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(flagX, flagBaseY, 3.2f);

        shapeRenderer.end();
    }

    public void renderObstacles(List<Map<String, Integer>> obstacles) {
        if (obstacles == null || obstacles.isEmpty())
            return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Map<String, Integer> obstacle : obstacles) {
            Integer x = obstacle.get("x");
            Integer y = obstacle.get("y");
            if (x == null || y == null)
                continue;

            float px = x * CELL_SIZE + CELL_SIZE / 2;
            float py = y * CELL_SIZE + CELL_SIZE / 2;
            if (!isInView(px, py, OBSTACLE_RADIUS * 2f))
                continue;

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
        // Drop shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.22f);
        shapeRenderer.ellipse(px - OBSTACLE_RADIUS, py - OBSTACLE_RADIUS - 1.5f,
                OBSTACLE_RADIUS * 2f, OBSTACLE_RADIUS * 0.9f);

        shapeRenderer.setColor(0.38f, 0.36f, 0.34f, 1f);
        shapeRenderer.circle(px, py, OBSTACLE_RADIUS);
        shapeRenderer.setColor(0.55f, 0.52f, 0.50f, 1f);
        shapeRenderer.circle(px - 0.8f, py + 0.8f, OBSTACLE_RADIUS * 0.7f);
        shapeRenderer.setColor(0.72f, 0.70f, 0.66f, 1f);
        shapeRenderer.circle(px - 1.4f, py + 1.6f, OBSTACLE_RADIUS * 0.32f);

        // Snow cap
        shapeRenderer.setColor(1f, 1f, 1f, 0.9f);
        shapeRenderer.arc(px, py + OBSTACLE_RADIUS * 0.15f, OBSTACLE_RADIUS * 0.95f, 30f, 120f);
    }

    private void drawWater(float px, float py) {
        float wave = MathUtils.sin(animationTime * 2f + px * 0.5f) * 0.5f;
        // Outer ripple
        shapeRenderer.setColor(0.55f, 0.78f, 0.98f, 0.35f);
        shapeRenderer.circle(px, py + wave, OBSTACLE_RADIUS * 1.25f);

        shapeRenderer.setColor(0.18f, 0.42f, 0.82f, 0.92f);
        shapeRenderer.circle(px, py + wave, OBSTACLE_RADIUS);
        shapeRenderer.setColor(0.35f, 0.6f, 0.95f, 0.85f);
        shapeRenderer.circle(px + 0.7f, py + wave + 0.7f, OBSTACLE_RADIUS * 0.55f);
        shapeRenderer.setColor(0.85f, 0.95f, 1f, 0.85f);
        shapeRenderer.circle(px - 1.2f, py + wave + 1.4f, OBSTACLE_RADIUS * 0.18f);
    }

    public void renderBoosts(List<Map<String, ?>> boosts) {
        if (boosts == null || boosts.isEmpty())
            return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Map<String, ?> boost : boosts) {
            Object xObj = boost.get("x");
            Object yObj = boost.get("y");
            if (xObj == null || yObj == null)
                continue;

            int x = ((Number) xObj).intValue();
            int y = ((Number) yObj).intValue();
            float px = x * CELL_SIZE + CELL_SIZE / 2;
            float py = y * CELL_SIZE + CELL_SIZE / 2;
            if (!isInView(px, py, BOOST_SIZE * 2f))
                continue;

            drawIcePatch(px, py);
        }

        shapeRenderer.end();
    }

    private void drawIcePatch(float px, float py) {
        float pulse = 1f + 0.08f * MathUtils.sin(animationTime * 3f + px);
        float s = BOOST_SIZE * pulse;

        // Glow
        shapeRenderer.setColor(0.7f, 0.92f, 1f, 0.35f);
        shapeRenderer.circle(px, py, s * 1.15f);

        // Diamond body
        shapeRenderer.setColor(0.45f, 0.78f, 1f, 0.95f);
        shapeRenderer.triangle(px, py + s, px + s * 0.85f, py, px, py - s);
        shapeRenderer.triangle(px, py + s, px - s * 0.85f, py, px, py - s);

        // Highlight
        shapeRenderer.setColor(0.92f, 0.98f, 1f, 0.95f);
        shapeRenderer.triangle(px, py + s * 0.95f, px + s * 0.35f, py + s * 0.2f, px - s * 0.35f, py + s * 0.2f);

        // Sparkle dot
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(px - s * 0.25f, py + s * 0.45f, s * 0.08f);
    }

    public void renderPlayers(List<PlayerRenderData> players, String currentPlayerId) {
        if (players == null || players.isEmpty())
            return;

        // Drop shadows under each penguin
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (PlayerRenderData player : players) {
            if (!isInView(player.position.x, player.position.y, PENGUIN_BODY * 3f))
                continue;
            shapeRenderer.setColor(0f, 0f, 0f, 0.25f);
            shapeRenderer.ellipse(player.position.x - PENGUIN_BODY,
                    player.position.y - PENGUIN_BODY * 0.95f,
                    PENGUIN_BODY * 2f, PENGUIN_BODY * 0.8f);
        }

        // Highlight ring under the player whose turn it is
        for (PlayerRenderData player : players) {
            if (currentPlayerId == null || !currentPlayerId.equals(player.playerId))
                continue;
            if (!isInView(player.position.x, player.position.y, PENGUIN_BODY * 3f))
                continue;
            float pulse = 1f + 0.08f * MathUtils.sin(animationTime * 4f);
            shapeRenderer.setColor(1f, 0.85f, 0.2f, 0.55f);
            shapeRenderer.circle(player.position.x, player.position.y - PENGUIN_BODY * 0.6f,
                    PENGUIN_BODY * 1.25f * pulse);
            shapeRenderer.setColor(1f, 0.95f, 0.6f, 0.85f);
            shapeRenderer.circle(player.position.x, player.position.y - PENGUIN_BODY * 0.6f,
                    PENGUIN_BODY * 1.05f * pulse);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < players.size(); i++) {
            PlayerRenderData player = players.get(i);
            if (!isInView(player.position.x, player.position.y, PENGUIN_BODY * 3f))
                continue;

            Texture skin = GameAssetManager.getPlayerSkin(player.skinIndex >= 0 ? player.skinIndex : i);
            if (skin == null)
                continue;

            float size = PENGUIN_BODY * 2.4f;
            batch.draw(skin,
                    player.position.x - size / 2f,
                    player.position.y - size / 2f,
                    size, size);

            boolean isCurrent = currentPlayerId != null && currentPlayerId.equals(player.playerId);
            worldFont.setColor(isCurrent ? Color.SCARLET : Color.DARK_GRAY);
            worldFont.draw(batch, player.displayName,
                    player.position.x - size / 2f,
                    player.position.y + size / 2f + 6f);
        }
        batch.end();
    }

    public void renderDragPreview(boolean isDragging, Vector2 dragStartScreen, Vector2 dragCurrentScreen,
            float angle, float velocity, float maxVelocity, GameModel model) {
        if (!isDragging || model.getCurrentPlayer() == null)
            return;

        float startX = model.getCurrentPlayer().positionX * CELL_SIZE + CELL_SIZE / 2f;
        float startY = model.getCurrentPlayer().positionY * CELL_SIZE + CELL_SIZE / 2f;

        Vector3 currentWorld = new Vector3(dragCurrentScreen.x, dragCurrentScreen.y, 0);
        camera.unproject(currentWorld);

        float power = Math.min(velocity / maxVelocity, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float r = Math.min(power * 2f, 1f);
        float g = Math.min((1f - power) * 2f, 1f);
        shapeRenderer.setColor(r, g, 0f, 0.7f);

        float radius = 4f + power * 22f;
        shapeRenderer.circle(startX, startY, radius);

        float dx = startX - currentWorld.x;
        float dy = startY - currentWorld.y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 1f) {
            float tipX = startX + dx;
            float tipY = startY + dy;
            float lineWidth = 2f + power * 4f;
            float nx = -dy / len * lineWidth / 2f;
            float ny = dx / len * lineWidth / 2f;
            shapeRenderer.triangle(
                    startX + nx, startY + ny,
                    startX - nx, startY - ny,
                    tipX, tipY);
            shapeRenderer.triangle(
                    startX - nx, startY - ny,
                    tipX + nx, tipY + ny,
                    tipX, tipY);

            float arrowSize = 5f + power * 2f;
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

    private boolean isInView(float x, float y, float padding) {
        float left = camera.position.x - camera.viewportWidth / 2f - padding;
        float right = camera.position.x + camera.viewportWidth / 2f + padding;
        float bottom = camera.position.y - camera.viewportHeight / 2f - padding;
        float top = camera.position.y + camera.viewportHeight / 2f + padding;
        return x >= left && x <= right && y >= bottom && y <= top;
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

    public float getCellSize() {
        return CELL_SIZE;
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
        public int skinIndex;

        public PlayerRenderData(String playerId, String displayName, int x, int y, int score, boolean connected) {
            this(playerId, displayName, x, y, score, connected, -1);
        }

        public PlayerRenderData(String playerId, String displayName, int x, int y, int score, boolean connected,
                int skinIndex) {
            this.playerId = playerId;
            this.displayName = displayName;
            this.position = new Vector2(x * CELL_SIZE + CELL_SIZE / 2f, y * CELL_SIZE + CELL_SIZE / 2f);
            this.score = score;
            this.connected = connected;
            this.skinIndex = skinIndex;
        }
    }
}
