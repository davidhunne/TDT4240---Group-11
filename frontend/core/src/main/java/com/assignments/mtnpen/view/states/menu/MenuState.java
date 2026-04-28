package com.assignments.mtnpen.view.states.menu;

import com.assignments.mtnpen.controller.menu.MenuController;
import com.assignments.mtnpen.model.menu.MenuModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.ui.FrostBackdrop;
import com.assignments.mtnpen.view.ui.UiTheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuState extends BaseState {

    private final MenuModel model;
    private final MenuController controller;

    private Skin skin;
    private FrostBackdrop backdrop;

    private Label statusLabel;
    private TextField playerNameField;
    private TextField lobbyCodeField;
    private TextButton createTab;
    private TextButton joinTab;
    private TextButton primaryAction;
    private TextButton settingsButton;
    private TextButton exitButton;
    private Container<TextField> lobbyCodeWrap;

    private boolean createMode = true;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        this.model = new MenuModel();
        this.controller = new MenuController(model, gsm);
    }

    @Override
    public void create() {
        super.create();
        skin = UiTheme.build();
        backdrop = new FrostBackdrop();
        backdrop.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        stage.addActor(backdrop);
        buildUi();
        registerListeners();
        applyMode();
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
        if (backdrop != null)
            backdrop.dispose();
        super.leave();
        if (skin != null)
            skin.dispose();
    }

    private void buildUi() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Hero title
        Label title = new Label("MOUNTAIN PENGUIN", skin, "title");
        Label tagline = new Label("Welcome, Create a lobby or join one with a code", skin, "subtitle");

        // Card with the inputs / tabs
        Table card = new Table();
        card.setBackground(skin.getDrawable("card"));
        card.pad(28f, 36f, 28f, 36f);
        card.defaults().pad(8f);

        // Tab row
        createTab = new TextButton("  Create Race  ", skin, "tab");
        joinTab = new TextButton("  Join Race  ", skin, "tab");
        Table tabs = new Table();
        tabs.defaults().height(48f).padRight(8f);
        tabs.add(createTab).width(180f);
        tabs.add(joinTab).width(180f);
        card.add(tabs).colspan(2).padBottom(18f).row();

        // Player name field
        Label nameLabel = new Label("YOUR PENGUIN NAME", skin, "muted");
        playerNameField = new TextField(model.getPlayerName(), skin);
        playerNameField.setMessageText("e.g. FrostyFlipper");
        playerNameField.setMaxLength(20);

        card.add(nameLabel).left().colspan(2).padBottom(2f).row();
        card.add(playerNameField).colspan(2).width(360f).height(46f).padBottom(10f).row();

        // Lobby code field (only shown in join mode)
        Label codeLabel = new Label("LOBBY CODE", skin, "muted");
        lobbyCodeField = new TextField(model.getLobbyCode(), skin);
        lobbyCodeField.setMessageText("Paste the host's code");
        lobbyCodeField.setMaxLength(40);
        lobbyCodeWrap = new Container<>(lobbyCodeField);
        lobbyCodeWrap.width(360f).height(46f);

        card.add(codeLabel).left().colspan(2).padBottom(2f).row();
        card.add(lobbyCodeWrap).colspan(2).padBottom(14f).row();

        // Primary action button
        primaryAction = new TextButton("CREATE RACE", skin, "gold");
        card.add(primaryAction).colspan(2).width(360f).height(58f).padTop(4f).row();

        // Status text under the card
        statusLabel = new Label(model.getStatusMessage(), skin, "status");
        statusLabel.setWrap(true);
        statusLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        // Footer row
        settingsButton = new TextButton("Settings", skin, "ghost");
        exitButton = new TextButton("Quit", skin, "ghost");
        Table footer = new Table();
        footer.defaults().height(40f).padLeft(6f).padRight(6f);
        footer.add(settingsButton).width(140f);
        footer.add(exitButton).width(140f);

        // Compose root
        root.add(title).padBottom(2f).row();
        root.add(tagline).padBottom(22f).row();
        root.add(card).row();
        root.add(statusLabel).width(420f).padTop(14f).row();
        root.add(footer).padTop(16f).row();

        stage.addActor(root);

        // Subtle entrance animation: fade + slide
        card.getColor().a = 0f;
        card.addAction(Actions.parallel(
                Actions.fadeIn(0.45f),
                Actions.moveBy(0f, -16f, 0f),
                Actions.moveBy(0f, 16f, 0.45f)));
        title.getColor().a = 0f;
        title.addAction(Actions.fadeIn(0.6f));
    }

    private void registerListeners() {
        createTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createMode = true;
                applyMode();
            }
        });
        joinTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createMode = false;
                applyMode();
            }
        });

        primaryAction.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                primaryAction.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.06f),
                        Actions.scaleTo(1f, 1f, 0.10f)));
                if (createMode) {
                    controller.onCreateLobbyClicked(playerNameField.getText().trim());
                } else {
                    controller.onJoinLobbyClicked(
                            playerNameField.getText().trim(),
                            lobbyCodeField.getText().trim());
                }
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

    private void applyMode() {
        createTab.setChecked(createMode);
        joinTab.setChecked(!createMode);
        if (createMode) {
            primaryAction.setText("CREATE RACE");
            // Hide the lobby-code row by collapsing the container
            lobbyCodeWrap.setActor(null);
            lobbyCodeWrap.height(0f);
        } else {
            primaryAction.setText("JOIN RACE");
            lobbyCodeWrap.setActor(lobbyCodeField);
            lobbyCodeWrap.height(46f);
        }
        lobbyCodeWrap.invalidateHierarchy();
    }
}
