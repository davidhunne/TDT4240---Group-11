package com.assignments.mtnpen.view.states.lobby;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.controller.lobby.LobbyController;
import com.assignments.mtnpen.model.lobby.LobbyModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
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

    private TextButton readyButton;
    private TextButton startGameButton;
    private TextButton leaveLobbyButton;

    public LobbyState(GameStateManager gsm, String playerName, String lobbyCode, boolean isHost) {
        super(gsm);
        this.model = new LobbyModel(playerName, lobbyCode, isHost);
        this.controller = new LobbyController(model, gsm);

        // Mocking some data for the model
        model.getPlayers().add(playerName + (isHost ? " (Host)" : ""));
        model.getPlayers().add("Player 2");
        model.getPlayers().add("Player 3");
    }

    @Override
    public void create() {
        super.create();
        skin = GameAssetManager.loadUiSkin(); //TODO: Replace with actual asset
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
        hostLabel = new Label("Host: " + (model.isHost() ? model.getPlayerName() : "Another Player"), skin);
        statusLabel = new Label("", skin);

        readyButton = new TextButton("Ready", skin);
        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);

        rootTable.defaults().pad(10).width(320);
        rootTable.add(new Label("Lobby", skin)).padBottom(20).row();
        rootTable.add(lobbyCodeLabel).row();
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
    }

    private void refreshPlayerList() {
        playerTable.clear();
        for (String player : model.getPlayers()) {
            playerTable.add(new Label(player, skin)).row();
        }
    }
}
