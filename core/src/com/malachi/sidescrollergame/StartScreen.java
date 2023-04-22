package com.malachi.sidescrollergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
public class StartScreen implements Screen {
    private SideScrollerGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;

    public StartScreen(SideScrollerGame game) {
        this.game = game;
        SpriteBatch batch = game.batch;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("comic-ui.json"), new TextureAtlas(Gdx.files.internal("comic-ui.atlas")));
        TextButton startButton = new TextButton("Start", skin);
        TextButton quitButton = new TextButton("Quit", skin);
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float menuWidth = stage.getWidth() / 3;
        float buttonHeight = stage.getHeight() * 0.1f; // 10% of the screen height

        try {
            Texture backgroundTexture = new Texture("repeated.png");
            Image background = new Image(backgroundTexture);
            stage.addActor(background);
        } catch (Exception e) {
            Gdx.app.error("Error loading file", e.getMessage());
        }

        // Create a new table for the title label
        Table titleTable = new Table();
        titleTable.setSize(stage.getWidth(), stage.getHeight());

        // Create the title label and add it to the table
        Label titleLabel = new Label("Side Scroller", skin);
        titleLabel.setFontScale(2f);
        titleTable.add(titleLabel).center().padTop(stage.getHeight() * -0.33f);

        // Create a new table for the buttons
        Table buttonTable = new Table();
        buttonTable.setSize(stage.getWidth(), stage.getHeight());

        VerticalGroup buttonGroup = new VerticalGroup();
        buttonGroup.space(20); // Add some space between the buttons
        buttonGroup.align(Align.center);
        buttonGroup.padTop(stage.getHeight() * 0.33f);
        buttonGroup.setFillParent(true);
        buttonTable.addActor(buttonGroup);

        // Create buttons
        TextButton startButton = new TextButton("Play", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        // Set buttons size
        startButton.setSize(menuWidth, buttonHeight);
        quitButton.setSize(menuWidth, buttonHeight);

        buttonGroup.addActor(startButton);
        buttonGroup.addActor(quitButton);

        // Add actions
        buttonTable.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(1f),
                Actions.moveTo(-menuWidth / 2, 0, 0.5f, Interpolation.pow5),
                Actions.moveTo(menuWidth / 4, 0, 0.25f, Interpolation.pow2),
                Actions.moveTo(0, 0, 0.2f, Interpolation.pow2)
        ));

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Image blackScreen = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("black.jpeg"))));
                blackScreen.setSize(stage.getWidth(), stage.getHeight());
                blackScreen.setPosition(0, 0);
                blackScreen.setColor(1, 1, 1, 0);
                stage.addActor(blackScreen);

                blackScreen.addAction(Actions.sequence(
                        Actions.fadeIn(0.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new GameScreen(game));
                            }
                        })
                ));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(titleTable);
        stage.addActor(buttonTable);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
