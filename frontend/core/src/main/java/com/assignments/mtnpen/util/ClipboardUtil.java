package com.assignments.mtnpen.util;

import com.badlogic.gdx.Gdx;

public class ClipboardUtil {
    public static void copyToClipboard(String text) {
        Gdx.app.getClipboard().setContents(text);
    }

    public static String getFromClipboard() {
        return Gdx.app.getClipboard().getContents();
    }
}
