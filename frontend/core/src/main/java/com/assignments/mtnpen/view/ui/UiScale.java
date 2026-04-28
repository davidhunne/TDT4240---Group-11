package com.assignments.mtnpen.view.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public final class UiScale {
    private static final float MIN_ANDROID_UI_SCALE = 1.45f;
    private static final float MAX_ANDROID_UI_SCALE = 1.85f;
    private static final float DENSITY_WEIGHT = 0.32f;

    private UiScale() {
    }

    public static float sceneUnitsPerPixel() {
        return 1f / uiScale();
    }

    public static float uiScale() {
        if (Gdx.app == null || Gdx.app.getType() != Application.ApplicationType.Android) {
            return 1f;
        }

        float density = Math.max(1f, Gdx.graphics.getDensity());
        float scaled = 1f + (density - 1f) * DENSITY_WEIGHT;
        return MathUtils.clamp(scaled, MIN_ANDROID_UI_SCALE, MAX_ANDROID_UI_SCALE);
    }
}
