package com.assignments.mtnpen.controller.settings;

import com.assignments.mtnpen.model.settings.SettingsModel;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;

public class SettingsController {
    private final SettingsModel model;
    private final GameStateManager gsm;

    public SettingsController(SettingsModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void onVolumeUp() {
        model.setVolume(Math.min(100, model.getVolume() + 10));
    }

    public void onVolumeDown() {
        model.setVolume(Math.max(0, model.getVolume() - 10));
    }

    public void onBackClicked() {
        gsm.set(new MenuState(gsm));
    }
}
