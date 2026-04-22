package com.assignments.mtnpen.view.states.settings;

import com.assignments.mtnpen.controller.settings.SettingsController;
import com.assignments.mtnpen.model.settings.SettingsModel;
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

public class SettingsState extends BaseState {

    private final SettingsModel model;
    private final SettingsController controller;

    private Skin skin;
    private Table rootTable;

    private Label titleLabel;
    private Label volumeLabel;
    private Label statusLabel;

    private TextButton volumeDownButton;
    private TextButton volumeUpButton;
    private TextButton backButton;

    public SettingsState(GameStateManager gsm) {
        super(gsm);
        this.model = new SettingsModel();
        this.controller = new SettingsController(model, gsm);
    }

    @Override
    public void create() {
        super.create();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json")); //TODO: Replace with actual asset
        buildUi();
        registerListeners();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onBackClicked();
        }
        if (!volumeLabel.getText().toString().contains(String.valueOf(model.getVolume()))) {
            volumeLabel.setText("Volume: " + model.getVolume());
        }
    }

    @Override
    public void leave() {
        if (skin != null) {
            skin.dispose();
        }
        super.leave();
    }

    private void buildUi() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        titleLabel = new Label("Settings", skin);
        volumeLabel = new Label("Volume: " + model.getVolume(), skin);
        statusLabel = new Label("", skin);

        volumeDownButton = new TextButton("-", skin);
        volumeUpButton = new TextButton("+", skin);
        backButton = new TextButton("Back", skin);

        rootTable.defaults().pad(10).width(300);
        rootTable.add(titleLabel).padBottom(30).row();

        Table volumeTable = new Table();
        volumeTable.add(volumeDownButton).width(50);
        volumeTable.add(volumeLabel).width(100).center();
        volumeTable.add(volumeUpButton).width(50);

        rootTable.add(volumeTable).row();
        rootTable.add(backButton).row();
        rootTable.add(statusLabel).padTop(20).row();

        stage.addActor(rootTable);
    }

    private void registerListeners() {
        volumeDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onVolumeDown();
            }
        });

        volumeUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onVolumeUp();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onBackClicked();
            }
        });
    }
}
