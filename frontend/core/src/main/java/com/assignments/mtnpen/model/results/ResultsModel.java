package com.assignments.mtnpen.model.results;

import java.util.ArrayList;
import java.util.List;

public class ResultsModel {
    private List<PlayerResult> results = new ArrayList<>();

    public List<PlayerResult> getResults() {
        return results;
    }

    public void setResults(List<PlayerResult> results) {
        this.results = results;
    }

    public static class PlayerResult {
        public String name;
        public int score;

        public PlayerResult(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
