package com.assignments.mtnpen.controller.results;

import com.assignments.mtnpen.model.results.ResultsModel;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.assignments.mtnpen.view.states.menu.MenuState;

public class ResultsController {
    private final ResultsModel model;
    private final GameStateManager gsm;

    public ResultsController(ResultsModel model, GameStateManager gsm) {
        this.model = model;
        this.gsm = gsm;
    }

    public void onBackToMenuClicked() {
        gsm.set(new MenuState(gsm));
    }
}
