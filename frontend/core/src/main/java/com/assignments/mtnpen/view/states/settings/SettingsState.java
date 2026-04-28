package com.assignments.mtnpen.view.states.settings;

import com.assignments.mtnpen.controller.settings.SettingsController;
import com.assignments.mtnpen.model.settings.SettingsModel;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.ui.FrostBackdrop;
import com.assignments.mtnpen.view.ui.UiTheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SettingsState extends BaseState {

    private final SettingsModel model;
    private final SettingsController controller;

    private Skin skin;
    private FrostBackdrop backdrop;

    private Label volumeValueLabel;
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
        skin = UiTheme.build();
        backdrop = new FrostBackdrop(0, 3);
        backdrop.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        stage.addActor(backdrop);
        buildUi();
        registerListeners();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onBackClicked();
        }
        String want = String.valueOf(model.getVolume());
        if (!volumeValueLabel.getText().toString().equals(want)) {
            volumeValueLabel.setText(want);
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

        Label title = new Label("SETTINGS", skin, "title");
        Label tagline = new Label("Tune the race to your taste", skin, "subtitle");

        Table card = new Table();
        card.setBackground(skin.getDrawable("card"));
        card.pad(28f, 36f, 28f, 36f);
        card.defaults().pad(8f);

        // Volume row
        Label volumeLabel = new Label("MUSIC VOLUME", skin, "muted");
        card.add(volumeLabel).left().colspan(3).padBottom(2f).row();

        volumeDownButton = new TextButton("-", skin, "primary");
        volumeUpButton = new TextButton("+", skin, "primary");
        volumeValueLabel = new Label(String.valueOf(model.getVolume()), skin, "hero");
        volumeValueLabel.setAlignment(Align.center);

        Table volumeRow = new Table();
        volumeRow.defaults().height(56f);
        volumeRow.add(volumeDownButton).width(72f);
        volumeRow.add(volumeValueLabel).width(180f).padLeft(14f).padRight(14f);
        volumeRow.add(volumeUpButton).width(72f);
        card.add(volumeRow).colspan(3).padBottom(8f).row();

        // Slider track visual under the volume controls
        Table track = new Table();
        track.setBackground(skin.getDrawable("chip"));
        Label trackHint = new Label(buildBar(model.getVolume()), skin, "status");
        trackHint.setAlignment(Align.center);
        track.add(trackHint).pad(8f, 16f, 8f, 16f).width(338f);
        card.add(track).colspan(3).padBottom(6f).row();

        backButton = new TextButton("BACK", skin, "gold");
        card.add(backButton).colspan(3).width(360f).height(58f).padTop(14f).row();

        // Compose
        root.add(title).padBottom(2f).row();
        root.add(tagline).padBottom(22f).row();
        root.add(card).row();

        stage.addActor(root);

        // Entrance animation
        card.getColor().a = 0f;
        card.addAction(Actions.fadeIn(0.4f));
        title.getColor().a = 0f;
        title.addAction(Actions.fadeIn(0.5f));

        // Refresh the bar text whenever the value label updates
        volumeValueLabel.addAction(Actions.forever(Actions.run(
                () -> trackHint.setText(buildBar(model.getVolume()))
        )));
    }

    private String buildBar(int volume) {
        int total = 20;
        int filled = Math.round((volume / 100f) * total);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            sb.append(i < filled ? '|' : '.');
        }
        return sb.toString();
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
