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

import com.badlogic.gdx.graphics.Texture;




public class GameState extends BaseState {
    private final GameModel model;
    private final GameController controller;
    private final InputController inputController;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch batch;
    private Texture penguinTexture;






    public GameState(GameStateManager gsm, String gameId, String playerId, String playerName) {
        super(gsm);
        this.model = new GameModel(gameId, playerId, playerName);
        this.controller = new GameController(model, gsm);
        this.camera = new OrthographicCamera();
        this.inputController = new InputController(model, controller, camera);
    }

    @Override
    public void create() {
        super.create();

        camera.setToOrtho(false, GameParameters.WORLD_WIDTH, GameParameters.WORLD_HEIGHT);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        penguinTexture = GameAssetManager.loadPenguin1(); //TODO: Replace with actual asset

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

        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1); // Light blue background
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderMap();
        renderPenguin();
        batch.end();

        renderAimLayer();
    }

    private void renderPenguin() {
        batch.draw(penguinTexture, model.getPenguinPositionX() - 16, model.getPenguinPositionY() - 16, 32, 32);
    }

    private void renderMap() {
    }

    private void renderAimLayer() {
        if(inputController.getState() != InputController.InputState.LOCKED) {
            Vector2 penguinPos = new Vector2(model.getPenguinPositionX(), model.getPenguinPositionY());
            Vector2 aimPos = inputController.getRelativeTouchPos().scl(-1); // Invert to get launch direction
            shapeRenderer.setProjectionMatrix(camera.combined);
            renderDragLine(penguinPos, aimPos);
            renderTrajectory(penguinPos, aimPos);
   
        }
    }

    private void renderDragLine(Vector2 penguinPos,Vector2 aimPos) {


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.8f);

        shapeRenderer.line(penguinPos, penguinPos.cpy().sub(aimPos));
        shapeRenderer.end();

    }

    

    private void renderTrajectory(Vector2 penguinPos,Vector2 aimPos) {




        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0.9f, 0.2f, 1f); // yellow dots
 
        for (int i = 1; i <= GameParameters.TRAJECTORY_STEPS; i++) {
            float t = i * GameParameters.TRAJECTORY_STEP_TIME;
            float dotX = penguinPos.x + aimPos.x * t;
            float dotY = penguinPos.y + aimPos.y * t;
 
            // Stop drawing if the dot exits the world
            if (dotX < 0 || dotX > GameParameters.WORLD_WIDTH
                    || dotY < 0 || dotY > GameParameters.WORLD_HEIGHT) break;
 
            // Dots fade as they get further along the arc
            float alpha = 1f - (float) i / GameParameters.TRAJECTORY_STEPS;
            shapeRenderer.setColor(1f, 0.9f, 0.2f, alpha);
            shapeRenderer.circle(dotX, dotY, 5f, 10);
        }
 
        shapeRenderer.end();


    }
}
