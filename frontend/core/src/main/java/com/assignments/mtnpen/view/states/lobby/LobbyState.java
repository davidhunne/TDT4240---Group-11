package com.assignments.mtnpen.view.states.lobby;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.controller.lobby.LobbyController;
import com.assignments.mtnpen.model.lobby.LobbyModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.util.ClipboardUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LobbyState extends BaseState {

    private final LobbyController controller;
    private final LobbyModel model;

    private Skin skin;
    private Table rootTable;
    private Table playerTable;

    private Label lobbyCodeLabel;
    private Label hostLabel;
    private Label statusLabel;
    private Label copyFeedbackLabel;

    private TextButton readyButton;
    private TextButton startGameButton;
    private TextButton leaveLobbyButton;
    private TextButton copyCodeButton;
    private float pollTimer;
    private float copyFeedbackTimer = 0f;
    private static final float COPY_FEEDBACK_DURATION = 1.5f;

    public LobbyState(GameStateManager gsm, String playerName, String lobbyCode, boolean isHost) {
        super(gsm);
        this.model = new LobbyModel(playerName, gsm.getPlayerId(), lobbyCode, isHost);
        this.controller = new LobbyController(model, gsm);
        model.getPlayers().add(playerName + (isHost ? " (Host)" : ""));
    }

    @Override
    public void create() {
        super.create();
        skin = GameAssetManager.getUiSkin(); //TODO: Replace with actual asset
        buildUi();
        registerListeners();
        refreshPlayerList();
    }

    @Override
    public void enter() {
        super.enter();
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        if (model.isHost()) {
            statusLabel.setText("You are the host. Wait for players, then start the race");
        } else {
            statusLabel.setText(model.isReady() ? "Ready! Waiting for host..." : "Waiting for the host to start the race");
        }
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onLeaveLobbyClicked();
        }
        pollTimer += delta;
        if (pollTimer >= 1f) {
            pollTimer = 0f;
            controller.pollGame(new Runnable() {
                @Override
                public void run() {
                    refreshPlayerList();
                    updateStatusLabel();
                }
            });
        }
        
        if (copyFeedbackTimer > 0) {
            copyFeedbackTimer -= delta;
            if (copyFeedbackTimer <= 0) {
                copyFeedbackLabel.setText("");
            }
        }
    }

    @Override
    public void leave() {
        if (skin != null) skin.dispose();
        super.leave();
    }

    private void buildUi() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        playerTable = new Table();

        lobbyCodeLabel = new Label("Lobby Code: " + model.getLobbyCode(), skin);
        hostLabel = new Label("Host: " + (model.getHostName().isEmpty() ? "Waiting..." : model.getHostName()), skin);
        statusLabel = new Label("", skin);
        copyFeedbackLabel = new Label("", skin);

        readyButton = new TextButton("Ready", skin);
        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);
        copyCodeButton = new TextButton("Copy Code", skin);

        rootTable.defaults().pad(10).width(320);
        rootTable.add(new Label("Lobby", skin)).padBottom(20).row();
        
        Table codeRow = new Table();
        codeRow.add(lobbyCodeLabel).expandX().fillX();
        codeRow.add(copyCodeButton).width(100).padLeft(10);
        rootTable.add(codeRow).fillX().row();
        
        rootTable.add(copyFeedbackLabel).row();
        rootTable.add(hostLabel).row();
        rootTable.add(new Label("Players:", skin)).padTop(15).row();
        rootTable.add(playerTable).row();

        if (!model.isHost()) {
            rootTable.add(readyButton).padTop(20).row();
        } else {
            rootTable.add(startGameButton).padTop(20).row();
        }

        rootTable.add(leaveLobbyButton).row();
        rootTable.add(statusLabel).padTop(20).row();

        stage.addActor(rootTable);
    }

    private void registerListeners() {
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onReadyClicked();
                readyButton.setText(model.isReady() ? "Not Ready" : "Ready");
                updateStatusLabel();
            }
        });

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onStartGameClicked();
            }
        });

        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onLeaveLobbyClicked();
            }
        });
        
        copyCodeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClipboardUtil.copyToClipboard(model.getLobbyCode());
                copyFeedbackLabel.setText("Copied!");
                copyFeedbackTimer = COPY_FEEDBACK_DURATION;
            }
        });
    }

    private void refreshPlayerList() {
        playerTable.clear();
        hostLabel.setText("Host: " + (model.getHostName().isEmpty() ? "Waiting..." : model.getHostName()));
        for (String player : model.getPlayers()) {
            playerTable.add(new Label(player, skin)).row();
        }
    }
}
