package com.assignments.mtnpen.view.states.menu;

import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.lobby.LobbyState;
import com.assignments.mtnpen.view.states.settings.SettingsState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuState extends BaseState {

    private Skin skin;

    private Table rootTable;

    private Label titleLabel;
    private Label statusLabel;

    private TextField playerNameField;
    private TextField lobbyCodeField;

    private TextButton createLobbyButton;
    private TextButton joinLobbyButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void create() {
        super.create();

        skin = new Skin(Gdx.files.internal("skin/uiskin.json")); //TODO: Replace with actual asset

        buildUi();
        registerListeners();
    }

    @Override
    public void enter() {
        super.enter();
        statusLabel.setText("Welcome! Create a lobby or join one with a code");
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void leave() {
        super.leave();
        skin.dispose();
    }

    private void buildUi() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        titleLabel = new Label("Mountain Penguins", skin);
        statusLabel = new Label("", skin);

        playerNameField = new TextField("", skin);
        playerNameField.setMessageText("Enter player name");

        lobbyCodeField = new TextField("", skin);
        lobbyCodeField.setMessageText("Enter lobby code");
        lobbyCodeField.setMaxLength(6);

        createLobbyButton = new TextButton("Create Lobby", skin);
        joinLobbyButton = new TextButton("Join Lobby", skin);
        settingsButton = new TextButton("Settings", skin);
        exitButton = new TextButton("Exit", skin);

        rootTable.defaults().pad(10).width(300);

        rootTable.add(titleLabel).padBottom(30).row();
        rootTable.add(playerNameField).row();
        rootTable.add(lobbyCodeField).row();
        rootTable.add(createLobbyButton).row();
        rootTable.add(joinLobbyButton).row();
        rootTable.add(settingsButton).row();
        rootTable.add(exitButton).row();
        rootTable.add(statusLabel).padTop(20).row();

        stage.addActor(rootTable);
    }

    private void registerListeners() {
        createLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final String playerName = playerNameField.getText().trim();

                if (playerName.isEmpty()) {
                    statusLabel.setText("Please enter a player name.");
                    return;
                }

                statusLabel.setText("Creating a lobby...");
                gsm.getNetworkManager().createGame("temp-id-" + playerName, playerName, new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.postRunnable(() -> gsm.set(new LobbyState(gsm, playerName, "HOST12", true)));
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.postRunnable(() -> statusLabel.setText("Failed to create lobby: " + t.getMessage()));
                    }
                });
            }
        });

        joinLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final String playerName = playerNameField.getText().trim();
                final String lobbyCode = lobbyCodeField.getText().trim().toUpperCase();

                if (playerName.isEmpty()) {
                    statusLabel.setText("Please enter a player name.");
                    return;
                }

                if (lobbyCode.isEmpty()) {
                    statusLabel.setText("Please enter a lobby code.");
                    return;
                }

                statusLabel.setText("Joining lobby " + lobbyCode + "...");
                gsm.getNetworkManager().joinGame(lobbyCode, "temp-id-" + playerName, playerName, new NetworkManager.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.postRunnable(() -> gsm.set(new LobbyState(gsm, playerName, lobbyCode, false)));
                    }

                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.postRunnable(() -> statusLabel.setText("Failed to join lobby: " + t.getMessage()));
                    }
                });
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new SettingsState(gsm));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }
}
