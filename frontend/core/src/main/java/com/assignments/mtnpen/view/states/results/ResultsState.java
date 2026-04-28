package com.assignments.mtnpen.view.states.results;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.controller.results.ResultsController;
import com.assignments.mtnpen.model.results.ResultsModel;
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

import java.util.Collections;

public class ResultsState extends BaseState {

    private final ResultsModel model;
    private final ResultsController controller;

    private Skin skin;
    private Table rootTable;

    private Label titleLabel;
    private Label placementLabel;
    private Label timeLabel;
    private Label statusLabel;

    private TextButton menuButton;

    public ResultsState(GameStateManager gsm, String playerName, int placement, float finishTime) {
        super(gsm);
        this.model = new ResultsModel();
        this.model.setResults(Collections.singletonList(new ResultsModel.PlayerResult(playerName, placement)));
        this.controller = new ResultsController(model, gsm);
    }

    @Override
    public void create() {
        super.create();
        skin = GameAssetManager.getUiSkin(); //TODO: Replace with actual asset
        buildUi();
        registerListeners();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onBackToMenuClicked();
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

        ResultsModel.PlayerResult res = model.getResults().get(0);

        titleLabel = new Label("Race Results", skin);
        placementLabel = new Label("Placement: " + res.score, skin);
        timeLabel = new Label("Time: --", skin); // For now
        statusLabel = new Label("Well played, " + res.name + "!", skin);

        menuButton = new TextButton("Back to Menu", skin);

        rootTable.defaults().pad(10).width(300);
        rootTable.add(titleLabel).padBottom(30).row();
        rootTable.add(statusLabel).row();
        rootTable.add(placementLabel).row();
        rootTable.add(timeLabel).row();
        rootTable.add(menuButton).padTop(20).row();

        stage.addActor(rootTable);
    }

    private void registerListeners() {
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onBackToMenuClicked();
            }
        });
    }
}
