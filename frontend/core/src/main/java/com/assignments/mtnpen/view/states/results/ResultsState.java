package com.assignments.mtnpen.view.states.results;

import com.assignments.mtnpen.controller.results.ResultsController;
import com.assignments.mtnpen.model.results.ResultsModel;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultsState extends BaseState {

    private final ResultsModel model;
    private final ResultsController controller;
    private final List<PlayerResult> results;
    private final String localPlayerId;
    private final String localPlayerName;
    private final float finishTime;

    private Skin skin;
    private FrostBackdrop backdrop;

    public ResultsState(GameStateManager gsm, String playerName, int score, float finishTime) {
        this(gsm, null, playerName,
                new ArrayList<>(Collections.singletonList(
                        new PlayerResult(null, playerName, score, 0, true))),
                finishTime);
    }

    public ResultsState(GameStateManager gsm,
                        String localPlayerId,
                        String localPlayerName,
                        List<PlayerResult> results,
                        float finishTime) {
        super(gsm);
        this.localPlayerId = localPlayerId;
        this.localPlayerName = localPlayerName;
        this.results = results == null ? new ArrayList<>() : results;
        this.finishTime = finishTime;
        this.model = new ResultsModel();
        this.controller = new ResultsController(model, gsm);
    }

    @Override
    public void create() {
        super.create();
        skin = UiTheme.build();
        backdrop = new FrostBackdrop(0, 4);
        backdrop.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        stage.addActor(backdrop);
        buildUi();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.onBackToMenuClicked();
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

        List<PlayerResult> sorted = new ArrayList<>(results);
        sorted.sort(Comparator
                .comparing((PlayerResult p) -> !p.won)
                .thenComparing(p -> -p.score));

        PlayerResult winner = sorted.isEmpty() ? null : sorted.get(0);
        boolean localWon = winner != null && localPlayerId != null
                && localPlayerId.equals(winner.playerId);

        String headline = localWon ? "VICTORY!"
                : (winner == null ? "RACE OVER" : "RACE OVER");
        Label title = new Label(headline, skin, "title");
        Label subtitle;
        if (winner == null) {
            subtitle = new Label("Thanks for racing.", skin, "subtitle");
        } else if (localWon) {
            subtitle = new Label("You reached the summit first!", skin, "subtitle");
        } else {
            subtitle = new Label(winner.displayName + " reached the summit first.", skin, "subtitle");
        }

        Table winnerCard = buildWinnerCard(winner);

        Table leaderboardCard = new Table();
        leaderboardCard.setBackground(skin.getDrawable("card"));
        leaderboardCard.pad(20f, 28f, 20f, 28f);
        leaderboardCard.add(new Label("LEADERBOARD", skin, "muted")).left().padBottom(10f).row();
        for (int i = 0; i < sorted.size(); i++) {
            leaderboardCard.add(buildLeaderRow(i + 1, sorted.get(i))).fillX().width(440f).row();
        }
        if (sorted.isEmpty()) {
            leaderboardCard.add(new Label("No racers tracked.", skin, "muted")).pad(10f).row();
        }

        // Time stat
        Table statRow = new Table();
        statRow.defaults().pad(0f, 16f, 0f, 16f);
        statRow.add(buildStat("TIME", formatTime(finishTime))).top();
        statRow.add(buildStat("RACERS", String.valueOf(results.size()))).top();
        if (winner != null) {
            statRow.add(buildStat("TOP SCORE", String.valueOf(winner.score))).top();
        }

        TextButton menuButton = new TextButton("BACK TO MENU", skin, "gold");
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onBackToMenuClicked();
            }
        });

        root.add(title).padBottom(2f).row();
        root.add(subtitle).padBottom(18f).row();
        root.add(winnerCard).padBottom(14f).row();
        root.add(statRow).padBottom(14f).row();
        root.add(leaderboardCard).row();
        root.add(menuButton).width(320f).height(58f).padTop(20f).row();

        stage.addActor(root);

        title.getColor().a = 0f;
        title.addAction(Actions.fadeIn(0.5f));
        winnerCard.getColor().a = 0f;
        winnerCard.addAction(Actions.sequence(Actions.delay(0.15f), Actions.fadeIn(0.45f)));
        leaderboardCard.getColor().a = 0f;
        leaderboardCard.addAction(Actions.sequence(Actions.delay(0.30f), Actions.fadeIn(0.45f)));

        if (localWon) {
            title.addAction(Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.04f, 1.04f, 0.5f),
                    Actions.scaleTo(1f, 1f, 0.5f))));
        }
    }

    private Table buildWinnerCard(PlayerResult winner) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("card"));
        card.pad(28f, 36f, 28f, 36f);

        if (winner == null) {
            card.add(new Label("No winner recorded.", skin, "muted")).pad(20f);
            return card;
        }

        Texture skinTex = GameAssetManager.getPlayerSkin(winner.skinIndex);
        if (skinTex != null) {
            Image avatar = new Image(new TextureRegionDrawable(skinTex));
            avatar.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            card.add(avatar).size(140f).padRight(28f);
        }

        Table info = new Table();
        info.defaults().left();
        info.add(new Label("CHAMPION", skin, "muted")).padBottom(2f).row();
        info.add(new Label(winner.displayName, skin, "title")).padBottom(8f).row();
        Table scoreRow = new Table();
        scoreRow.add(new Label("SCORE", skin, "muted")).padRight(8f);
        scoreRow.add(new Label(String.valueOf(winner.score), skin, "hero"));
        info.add(scoreRow).left();

        card.add(info).left();
        return card;
    }

    private Table buildLeaderRow(int rank, PlayerResult p) {
        Table row = new Table();
        row.setBackground(skin.getDrawable("card-light"));
        row.pad(8f, 12f, 8f, 12f);

        Label rankLabel = new Label("#" + rank, skin, p.won ? "hero" : "status");
        row.add(rankLabel).width(54f).left();

        Texture skinTex = GameAssetManager.getPlayerSkin(p.skinIndex);
        if (skinTex != null) {
            Image avatar = new Image(new TextureRegionDrawable(skinTex));
            avatar.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            row.add(avatar).size(44f).padRight(12f);
        }

        boolean isLocal = localPlayerId != null && localPlayerId.equals(p.playerId);
        String displayName = p.displayName == null ? "Player" : p.displayName;
        Label name = new Label(displayName + (isLocal ? "  (you)" : ""),
                skin, isLocal ? "hero" : "default");
        row.add(name).left().expandX().fillX();

        Label score = new Label(String.valueOf(p.score), skin, "subtitle");
        score.setAlignment(Align.right);
        row.add(score).right().padLeft(8f);

        if (p.won) {
            row.row();
            Label crown = new Label("WINNER", skin, "status");
            row.add(crown).colspan(4).right().padTop(4f);
        }

        return row;
    }

    private Table buildStat(String label, String value) {
        Table stat = new Table();
        stat.setBackground(skin.getDrawable("chip"));
        stat.pad(10f, 18f, 10f, 18f);
        stat.add(new Label(label, skin, "muted")).padBottom(2f).row();
        stat.add(new Label(value, skin, "hero")).row();
        return stat;
    }

    private String formatTime(float seconds) {
        int s = Math.max(0, Math.round(seconds));
        int mins = s / 60;
        int secs = s % 60;
        return String.format("%d:%02d", mins, secs);
    }

    public static class PlayerResult {
        public final String playerId;
        public final String displayName;
        public final int score;
        public final int skinIndex;
        public final boolean won;

        public PlayerResult(String playerId, String displayName, int score, int skinIndex, boolean won) {
            this.playerId = playerId;
            this.displayName = displayName;
            this.score = score;
            this.skinIndex = skinIndex;
            this.won = won;
        }
    }
}
