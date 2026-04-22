package com.assignments.mtnpen.view.assetmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private GameAssetManager() {
    }

    // Placeholder
    public static Skin loadUiSkin() {
        return new Skin(Gdx.files.internal(AssetPaths.UI_SKIN));
    }

    public static Texture loadPenguin1() {
        return new Texture(Gdx.files.internal(AssetPaths.PENGUIN1));
    }
}
