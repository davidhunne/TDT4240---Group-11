package com.assignments.mtnpen.view.states.game;

import com.badlogic.gdx.InputMultiplexer;
import com.assignments.mtnpen.controller.game.GameController;
import com.assignments.mtnpen.controller.input.InputController;
import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.model.game.GamePhase;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
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
    private final GameUI ui;
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

        java.util.List<GameRenderer.PlayerRenderData> playerRenderData = new java.util.ArrayList<>();
        java.util.List<GameModel.PlayerData> allPlayers = model.getPlayers();
        for (int i = 0; i < allPlayers.size(); i++) {
            GameModel.PlayerData pd = allPlayers.get(i);
            playerRenderData.add(new GameRenderer.PlayerRenderData(
                    pd.playerId, pd.displayName, pd.positionX, pd.positionY, pd.score, pd.connected, i));
        }
        renderer.renderPlayers(playerRenderData, model.getCurrentTurnPlayerId());

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
