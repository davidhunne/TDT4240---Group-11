package com.assignments.mtnpen.view.assetmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public final class GameAssetManager {
    private static final AssetManager manager = new AssetManager();
    private static boolean loaded = false;
    private GameAssetManager() {
    }

    public static void loadAssets() {
        // Preload assets if necessary
        //manager.load(AssetPaths.UI_SKIN, Skin.class);
        manager.load(AssetPaths.PENGUIN1, Texture.class);
        for (String skin : AssetPaths.PLAYER_SKINS) {
            manager.load(skin, Texture.class);
        }
        manager.finishLoading();

        for (String skin : AssetPaths.PLAYER_SKINS) {
            Texture t = manager.get(skin, Texture.class);
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        loaded = true;
    }

    public static Skin getUiSkin() {
        FileHandle skinFile = Gdx.files.internal(AssetPaths.UI_SKIN);
        if (skinFile.exists()) {
            return new Skin(skinFile);
        }
        return createDefaultUiSkin();
    }

    public static Texture getPenguin1() {
        return manager.get(AssetPaths.PENGUIN1, Texture.class);
    }

    public static Texture getPlayerSkin(int index) {
        if (AssetPaths.PLAYER_SKINS.length == 0) {
            return getPenguin1();
        }
        int safeIndex = Math.floorMod(index, AssetPaths.PLAYER_SKINS.length);
        return manager.get(AssetPaths.PLAYER_SKINS[safeIndex], Texture.class);
    }

    private static Skin createDefaultUiSkin() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        Drawable buttonUp = skin.newDrawable("white", new Color(0.18f, 0.20f, 0.24f, 1f));
        Drawable buttonDown = skin.newDrawable("white", new Color(0.10f, 0.12f, 0.16f, 1f));
        Drawable fieldBackground = skin.newDrawable("white", new Color(0.08f, 0.09f, 0.12f, 1f));
        Drawable cursor = skin.newDrawable("white", Color.WHITE);
        Drawable selection = skin.newDrawable("white", new Color(0.25f, 0.45f, 0.75f, 0.75f));

        skin.add("default", new Label.LabelStyle(font, Color.WHITE));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonUp;
        buttonStyle.down = buttonDown;
        buttonStyle.checked = buttonDown;
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
        textFieldStyle.cursor = cursor;
        textFieldStyle.selection = selection;
        textFieldStyle.background = fieldBackground;
        skin.add("default", textFieldStyle);

        return skin;
    }
}
