package com.assignments.mtnpen.state.results;

import com.assignments.mtnpen.state.base.BaseState;
import com.assignments.mtnpen.state.manager.GameStateManager;
import com.assignments.mtnpen.state.game.GameState;
import com.assignments.mtnpen.state.menu.MenuState;
import com.assignments.mtnpen.state.settings.SettingsState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
public class ResultsState extends BaseState {

    private final String playerName;
    private final int placement;
    private final float finishTime;

    private Skin skin;

    private Table rootTable;

    private Label titleLabel;
    private Label placementLabel;
    private Label timeLabel;
    private Label statusLabel;

    private TextButton menuButton;

    public ResultsState(GameStateManager gsm, String playerName, int placement, float finishTime) {
        super(gsm);
        this.playerName = playerName;
        this.placement = placement;
        this.finishTime = finishTime;
    }

    @Override
    public void create() {
        super.create();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        buildUi();
        registerListeners();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(new MenuState(gsm));
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

        titleLabel = new Label("Race Results", skin);
        placementLabel = new Label("Placement: " + placement, skin);
        timeLabel = new Label("Time: " + String.format("%.2f", finishTime) + "s", skin);
        statusLabel = new Label("Well played, " + playerName + "!", skin);

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
                gsm.set(new MenuState(gsm));
            }
        });
    }
}
