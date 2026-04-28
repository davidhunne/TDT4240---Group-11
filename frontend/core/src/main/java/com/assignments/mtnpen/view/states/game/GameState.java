package com.assignments.mtnpen.view.states.game;

import com.assignments.mtnpen.controller.game.GameController;
import com.assignments.mtnpen.model.game.GameModel;
import com.assignments.mtnpen.view.assetmanager.GameAssetManager;
import com.assignments.mtnpen.view.states.base.BaseState;
import com.assignments.mtnpen.view.states.manager.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.assignments.mtnpen.model.parameters.GameParameters;
import com.assignments.mtnpen.controller.input.InputController;

import com.badlogic.gdx.InputMultiplexer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class GameState extends BaseState {
    private final GameModel model;
    private final GameController controller;
    private final InputController inputController;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch sb;





    public GameState(GameStateManager gsm, String gameId, String playerId, String playerName) {
        super(gsm);
        this.model = new GameModel(gameId, playerId, playerName);
        this.controller = new GameController(model, gsm);
        this.inputController = new InputController(controller);
    }

    @Override
    public void create() {
        super.create();

        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();

        camera.setToOrtho(false, GameParameters.WORLD_WIDTH, GameParameters.WORLD_HEIGHT);
    }



    @Override
    protected void update(float delta) {
        model.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.pauseGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            controller.onGameFinished();
        }

        Vector2 screenPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        inputController.setPenguinPosition(screenPos.x, screenPos.y);


    }

    @Override
    public void enter() {
        // TODO: Initialize ECS, Load map, etc.
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputController);
        multiplexer.addProcessor(stage); // For handling back key, etc.
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void leave() {
        // TODO: Dispose assets
        shapeRenderer.dispose();
        super.leave();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // TODO: Render ECS entities

        sb.begin();
        sb.draw(GameAssetManager.loadPenguin1(), model.getPenguinPositionX() - 16, model.getPenguinPositionY() - 16, 32, 32);
        sb.end();

        camera.update();

        renderTrajectory();
    }

    private void renderTrajectory() {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for trajectory

        Vector2 penguinPos = new Vector2(model.getPenguinPositionX(), model.getPenguinPositionY());
        Vector2 launchVector = new Vector2(inputController.getRelativeTouchPos()).scl(-1); // Invert to get launch direction
        Vector2 currentPos = new Vector2(penguinPos);


        shapeRenderer.end();

    }
}
