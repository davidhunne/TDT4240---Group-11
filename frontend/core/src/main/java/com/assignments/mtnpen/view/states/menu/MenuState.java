package com.assignments.mtnpen.view.states.menu;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.controller.menu.MenuController;
import com.assignments.mtnpen.model.menu.MenuModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuState extends BaseState {

    private final MenuModel model;
    private final MenuController controller;

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
        this.model = new MenuModel();
        this.controller = new MenuController(model, gsm);
    }

    @Override
    public void create() {
        super.create();

        skin = GameAssetManager.loadUiSkin(); //TODO: Replace with actual asset

        buildUi();
        registerListeners();
    }

    @Override
    public void enter() {
        super.enter();
        statusLabel.setText(model.getStatusMessage());
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onExitClicked();
        }
        if (!statusLabel.getText().toString().equals(model.getStatusMessage())) {
            statusLabel.setText(model.getStatusMessage());
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
        statusLabel = new Label(model.getStatusMessage(), skin);

        playerNameField = new TextField(model.getPlayerName(), skin);
        playerNameField.setMessageText("Enter player name");

        lobbyCodeField = new TextField(model.getLobbyCode(), skin);
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
                controller.onCreateLobbyClicked(playerNameField.getText().trim());
            }
        });

        joinLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onJoinLobbyClicked(playerNameField.getText().trim(), lobbyCodeField.getText().trim().toUpperCase());
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onSettingsClicked();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onExitClicked();
            }
        });
    }
}
