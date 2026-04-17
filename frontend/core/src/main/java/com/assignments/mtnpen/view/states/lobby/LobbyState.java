package com.assignments.mtnpen.view.states.lobby;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.game.GameState;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class LobbyState extends BaseState {

    private final String playerName;
    private final String lobbyCode;
    private final boolean isHost;

    private Skin skin;

    private Table rootTable;
    private Table playerTable;

    private Label titleLabel;
    private Label lobbyCodeLabel;
    private Label hostLabel;
    private Label statusLabel;

    private TextButton readyButton;
    private TextButton startGameButton;
    private TextButton leaveLobbyButton;

    private final List<String> players;
    private boolean isReady;

    public LobbyState(GameStateManager gsm, String playerName, String lobbyCode, boolean isHost) {
        super(gsm);
        this.playerName = playerName;
        this.lobbyCode = lobbyCode;
        this.isHost = isHost;
        this.players = new ArrayList<>();
        this.isReady = false;
    }

    @Override
    public void create() {
        super.create();

        skin = GameAssetManager.loadUiSkin(); //TODO: Replace with actual asset

        mockPopulatePlayers();
        buildUi();
        registerListeners();
        refreshPlayerList();
    }

    @Override
    public void enter() {
        super.enter();

        if(isHost) {
            statusLabel.setText("You are the host. Wait for players, then start the race");
        } else {
            statusLabel.setText("Waiting for the host to start the race");
        }
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(new MenuState(gsm));
        }
    }

    @Override
    public void leave() {
        if(skin != null) {
            skin.dispose();
        }
        super.leave();
    }

    private void buildUi() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        playerTable = new Table();

        titleLabel = new Label("Lobby", skin);
        lobbyCodeLabel = new Label("Lobby Code: " + lobbyCode, skin);
        hostLabel = new Label("Host: " + (isHost ? playerName : "Another Player"), skin);
        statusLabel = new Label("", skin);

        readyButton = new TextButton("Ready", skin);
        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);

        rootTable.defaults().pad(10).width(320);

        rootTable.add(titleLabel).padBottom(20).row();
        rootTable.add(lobbyCodeLabel).row();
        rootTable.add(hostLabel).row();

        rootTable.add(new Label("Players:", skin)).padTop(15).row();
        rootTable.add(playerTable).row();

        if(!isHost) {
            rootTable.add(readyButton).padTop(20).row();
        } else {
            rootTable.add(startGameButton).padTop(20).row();
        }

        rootTable.add(leaveLobbyButton).row();
        rootTable.add(statusLabel).padTop(20).row();

        stage.addActor(rootTable);
    }

    private void registerListeners() {
        if (!isHost) {
            readyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isReady = !isReady;
                    readyButton.setText(isReady ? "Not Ready" : "Ready");
                    statusLabel.setText(isReady ? "Ready! Waiting for host..." : "Waiting for you to be ready");
                }
            });
        } else {
            startGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gsm.set(new GameState(gsm));
                }
            });
        }

        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new MenuState(gsm));
            }
        });
    }

    private void refreshPlayerList() {
        playerTable.clear();
        for (String player : players) {
            playerTable.add(new Label(player, skin)).row();
        }
    }

    private void mockPopulatePlayers() {
        players.add(playerName + (isHost ? " (Host)" : ""));
        players.add("Player 2");
        players.add("Player 3");
    }
}
