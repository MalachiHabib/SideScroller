package com.malachi.sidescrollergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.malachi.sidescrollergame.SideScrollerGame;


public class GameOverScreen implements Screen {
    private final SideScrollerGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final Skin skin;
    private int score;

    public GameOverScreen(SideScrollerGame game, int score) {
        this.game = game;
        this.score = score;

        camera = new OrthographicCamera();
        stage = new Stage();

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        skin = new Skin(Gdx.files.internal("MainAssets/comic-ui.json"), new TextureAtlas(Gdx.files.internal("MainAssets/comic-ui.atlas")));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        createBackground();
        createGameOverLabel();
        createScoreLabel();
        createButtons();

        stage.act(delta);
        stage.draw();
    }

    private void createBackground() {
        Texture backgroundTexture = new Texture("Backgrounds/deathScreen.png");
        Image background = new Image(backgroundTexture);
        background.setScale(.5f);
        stage.addActor(background);
    }

    private void createGameOverLabel() {
        Label.LabelStyle gameOverStyle = new Label.LabelStyle(skin.getFont("title"), Color.WHITE);
        Label gameOverLabel = new Label("GAME OVER", gameOverStyle);
        gameOverLabel.setPosition(stage.getWidth() / 2 - gameOverLabel.getWidth() / 2, stage.getHeight() * 0.8f);
        stage.addActor(gameOverLabel);
    }

    private void createScoreLabel() {
        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("title"), Color.WHITE);
        Label scoreLabel = new Label("Score: " + score, scoreStyle);
        scoreLabel.setPosition(stage.getWidth() / 2 - scoreLabel.getWidth() / 2, stage.getHeight() * 0.7f);
        stage.addActor(scoreLabel);
    }

    private void createButtons() {
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        restartButton.setSize(stage.getWidth() / 4, stage.getHeight() / 10);
        quitButton.setSize(stage.getWidth() / 4, stage.getHeight() / 10);

        restartButton.setPosition(stage.getWidth() / 2 - restartButton.getWidth() / 2, stage.getHeight() * 0.45f);
        quitButton.setPosition(stage.getWidth() / 2 - quitButton.getWidth() / 2, stage.getHeight() * 0.3f);

        createListeners(restartButton, quitButton);

        stage.addActor(restartButton);
        stage.addActor(quitButton);
    }

    private void createListeners(TextButton restartButton, TextButton quitButton) {
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void show() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


}