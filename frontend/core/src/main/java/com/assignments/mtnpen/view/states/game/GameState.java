package com.assignments.mtnpen.view.states.game;

import com.badlogic.gdx.InputMultiplexer;
import com.assignments.mtnpen.controller.game.GameController;
import com.assignments.mtnpen.controller.input.InputController;
import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.model.game.GamePhase;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.view.rendering.FxSystem;
import com.assignments.mtnpen.view.rendering.GameRenderer;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.assignments.mtnpen.view.ui.GameUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameState extends BaseState {
    private final GameModel model;
    private final GameController controller;
    private final InputController inputController;
    private final GameRenderer renderer;
    private final FxSystem fx;
    private final GameUI ui;
    private final java.util.Map<String, int[]> lastPlayerCells = new java.util.HashMap<>();
    private final java.util.Map<String, Integer> lastPlayerScores = new java.util.HashMap<>();
    private final java.util.Set<String> seenObstacleCells = new java.util.HashSet<>();
    private final java.util.Set<String> lastBoostCells = new java.util.HashSet<>();
    private boolean finishCelebrated = false;
    private float finishDelayTimer = -1f;
    private boolean awaitingMoveResolution = false;
    private int pendingFromX, pendingFromY, pendingTargetX, pendingTargetY;
    private static final int FLAG_CELL_X = 50;
    private static final int FLAG_CELL_Y = 99;
    private InputMultiplexer inputMultiplexer;
    private Skin skin;
    private Table overlayTable;
    private TextButton exitGameButton;

    private float initialPollDelay = 1f;
    private boolean gameStateLoaded = false;
    private float autoFallbackTimer = 0f;
    private static final float AUTO_FALLBACK_TIMEOUT = 5f;

    private boolean showDragPreview = false;
    private Vector2 dragScreenStart = new Vector2();
    private Vector2 dragScreenCurrent = new Vector2();
    private float dragAngle = 0f;
    private float dragVelocity = 0f;

    public GameState(GameStateManager gsm, String gameId, String playerId, String playerName) {
        super(gsm);
        this.model = new GameModel(gameId, playerId, playerName);
        this.controller = new GameController(model, gsm);
        this.inputController = new InputController(model, createInputCallback());
        this.renderer = new GameRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.fx = new FxSystem();
        this.ui = new GameUI();

    };

    @Override
    public void create() {
        super.create();
        skin = GameAssetManager.getUiSkin();
        buildUi();
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(inputController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    protected void update(float delta) {
        if (!gameStateLoaded) {
            initialPollDelay -= delta;
            autoFallbackTimer += delta;
            if (initialPollDelay <= 0 || autoFallbackTimer >= AUTO_FALLBACK_TIMEOUT) {
                gameStateLoaded = true;
            }
            return;
        }

        model.update(delta);
        controller.update(delta);
        fx.update(delta);
        emitMoveFeedback();

        if (finishDelayTimer > 0f) {
            finishDelayTimer -= delta;
            if (finishDelayTimer <= 0f) {
                finishDelayTimer = -1f;
                controller.onLocalPlayerReachedFlag();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handleExit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            controller.onGameFinished();
        }

        if ("finished".equals(model.getGameStatus())) {
            controller.onGameFinished();
        }

        Vector2 screenPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());


    }

    @Override
    public void enter() {
        controller.onGameEntered();
        if (inputMultiplexer == null) {
            inputMultiplexer = new InputMultiplexer();
            inputMultiplexer.addProcessor(stage);
            inputMultiplexer.addProcessor(inputController);
        }
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void leave() {
        controller.onGameLeft();
        Gdx.input.setInputProcessor(null);
        renderer.dispose();
        fx.dispose();
        ui.dispose();
        if (skin != null) {
            skin.dispose();
        }
        super.leave();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.95f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        GameModel.PlayerData localPlayer = model.getCurrentPlayer();
        if (localPlayer != null) {
            renderer.setFollowTargetCell(localPlayer.positionX, localPlayer.positionY);
        }

        renderer.beginRender();

        renderer.renderBoard();
        renderer.renderFinishFlag();
        renderer.renderObstacles(model.getObstacles());
        renderer.renderBoosts(model.getBoosts());
        renderer.renderMountainBorder();

        java.util.List<GameRenderer.PlayerRenderData> playerRenderData = new java.util.ArrayList<>();
        java.util.List<GameModel.PlayerData> allPlayers = model.getPlayers();
        for (int i = 0; i < allPlayers.size(); i++) {
            GameModel.PlayerData pd = allPlayers.get(i);
            playerRenderData.add(new GameRenderer.PlayerRenderData(
                    pd.playerId, pd.displayName, pd.positionX, pd.positionY, pd.score, pd.connected, i));
        }
        if (showDragPreview) {
            renderer.renderDragPreview(
                    inputController.isDragging(),
                    dragScreenStart,
                    dragScreenCurrent,
                    dragAngle,
                    dragVelocity,
                    50f,
                    model);
        }
        renderer.renderPlayers(playerRenderData, model.getCurrentTurnPlayerId());

        renderer.renderFx(fx);

        renderer.renderFlagIndicator();

        renderer.endRender();

        stage.act(delta);
        stage.draw();

        GameModel.PlayerData currentPlayer = model.getCurrentPlayer();
        int score = currentPlayer != null ? currentPlayer.score : 0;
        ui.render(
                model.getCurrentPhase(),
                model.getPhaseTimer(),
                model.getInputPhaseTimeout(),
                score,
                model.getPlayerName(),
                dragVelocity,
                dragAngle,
                model.hasMove());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        renderer.updateCamera(width, height);
    }

    private void buildUi() {
        overlayTable = new Table();
        overlayTable.setFillParent(true);
        overlayTable.top().right();
        overlayTable.pad(20f);

        exitGameButton = new TextButton("Exit Game", skin);
        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleExit();
            }
        });

        overlayTable.add(exitGameButton).width(160f).height(44f);
        stage.addActor(overlayTable);
    }

    private void emitMoveFeedback() {
        java.util.Set<String> currentBoosts = new java.util.HashSet<>();
        for (java.util.Map<String, ?> b : model.getBoosts()) {
            Object xo = b.get("x");
            Object yo = b.get("y");
            if (xo == null || yo == null) continue;
            currentBoosts.add(((Number) xo).intValue() + "," + ((Number) yo).intValue());
        }

        for (GameModel.PlayerData pd : model.getPlayers()) {
            String key = pd.playerId;
            int[] prev = lastPlayerCells.get(key);
            int curX = pd.positionX;
            int curY = pd.positionY;
            Vector2 world = renderer.cellToWorld(curX, curY);

            if (prev == null) {
                lastPlayerCells.put(key, new int[]{curX, curY});
                lastPlayerScores.put(key, pd.score);
                continue;
            }

            boolean moved = prev[0] != curX || prev[1] != curY;
            if (moved) {
                String cellKey = curX + "," + curY;
                Integer prevScore = lastPlayerScores.get(key);
                int scoreDelta = prevScore == null ? 0 : pd.score - prevScore;

                boolean boostConsumed = lastBoostCells.contains(cellKey)
                        && !currentBoosts.contains(cellKey);

                if (boostConsumed || scoreDelta > 0) {
                    int amount = scoreDelta > 0 ? scoreDelta : 10;
                    fx.spawnBoost(world.x, world.y, amount);
                } else {
                    fx.spawnLand(world.x, world.y);
                }

                boolean reachedFlagZone = Math.abs(curX - FLAG_CELL_X) <= 4
                        && Math.abs(curY - FLAG_CELL_Y) <= 4;
                if (reachedFlagZone && !finishCelebrated) {
                    fx.spawnFinish(world.x, world.y);
                    finishCelebrated = true;
                    if (key.equals(model.getPlayerId())) {
                        finishDelayTimer = 1.4f;
                    }
                }

                lastPlayerCells.put(key, new int[]{curX, curY});
            }
            lastPlayerScores.put(key, pd.score);
        }

        if (awaitingMoveResolution && !model.hasMove()) {
            GameModel.PlayerData me = model.getCurrentPlayer();
            if (me != null && me.positionX == pendingFromX && me.positionY == pendingFromY) {
                Vector2 hit = renderer.cellToWorld(pendingTargetX, pendingTargetY);
                fx.spawnHit(hit.x, hit.y);
            }
            awaitingMoveResolution = false;
        }

        lastBoostCells.clear();
        lastBoostCells.addAll(currentBoosts);
    }

    private InputController.InputCallback createInputCallback() {
        return new InputController.InputCallback() {
            @Override
            public void onDragStart(float screenX, float screenY) {
                dragScreenStart.set(screenX, screenY);
                dragScreenCurrent.set(screenX, screenY);
                showDragPreview = true;
                Gdx.app.log("GameState", "Drag started at: " + screenX + ", " + screenY);
            }

            @Override
            public void onDragUpdate(float screenX, float screenY, float angle, float velocity) {
                dragScreenCurrent.set(screenX, screenY);
                dragAngle = angle;
                dragVelocity = velocity;
                showDragPreview = true;
            }

            @Override
            public void onDragEnd(float screenX, float screenY, float angle, float velocity) {
                dragAngle = angle;
                dragVelocity = velocity;
                showDragPreview = false;
                dragScreenStart.setZero();
                dragScreenCurrent.setZero();

                if (model.isCurrentPlayerTurnForUI() && model.getCurrentPhase() == GamePhase.INPUT) {
                    Gdx.app.log("GameState",
                            String.format("Submitting move: angle=%.2f, velocity=%.2f", angle, velocity));
                    GameModel.PlayerData me = model.getCurrentPlayer();
                    if (me != null) {
                        pendingFromX = me.positionX;
                        pendingFromY = me.positionY;
                        int[] target = model.getTargetPosition(angle, velocity);
                        pendingTargetX = target[0];
                        pendingTargetY = target[1];
                        awaitingMoveResolution = true;
                    }
                    controller.submitMove(angle, velocity);
                } else {
                    String reason = !model.isCurrentPlayerTurnForUI() ? "not your turn" : "not input phase";
                    Gdx.app.log("GameState", "Move rejected: " + reason);
                }
            }

            @Override
            public void onMenuButtonPressed() {
                handleExit();
            }
        };
    }

    private void handleExit() {
        gsm.set(new MenuState(gsm));
    }
}
