package com.assignments.mtnpen.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public final class UiTheme {
        public static final Color ICE_DEEP = new Color(0.06f, 0.12f, 0.24f, 1f);
        public static final Color ICE_DARK = new Color(0.10f, 0.18f, 0.34f, 1f);
        public static final Color ICE_MID = new Color(0.20f, 0.42f, 0.78f, 1f);
        public static final Color ICE_LIGHT = new Color(0.55f, 0.78f, 0.98f, 1f);
        public static final Color SNOW = new Color(0.96f, 0.99f, 1f, 1f);
        public static final Color GOLD = new Color(1f, 0.84f, 0.27f, 1f);
        public static final Color GOLD_DEEP = new Color(0.92f, 0.65f, 0.10f, 1f);
        public static final Color CORAL = new Color(1f, 0.42f, 0.39f, 1f);

        private UiTheme() {
        }

        public static Skin build() {
                Skin skin = new Skin();

                BitmapFont base = new BitmapFont();
                base.getData().setScale(1.5f);
                BitmapFont label = new BitmapFont();
                label.getData().setScale(1.3f);
                BitmapFont small = new BitmapFont();
                small.getData().setScale(1.2f);
                BitmapFont title = new BitmapFont();
                title.getData().setScale(2.4f);
                BitmapFont hero = new BitmapFont();
                hero.getData().setScale(1.6f);
                BitmapFont button = new BitmapFont();
                button.getData().setScale(1.6f);

                skin.add("default-font", base);
                skin.add("title-font", title);
                skin.add("hero-font", hero);
                skin.add("button-font", button);
                skin.add("small-font", small);

                skin.add("white", makeFlatTexture(SNOW));
                skin.add("ice-deep", makeFlatTexture(ICE_DEEP));

                Drawable cardBg = roundedNinePatch(skin, "card",
                                new Color(0.10f, 0.18f, 0.34f, 0.92f),
                                new Color(0.18f, 0.30f, 0.55f, 1f), 24, 4);
                Drawable cardBgLight = roundedNinePatch(skin, "card-light",
                                new Color(0.18f, 0.30f, 0.52f, 1f), null, 10, 0);
                Drawable chipBg = roundedNinePatch(skin, "chip",
                                new Color(0.07f, 0.13f, 0.24f, 1f), null, 10, 0);
                Color inputFill = new Color(0.07f, 0.13f, 0.24f, 1f);
                Drawable inputBg = roundedNinePatch(skin, "input", inputFill, null, 8, 0);
                Drawable inputBgFocus = inputBg;

                Drawable primaryUp = roundedNinePatch(skin, "btn-primary-up",
                                new Color(0.20f, 0.50f, 0.95f, 1f), null, 22, 0);
                Drawable primaryDown = roundedNinePatch(skin, "btn-primary-down",
                                new Color(0.10f, 0.32f, 0.72f, 1f), null, 22, 0);
                Drawable primaryOver = roundedNinePatch(skin, "btn-primary-over",
                                new Color(0.28f, 0.58f, 1f, 1f), null, 22, 0);

                Drawable goldUp = roundedNinePatch(skin, "btn-gold-up",
                                new Color(1f, 0.78f, 0.18f, 1f), null, 22, 0);
                Drawable goldDown = roundedNinePatch(skin, "btn-gold-down",
                                new Color(0.78f, 0.55f, 0.08f, 1f), null, 22, 0);
                Drawable goldOver = roundedNinePatch(skin, "btn-gold-over",
                                new Color(1f, 0.86f, 0.30f, 1f), null, 22, 0);
                Drawable goldDisabled = roundedNinePatch(skin, "btn-gold-disabled",
                                new Color(0.58f, 0.62f, 0.68f, 1f), null, 22, 0);

                Drawable ghostUp = roundedNinePatch(skin, "btn-ghost-up",
                                new Color(0.22f, 0.34f, 0.55f, 1f), null, 18, 0);
                Drawable ghostDown = roundedNinePatch(skin, "btn-ghost-down",
                                new Color(0.14f, 0.24f, 0.42f, 1f), null, 18, 0);

                Drawable dangerUp = roundedNinePatch(skin, "btn-danger-up",
                                new Color(0.85f, 0.26f, 0.30f, 1f), null, 18, 0);
                Drawable dangerDown = roundedNinePatch(skin, "btn-danger-down",
                                new Color(0.62f, 0.14f, 0.20f, 1f), null, 18, 0);

                Drawable cursor = skin.newDrawable("white", GOLD);
                Drawable selection = skin.newDrawable("white", new Color(0.30f, 0.55f, 0.95f, 0.45f));

                skin.add("default", new Label.LabelStyle(base, SNOW));
                skin.add("title", new Label.LabelStyle(title, SNOW));
                skin.add("hero", new Label.LabelStyle(hero, GOLD));
                skin.add("subtitle", new Label.LabelStyle(label, ICE_LIGHT));
                skin.add("muted", new Label.LabelStyle(small, new Color(0.7f, 0.82f, 0.96f, 1f)));
                skin.add("status", new Label.LabelStyle(small, new Color(1f, 0.92f, 0.55f, 1f)));
                skin.add("error", new Label.LabelStyle(small, CORAL));
                skin.add("code", new Label.LabelStyle(hero, SNOW));

                // skin.getDrawable() searches for Drawable.class first. Register them as such.
                skin.add("card", cardBg, Drawable.class);
                skin.add("card-light", cardBgLight, Drawable.class);
                skin.add("chip", chipBg, Drawable.class);

                TextButton.TextButtonStyle primary = new TextButton.TextButtonStyle();
                primary.up = primaryUp;
                primary.down = primaryDown;
                primary.over = primaryOver;
                primary.checked = primaryDown;
                primary.font = button;
                primary.fontColor = SNOW;
                primary.downFontColor = SNOW;
                primary.overFontColor = SNOW;
                skin.add("default", primary);
                skin.add("primary", primary);

                TextButton.TextButtonStyle gold = new TextButton.TextButtonStyle();
                gold.up = goldUp;
                gold.down = goldDown;
                gold.over = goldOver;
                gold.checked = goldDown;
                gold.disabled = goldDisabled;
                gold.font = button;
                gold.fontColor = ICE_DEEP;
                gold.downFontColor = ICE_DEEP;
                gold.overFontColor = ICE_DEEP;
                gold.disabledFontColor = new Color(0.20f, 0.26f, 0.34f, 1f);
                skin.add("gold", gold);

                TextButton.TextButtonStyle ghost = new TextButton.TextButtonStyle();
                ghost.up = ghostUp;
                ghost.down = ghostDown;
                ghost.over = ghostDown;
                ghost.checked = ghostDown;
                ghost.font = base;
                ghost.fontColor = SNOW;
                ghost.downFontColor = GOLD;
                ghost.checkedFontColor = GOLD;
                skin.add("ghost", ghost);

                TextButton.TextButtonStyle danger = new TextButton.TextButtonStyle();
                danger.up = dangerUp;
                danger.down = dangerDown;
                danger.over = dangerDown;
                danger.font = base;
                danger.fontColor = SNOW;
                skin.add("danger", danger);

                TextButton.TextButtonStyle tab = new TextButton.TextButtonStyle();
                tab.up = ghostUp;
                tab.down = primaryDown;
                tab.over = primaryOver;
                tab.checked = primaryUp;
                tab.font = button;
                tab.fontColor = SNOW;
                tab.downFontColor = SNOW;
                tab.overFontColor = SNOW;
                tab.checkedFontColor = SNOW;
                skin.add("tab", tab);

                TextField.TextFieldStyle field = new TextField.TextFieldStyle();
                field.font = base;
                field.fontColor = GOLD;
                field.messageFont = base;
                field.messageFontColor = new Color(0.55f, 0.7f, 0.92f, 1f);
                field.background = inputBg;
                field.focusedBackground = inputBgFocus;
                field.cursor = cursor;
                field.selection = selection;
                skin.add("default", field);

                return skin;
        }

        private static Drawable roundedNinePatch(Skin skin, String name, Color fill, Color border,
                        int radius, int borderWidth) {
                int size = Math.max(radius * 2 + 4, 64);
                Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
                pm.setBlending(Pixmap.Blending.None);
                pm.setColor(0, 0, 0, 0);
                pm.fill();

                pm.setColor(fill);
                pm.fillRectangle(radius, 0, size - radius * 2, size);
                pm.fillRectangle(0, radius, size, size - radius * 2);
                pm.fillCircle(radius, radius, radius);
                pm.fillCircle(size - radius - 1, radius, radius);
                pm.fillCircle(radius, size - radius - 1, radius);
                pm.fillCircle(size - radius - 1, size - radius - 1, radius);

                if (borderWidth > 0) {
                        pm.setColor(border);
                        for (int i = 0; i < borderWidth; i++) {
                                int r = radius - i;
                                if (r < 1)
                                        break;
                                drawRoundedOutline(pm, size, radius, r);
                        }
                }

                Texture tex = new Texture(pm);
                tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                skin.add("tex-" + name, tex);
                pm.dispose();

                int pad = radius;
                NinePatch np = new NinePatch(tex, pad, pad, pad, pad);
                NinePatchDrawable d = new NinePatchDrawable(np);
                d.setMinHeight(0f);
                d.setMinWidth(0f);
                skin.add(name, d, Drawable.class);
                return d;
        }

        private static void drawRoundedOutline(Pixmap pm, int size, int corner, int r) {
                pm.drawLine(corner, size - 1 - (corner - r), size - corner - 1, size - 1 - (corner - r));
                pm.drawLine(corner, (corner - r), size - corner - 1, (corner - r));
                pm.drawLine((corner - r), corner, (corner - r), size - corner - 1);
                pm.drawLine(size - 1 - (corner - r), corner, size - 1 - (corner - r), size - corner - 1);
                pm.drawCircle(corner, corner, r);
                pm.drawCircle(size - corner - 1, corner, r);
                pm.drawCircle(corner, size - corner - 1, r);
                pm.drawCircle(size - corner - 1, size - corner - 1, r);
        }

        private static Texture makeFlatTexture(Color c) {
                Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pm.setColor(c);
                pm.fill();
                Texture t = new Texture(pm);
                pm.dispose();
                return t;
        }
}
