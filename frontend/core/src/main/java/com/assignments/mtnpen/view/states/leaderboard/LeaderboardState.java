package com.assignments.mtnpen.view.states.leaderboard;

import com.assignments.mtnpen.network.NetworkManager;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;
import com.assignments.mtnpen.view.ui.FrostBackdrop;
import com.assignments.mtnpen.view.ui.UiTheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardState extends BaseState {

    private static final int LIMIT = 25;

    private Skin skin;
    private FrostBackdrop backdrop;

    private Table contentSlot;
    private Label statusLabel;
    private TextButton refreshButton;
    private TextButton backButton;

    private final List<Entry> entries = new ArrayList<>();
    private boolean loading = false;

    public LeaderboardState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void create() {
        super.create();
        skin = UiTheme.build();
        backdrop = new FrostBackdrop(0, 4);
        backdrop.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        stage.addActor(backdrop);
        buildShell();
        fetch();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            backToMenu();
        }
    }

    @Override
    public void leave() {
        if (backdrop != null) backdrop.dispose();
        super.leave();
        if (skin != null) skin.dispose();
    }

    private void buildShell() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label title = new Label("LEADERBOARD", skin, "title");
        Label tagline = new Label("Best penguins on the mountain", skin, "subtitle");

        contentSlot = new Table();
        statusLabel = new Label("Loading...", skin, "muted");
        contentSlot.add(statusLabel).pad(40f);

        Container<Table> contentWrap = new Container<>(contentSlot);
        contentWrap.width(640f).minHeight(360f);

        refreshButton = new TextButton("Refresh", skin, "ghost");
        backButton = new TextButton("BACK", skin, "gold");

        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fetch();
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMenu();
            }
        });

        Table footer = new Table();
        footer.defaults().pad(6f);
        footer.add(refreshButton).width(140f).height(44f);
        footer.add(backButton).width(220f).height(54f);

        root.add(title).padBottom(2f).row();
        root.add(tagline).padBottom(20f).row();
        root.add(contentWrap).padBottom(14f).row();
        root.add(footer).padTop(4f).row();

        stage.addActor(root);

        title.getColor().a = 0f;
        title.addAction(Actions.fadeIn(0.5f));
    }

    private void fetch() {
        if (loading) return;
        loading = true;
        renderLoading();

        gsm.getNetworkManager().getLeaderboard(LIMIT, new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.postRunnable(() -> {
                    loading = false;
                    parse(response);
                    renderLeaderboard();
                });
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> {
                    loading = false;
                    renderError(t.getMessage());
                });
            }
        });
    }

    private void parse(String response) {
        entries.clear();
        try {
            JsonValue root = gsm.getNetworkManager().parse(response);
            if (root == null) return;
            for (JsonValue p = root.child; p != null; p = p.next) {
                Entry e = new Entry();
                e.playerId = p.getString("playerId", "");
                e.displayName = p.getString("displayName", "Penguin");
                e.highScore = p.getInt("highScore", 0);
                e.gamesWon = p.getInt("gamesWon", 0);
                e.gamesPlayed = p.getInt("gamesPlayed", 0);
                entries.add(e);
            }
        } catch (Exception ex) {
            Gdx.app.log("LeaderboardState", "parse failed: " + ex.getMessage());
        }
    }

    private void renderLoading() {
        contentSlot.clear();
        statusLabel = new Label("Fetching champions...", skin, "muted");
        contentSlot.add(statusLabel).pad(40f);
    }

    private void renderError(String message) {
        contentSlot.clear();
        Table errCard = new Table();
        errCard.setBackground(skin.getDrawable("card"));
        errCard.pad(28f, 36f, 28f, 36f);
        errCard.add(new Label("Couldn't fetch leaderboard", skin, "hero")).padBottom(6f).row();
        Label detail = new Label(message == null ? "Unknown error" : message, skin, "error");
        detail.setWrap(true);
        detail.setAlignment(Align.center);
        errCard.add(detail).width(420f).row();
        contentSlot.add(errCard);
    }

    private void renderLeaderboard() {
        contentSlot.clear();

        if (entries.isEmpty()) {
            Table empty = new Table();
            empty.setBackground(skin.getDrawable("card"));
            empty.pad(40f, 60f, 40f, 60f);
            empty.add(new Label("No races completed yet.", skin, "hero")).padBottom(6f).row();
            empty.add(new Label("Be the first to reach the summit!", skin, "muted")).row();
            contentSlot.add(empty);
            return;
        }

        Table column = new Table();
        column.top();

        if (entries.size() >= 3) {
            column.add(buildPodium()).padBottom(16f).row();
        }

        Table listCard = new Table();
        listCard.setBackground(skin.getDrawable("card"));
        listCard.pad(20f, 28f, 20f, 28f);

        Table header = new Table();
        header.add(new Label("RANK", skin, "muted")).width(60f).left();
        header.add(new Label("PENGUIN", skin, "muted")).expandX().left().padLeft(8f);
        header.add(new Label("HIGH", skin, "muted")).width(80f).right();
        header.add(new Label("WINS", skin, "muted")).width(60f).right();
        header.add(new Label("RACES", skin, "muted")).width(70f).right();
        listCard.add(header).fillX().padBottom(8f).row();

        Table rowsTable = new Table();
        rowsTable.top();
        for (int i = 0; i < entries.size(); i++) {
            rowsTable.add(buildRow(i + 1, entries.get(i))).fillX().width(560f).padBottom(6f).row();
        }
        ScrollPane scroll = new ScrollPane(rowsTable);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        listCard.add(scroll).width(580f).height(280f).row();

        column.add(listCard).row();

        contentSlot.add(column);

        contentSlot.getColor().a = 0f;
        contentSlot.addAction(Actions.fadeIn(0.35f));
    }

    private Table buildPodium() {
        Table podium = new Table();
        podium.defaults().pad(0f, 8f, 0f, 8f);
        podium.add(buildPodiumColumn(1, entries.size() > 1 ? entries.get(1) : null,
                160f, "#7E91A8")).bottom();
        podium.add(buildPodiumColumn(0, entries.get(0),
                200f, "#FFC72E")).bottom();
        podium.add(buildPodiumColumn(2, entries.size() > 2 ? entries.get(2) : null,
                140f, "#B57A3D")).bottom();
        return podium;
    }

    private Table buildPodiumColumn(int idx, Entry e, float blockHeight, String accent) {
        Table col = new Table();
        col.bottom();

        if (e == null) return col;

        Texture skinTex = GameAssetManager.getPlayerSkin(idx);
        if (skinTex != null) {
            Image avatar = new Image(new TextureRegionDrawable(skinTex));
            avatar.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            col.add(avatar).size(74f).padBottom(4f).row();
        }

        col.add(new Label(e.displayName, skin, idx == 0 ? "hero" : "default"))
                .padBottom(2f).row();

        Label score = new Label(String.valueOf(e.highScore), skin, idx == 0 ? "hero" : "subtitle");
        col.add(score).padBottom(8f).row();

        Table block = new Table();
        block.setBackground(skin.getDrawable(idx == 0 ? "card-light" : "chip"));
        Label rankNum = new Label("#" + (idx + 1), skin, idx == 0 ? "title" : "hero");
        rankNum.setAlignment(Align.center);
        block.add(rankNum).expand().fill();
        col.add(block).width(100f).height(blockHeight).row();

        return col;
    }

    private Table buildRow(int rank, Entry e) {
        Table row = new Table();
        row.setBackground(skin.getDrawable("card-light"));
        row.pad(8f, 12f, 8f, 12f);

        Label rankLabel = new Label("#" + rank, skin, rank <= 3 ? "hero" : "status");
        row.add(rankLabel).width(60f).left();

        Texture skinTex = GameAssetManager.getPlayerSkin(rank - 1);
        if (skinTex != null) {
            Image avatar = new Image(new TextureRegionDrawable(skinTex));
            avatar.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            row.add(avatar).size(36f).padRight(10f);
        }

        Label name = new Label(e.displayName, skin, rank <= 3 ? "default" : "default");
        row.add(name).expandX().left();

        row.add(new Label(String.valueOf(e.highScore), skin, "subtitle")).width(80f).right();
        row.add(new Label(String.valueOf(e.gamesWon), skin, "subtitle")).width(60f).right();
        row.add(new Label(String.valueOf(e.gamesPlayed), skin, "muted")).width(70f).right();

        return row;
    }

    private void backToMenu() {
        gsm.set(new MenuState(gsm));
    }

    private static class Entry {
        String playerId;
        String displayName;
        int highScore;
        int gamesWon;
        int gamesPlayed;
    }
}
