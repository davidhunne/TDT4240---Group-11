package com.assignments.mtnpen.view.ui;

import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;

public class FrostBackdrop extends Actor {
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final Snowflake[] flakes;
    private final BackgroundPenguin[] penguins;
    private float time = 0f;

    public FrostBackdrop() {
        this(70, 4);
    }

    public FrostBackdrop(int snowCount, int penguinCount) {
        setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        flakes = new Snowflake[snowCount];
        for (int i = 0; i < flakes.length; i++)
            flakes[i] = new Snowflake();
        penguins = new BackgroundPenguin[penguinCount];
        for (int i = 0; i < penguins.length; i++) {
            penguins[i] = new BackgroundPenguin(i, penguins.length);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapes.setProjectionMatrix(batch.getProjectionMatrix());
        shapes.setTransformMatrix(batch.getTransformMatrix());

        shapes.begin(ShapeRenderer.ShapeType.Filled);

        Color skyTop = new Color(0.04f, 0.07f, 0.18f, 1f);
        Color skyMid = new Color(0.10f, 0.20f, 0.42f, 1f);
        Color skyHorizon = new Color(0.28f, 0.52f, 0.82f, 1f);
        shapes.rect(0, h * 0.45f, w, h * 0.55f,
                skyMid, skyMid, skyTop, skyTop);
        shapes.rect(0, h * 0.20f, w, h * 0.25f,
                skyHorizon, skyHorizon, skyMid, skyMid);

        float auroraY = h * 0.72f;
        for (int i = 0; i < 80; i++) {
            float t = i / 80f;
            float x = t * w;
            float wave = MathUtils.sin(t * 6f + time * 0.6f) * 18f
                    + MathUtils.sin(t * 13f + time * 0.9f) * 10f;
            float thickness = 24f + MathUtils.sin(t * 4f + time * 0.5f) * 6f;
            float alpha = 0.08f + 0.06f * MathUtils.sin(t * 5f + time * 0.7f);
            shapes.setColor(0.40f, 1f, 0.78f, alpha);
            shapes.rect(x, auroraY + wave, w / 80f + 1f, thickness);
            shapes.setColor(0.55f, 0.75f, 1f, alpha * 0.9f);
            shapes.rect(x, auroraY + wave + thickness * 0.6f, w / 80f + 1f, thickness * 0.5f);
        }

        drawMountainRange(w, h, h * 0.30f, h * 0.18f, 7f, 0.5f,
                new Color(0.18f, 0.28f, 0.50f, 1f));
        drawMountainRange(w, h, h * 0.24f, h * 0.22f, 5f, 0.85f,
                new Color(0.12f, 0.20f, 0.40f, 1f));
        drawFrontMountains(w, h, h * 0.18f, h * 0.30f, 4f);

        shapes.setColor(0.94f, 0.97f, 1f, 1f);
        shapes.rect(0, 0, w, h * 0.18f);
        shapes.setColor(0.78f, 0.88f, 0.98f, 1f);
        shapes.rect(0, 0, w, h * 0.05f);

        shapes.end();

        batch.begin();
        for (BackgroundPenguin p : penguins) {
            p.update(time, w, h);
            Texture tex = GameAssetManager.getPlayerSkin(p.skinIndex);
            if (tex == null)
                continue;
            float size = h * 0.12f * p.scale;
            batch.setColor(1f, 1f, 1f, 0.95f);
            batch.draw(tex, p.x - size / 2f, p.y, size, size);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawMountainRange(float w, float h, float baseY, float maxHeight,
            float peakStep, float roughness, Color color) {
        shapes.setColor(color);
        int peaks = Math.max(3, (int) (w / (peakStep * 12f)));
        for (int i = 0; i < peaks; i++) {
            float cx = (i / (float) peaks) * w + (w / peaks) * 0.5f;
            float halfW = (w / peaks) * 0.7f;
            float pkH = maxHeight * (0.5f + roughness * (((i * 17) % 9) / 9f));
            shapes.triangle(
                    cx - halfW, baseY,
                    cx + halfW, baseY,
                    cx + (((i * 23) % 7) - 3) * 4f, baseY + pkH);
        }
    }

    private void drawFrontMountains(float w, float h, float baseY, float maxHeight,
            float peakStep) {
        Color body = new Color(0.06f, 0.12f, 0.26f, 1f);
        Color shadow = new Color(0.03f, 0.07f, 0.16f, 1f);
        Color snow = new Color(0.97f, 0.99f, 1f, 1f);

        int peaks = 5;
        for (int i = 0; i < peaks; i++) {
            float cx = (i / (float) peaks) * w + (w / peaks) * 0.5f;
            float halfW = (w / peaks) * 0.85f;
            float pkH = maxHeight * (0.7f + (((i * 11) % 5) / 5f) * 0.4f);
            float tipX = cx + (((i * 13) % 7) - 3) * 6f;
            float tipY = baseY + pkH;

            shapes.setColor(body);
            shapes.triangle(cx - halfW, baseY, cx + halfW, baseY, tipX, tipY);

            shapes.setColor(shadow);
            shapes.triangle(cx - halfW, baseY, cx, baseY, tipX, tipY);

            // Snow cap
            float capH = pkH * 0.32f;
            float capBaseLeft = tipX - halfW * 0.30f;
            float capBaseRight = tipX + halfW * 0.30f;
            float capBaseY = tipY - capH;
            shapes.setColor(snow);
            shapes.triangle(capBaseLeft, capBaseY, capBaseRight, capBaseY, tipX, tipY);
            // Snow drip
            shapes.triangle(capBaseLeft + halfW * 0.10f, capBaseY + capH * 0.4f,
                    capBaseLeft + halfW * 0.04f, capBaseY,
                    capBaseLeft + halfW * 0.18f, capBaseY);
        }
    }

    private static class Snowflake {
        float x, y, r, alpha;
        float speed, drift;
        float seed;

        Snowflake() {
            seed = MathUtils.random(0f, 1000f);
            r = MathUtils.random(1.2f, 3.6f);
            speed = MathUtils.random(20f, 60f);
            drift = MathUtils.random(-10f, 10f);
            alpha = MathUtils.random(0.45f, 0.95f);
            x = MathUtils.random(0f, 2000f);
            y = MathUtils.random(0f, 2000f);
        }

    }

    private static class BackgroundPenguin {
        final int skinIndex;
        final float baseX;
        final float scale;
        final float speed;
        float x, y;

        BackgroundPenguin(int idx, int total) {
            this.skinIndex = idx % 4;
            this.baseX = (idx + 0.5f) / total;
            this.scale = MathUtils.random(0.85f, 1.15f);
            this.speed = MathUtils.random(0.1f, 0.25f) * (idx % 2 == 0 ? 1f : -1f);
        }

        void update(float t, float w, float h) {
            float u = (baseX + t * speed) % 1f;
            if (u < 0)
                u += 1f;
            x = u * w;
            y = h * 0.04f + Math.abs(MathUtils.sin(t * 4f + skinIndex)) * 3f;
        }
    }

    public void dispose() {
        shapes.dispose();
    }
}
