package com.assignments.mtnpen.view.states.lobby;

import com.assignments.mtnpen.controller.lobby.LobbyController;
import com.assignments.mtnpen.model.lobby.LobbyModel;
import com.assignments.mtnpen.util.ClipboardUtil;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.ui.FrostBackdrop;
import com.assignments.mtnpen.view.ui.UiTheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class LobbyState extends BaseState {

    private final LobbyController controller;
    private final LobbyModel model;

    private Skin skin;
    private FrostBackdrop backdrop;

    private Table playerListTable;
    private Label statusLabel;
    private Label copyFeedbackLabel;
    private Label hostLabel;
    private Label codeLabel;

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
        skin = UiTheme.build();
        backdrop = new FrostBackdrop(60, 3);
        backdrop.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        stage.addActor(backdrop);
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
            statusLabel.setText("You are the host. Wait for penguins, then start the race.");
        } else {
            statusLabel.setText(model.isReady()
                    ? "Ready! Waiting for the host..."
                    : "Waiting for the host to launch the race");
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
            controller.pollGame(() -> {
                refreshPlayerList();
                updateStatusLabel();
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
        if (backdrop != null) backdrop.dispose();
        super.leave();
        if (skin != null) skin.dispose();
    }

    private void buildUi() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label title = new Label("LOBBY", skin, "title");

        // Lobby code chip — large, prominent
        Table codeChip = new Table();
        codeChip.setBackground(skin.getDrawable("chip"));
        codeChip.pad(14f, 22f, 14f, 22f);
        Label codePrefix = new Label("CODE", skin, "muted");
        codeLabel = new Label(model.getLobbyCode(), skin, "code");
        copyCodeButton = new TextButton("Copy", skin, "ghost");
        copyFeedbackLabel = new Label("", skin, "status");

        codeChip.add(codePrefix).padRight(14f);
        codeChip.add(codeLabel).padRight(18f);
        codeChip.add(copyCodeButton).width(110f).height(40f);

        hostLabel = new Label("Host: Waiting...", skin, "subtitle");

        // Players card
        Table playersCard = new Table();
        playersCard.setBackground(skin.getDrawable("card"));
        playersCard.pad(20f, 24f, 20f, 24f);
        Label playersTitle = new Label("RACERS", skin, "muted");
        playersCard.add(playersTitle).left().padBottom(10f).row();

        playerListTable = new Table();
        playerListTable.top().left();
        playerListTable.defaults().pad(6f);
        playersCard.add(playerListTable).width(440f).minHeight(180f).row();

        // Action button
        if (model.isHost()) {
            startGameButton = new TextButton("START THE RACE", skin, "gold");
        } else {
            readyButton = new TextButton("I'M READY", skin, "gold");
        }

        leaveLobbyButton = new TextButton("Leave Lobby", skin, "danger");

        statusLabel = new Label("", skin, "status");
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        // Compose
        root.add(title).padBottom(10f).row();
        root.add(codeChip).padBottom(6f).row();
        root.add(copyFeedbackLabel).padBottom(2f).row();
        root.add(hostLabel).padBottom(14f).row();
        root.add(playersCard).row();

        Table actionRow = new Table();
        actionRow.defaults().pad(8f);
        if (model.isHost()) {
            actionRow.add(startGameButton).width(280f).height(58f);
        } else {
            actionRow.add(readyButton).width(280f).height(58f);
        }
        root.add(actionRow).padTop(16f).row();
        root.add(statusLabel).width(420f).padTop(6f).row();
        root.add(leaveLobbyButton).width(180f).height(40f).padTop(14f).row();

        stage.addActor(root);

        // Subtle entrance
        playersCard.getColor().a = 0f;
        playersCard.addAction(Actions.fadeIn(0.4f));
        codeChip.getColor().a = 0f;
        codeChip.addAction(Actions.sequence(Actions.delay(0.05f), Actions.fadeIn(0.4f)));
    }

    private void registerListeners() {
        if (readyButton != null) {
            readyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.onReadyClicked();
                    readyButton.setText(model.isReady() ? "NOT READY" : "I'M READY");
                    updateStatusLabel();
                }
            });
        }

        if (startGameButton != null) {
            startGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.onStartGameClicked();
                }
            });
        }

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
                copyFeedbackLabel.setText("Copied to clipboard!");
                copyFeedbackTimer = COPY_FEEDBACK_DURATION;
            }
        });
    }

    private void refreshPlayerList() {
        playerListTable.clear();
        hostLabel.setText("Host: " + (model.getHostName().isEmpty() ? "Waiting..." : model.getHostName()));
        int idx = 0;
        for (String player : model.getPlayers()) {
            playerListTable.add(buildPlayerChip(idx, player)).left().fillX().width(420f).row();
            idx++;
        }
        if (model.getPlayers().isEmpty()) {
            Label empty = new Label("Waiting for penguins to join...", skin, "muted");
            playerListTable.add(empty).pad(20f).row();
        }
    }

    private Table buildPlayerChip(int index, String name) {
        Table row = new Table();
        row.setBackground(skin.getDrawable("card-light"));
        row.pad(8f, 12f, 8f, 12f);

        Texture skinTex = GameAssetManager.getPlayerSkin(index);
        if (skinTex != null) {
            Image avatar = new Image(new TextureRegionDrawable(skinTex));
            avatar.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            row.add(avatar).size(48f, 48f).padRight(14f);
        }

        Label nameLabel = new Label(name, skin, "default");
        row.add(nameLabel).left().expandX().fillX();

        Label num = new Label("#" + (index + 1), skin, "status");
        row.add(num).right().padLeft(8f);

        return row;
    }
}
