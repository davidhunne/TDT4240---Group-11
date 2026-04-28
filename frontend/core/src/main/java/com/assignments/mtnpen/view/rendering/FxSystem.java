package com.assignments.mtnpen.view.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FxSystem {
    private static final int PARTICLES_PER_HIT = 14;
    private static final int PARTICLES_PER_BOOST = 18;
    private static final int PARTICLES_PER_LAND = 8;

    private final List<Particle> particles = new ArrayList<>();
    private final List<Floater> floaters = new ArrayList<>();
    private final BitmapFont floatFont;

    public FxSystem() {
        this.floatFont = new BitmapFont();
        this.floatFont.getData().setScale(0.25f);
    }

    public void spawnHit(float x, float y) {
        for (int i = 0; i < PARTICLES_PER_HIT; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(20f, 55f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = 0.55f;
            p.maxLife = 0.55f;
            p.size = MathUtils.random(1.2f, 2.4f);
            p.color = new Color(0.55f, 0.52f, 0.48f, 1f);
            p.gravity = -40f;
            particles.add(p);
        }
        floaters.add(new Floater(x, y, "OUCH!", new Color(1f, 0.55f, 0.45f, 1f), 0.85f));
    }

    public void spawnBoost(float x, float y, int amount) {
        for (int i = 0; i < PARTICLES_PER_BOOST; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(30f, 70f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = 0.7f;
            p.maxLife = 0.7f;
            p.size = MathUtils.random(1.2f, 2.6f);
            p.color = new Color(0.55f, 0.85f, 1f, 1f);
            p.gravity = 0f;
            particles.add(p);
        }
        for (int i = 0; i < 6; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(10f, 30f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = 1.0f;
            p.maxLife = 1.0f;
            p.size = MathUtils.random(1.6f, 2.8f);
            p.color = new Color(1f, 0.95f, 0.55f, 1f);
            p.gravity = 0f;
            particles.add(p);
        }
        String label = "+" + amount;
        floaters.add(new Floater(x, y, label, new Color(1f, 0.92f, 0.35f, 1f), 1.2f));
    }

    public void spawnLand(float x, float y) {
        for (int i = 0; i < PARTICLES_PER_LAND; i++) {
            float angle = MathUtils.random(MathUtils.PI, MathUtils.PI2);
            float speed = MathUtils.random(15f, 35f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed * 0.4f;
            p.life = 0.5f;
            p.maxLife = 0.5f;
            p.size = MathUtils.random(1.0f, 1.8f);
            p.color = new Color(0.92f, 0.97f, 1f, 1f);
            p.gravity = -25f;
            particles.add(p);
        }
    }

    public void spawnFinish(float x, float y) {
        for (int i = 0; i < 36; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(40f, 110f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = 1.4f;
            p.maxLife = 1.4f;
            p.size = MathUtils.random(1.5f, 3.2f);
            float[] palette = colorChoice(i);
            p.color = new Color(palette[0], palette[1], palette[2], 1f);
            p.gravity = -30f;
            particles.add(p);
        }
        floaters.add(new Floater(x, y, "FINISH!", new Color(1f, 0.92f, 0.35f, 1f), 1.6f));
    }

    private float[] colorChoice(int i) {
        switch (i % 4) {
            case 0: return new float[] {1f, 0.85f, 0.25f};
            case 1: return new float[] {0.4f, 0.8f, 1f};
            case 2: return new float[] {1f, 0.55f, 0.55f};
            default: return new float[] {0.95f, 0.95f, 0.95f};
        }
    }

    public void update(float delta) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.life -= delta;
            if (p.life <= 0f) {
                it.remove();
                continue;
            }
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.vy += p.gravity * delta;
            p.vx *= (1f - 1.5f * delta);
        }
        Iterator<Floater> fi = floaters.iterator();
        while (fi.hasNext()) {
            Floater f = fi.next();
            f.life -= delta;
            if (f.life <= 0f) {
                fi.remove();
                continue;
            }
            f.y += 16f * delta;
        }
    }

    public void renderParticles(ShapeRenderer shapes) {
        if (particles.isEmpty()) return;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : particles) {
            float a = Math.max(0f, p.life / p.maxLife);
            shapes.setColor(p.color.r, p.color.g, p.color.b, a);
            shapes.circle(p.x, p.y, p.size);
        }
        shapes.end();
    }

    public void renderFloaters(SpriteBatch batch, OrthographicCamera camera) {
        if (floaters.isEmpty()) return;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Floater f : floaters) {
            float a = Math.max(0f, f.life / f.maxLife);
            floatFont.setColor(f.color.r, f.color.g, f.color.b, a);
            floatFont.draw(batch, f.text, f.x - 4f * f.text.length() * 0.3f, f.y);
        }
        batch.end();
    }

    public void dispose() {
        floatFont.dispose();
    }

    private static class Particle {
        float x, y;
        float vx, vy;
        float life, maxLife;
        float size;
        float gravity;
        Color color;
    }

    private static class Floater {
        float x, y;
        String text;
        Color color;
        float life, maxLife;

        Floater(float x, float y, String text, Color color, float life) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            this.life = life;
            this.maxLife = life;
        }
    }
}
