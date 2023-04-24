package com.malachi.sidescrollergame.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.malachi.sidescrollergame.SideScrollerGame;
import com.malachi.sidescrollergame.character.Enemy;
import com.malachi.sidescrollergame.character.Player;
import com.malachi.sidescrollergame.character.Projectile;
import com.malachi.sidescrollergame.character.ShootingEnemy;
import com.malachi.sidescrollergame.character.StalkerEnemy;
import com.malachi.sidescrollergame.character.States;
import com.malachi.sidescrollergame.util.OnScreenController;
import com.malachi.sidescrollergame.util.ScreenShake;

import java.util.List;

public class GameScreen implements Screen {

    public static final int WORLD_WIDTH = 128;
    public static final int WORLD_HEIGHT = 72;
    public static final float SPEED_INCREASE_FACTOR = 0.06f;
    private final Camera camera;
    private final Viewport viewport;
    private final Texture[] backgrounds;
    private final float[] bgOffsets = {0, 0, 0, 0, 0};
    private final Player player;
    private final Enemy[] enemies = new Enemy[4];
    public SideScrollerGame game;
    private Music backgroundMusic;
    private OnScreenController onScreenController;
    private ScreenShake screenShake;
    private float bgSpeed;
    private boolean paused;
    private Sprite grayOverlay;

    public GameScreen(SideScrollerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        backgrounds = new Texture[7];
        backgrounds[0] = new Texture("backgrounds/sky.png");
        backgrounds[1] = new Texture("backgrounds/rocks_1.png");
        backgrounds[2] = new Texture("backgrounds/rocks_2.png");
        backgrounds[3] = new Texture("backgrounds/clouds_1.png");
        backgrounds[4] = new Texture("backgrounds/clouds_2.png");
        backgrounds[5] = new Texture("backgrounds/clouds_3.png");
        backgrounds[6] = new Texture("backgrounds/clouds_4.png");

        bgSpeed = (float) WORLD_HEIGHT / 4;
        player = new Player(40, 13, 13, (float) WORLD_WIDTH / 8, (float) WORLD_HEIGHT / 2, (float) (1 + Math.random() * 1.5));

        grayOverlay = new Sprite(new Texture("Backgrounds/black.jpeg"));
        grayOverlay.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        grayOverlay.setAlpha(0.5f);

        for (int i = 0; i < 4; i++) {
            float randomOffset = (float) (Math.random() * WORLD_WIDTH);
            float xPos = WORLD_WIDTH * 2 * ((i % 2) + 1) + randomOffset;
            float yPos = (int) (Math.random() * 53 + 10);

            if (i < 2) {
                enemies[i] = new ShootingEnemy(35, 13, 13, xPos, yPos, 8f);
            } else {
                enemies[i] = new StalkerEnemy(35, 13, 13, xPos, yPos, player);
            }
        }

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/main.wav"));

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            onScreenController = new OnScreenController(SideScrollerGame.batch);
        }
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        if (paused) {
            delta = 0;
        }

        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        bgSpeed += bgSpeed * delta * SPEED_INCREASE_FACTOR;

        screenShake.update(delta);
        if (!screenShake.isFinished()) {
            float shakeIntensity = screenShake.getShakeIntensity();
            float shakeX = (float) (Math.random() * 2 - 1) * shakeIntensity;
            float shakeY = (float) (Math.random() * 2 - 1) * shakeIntensity;

            camera.position.set(camera.position.x + shakeX, camera.position.y + shakeY, 0);
            camera.update();
        }
        camera.position.set(64, 36, 0);

        SideScrollerGame.batch.setProjectionMatrix(camera.combined);
        SideScrollerGame.batch.begin();

        for (int i = 0; i <= 3; i++) {
            bgOffsets[i] += delta * bgSpeed / (7 / (i + 1));
            if (bgOffsets[i] > WORLD_WIDTH) {
                bgOffsets[i] = 0;
            }
            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i], 0, WORLD_WIDTH, WORLD_HEIGHT);
            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i] + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }

        player.detectInput(delta, onScreenController, screenShake);
        player.update(delta);
        player.draw(SideScrollerGame.batch);

        for (Enemy enemy : enemies) {
            enemy.setSpeed(enemy.getSpeed() * delta * SPEED_INCREASE_FACTOR);
            enemy.update(delta);
            enemy.draw(SideScrollerGame.batch);
        }

        if (player.getState() != States.State.DIED) {
            player.setScore(delta * 10 * SPEED_INCREASE_FACTOR);
        }

        renderProjectiles(delta);
        detectCollisions();

        for (int i = 4; i < bgOffsets.length; i++) {
            bgOffsets[i] += delta * bgSpeed;
            if (bgOffsets[i] > WORLD_WIDTH) {
                bgOffsets[i] = 0;
            }

            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i], 0, WORLD_WIDTH, WORLD_HEIGHT);
            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i] + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            onScreenController.update(viewport);
            onScreenController.render();
        }

        if (paused) {
            grayOverlay.draw(SideScrollerGame.batch);
        }

        SideScrollerGame.batch.end();
    }

    private void renderProjectiles(float delta) {
        //TODO: enemies all shoot at same time maybe change that
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy shootingEnemy = (ShootingEnemy) enemy;
                shootingEnemy.fireProjectile(delta);
            }
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if (onScreenController.pressedShoot() && player.canShoot()) {
                player.addNewProjectile();
                camera.update();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.canShoot() && player.getState() != States.State.DIED) {
            player.addNewProjectile();
        }

        player.updateProjectiles(delta);
        player.renderProjectiles(SideScrollerGame.batch);
        player.removeOutOfBoundsProjectiles(WORLD_WIDTH);
    }

    private void detectCollisions() {
        List<Projectile> playerProjectiles = player.getProjectiles();

        // Player projectiles colliding with enemies
        for (Projectile projectile : playerProjectiles) {
            for (Enemy enemy : enemies) {
                if (enemy.intersects(projectile.getBoundingBox())) {
                    player.setScore(100);
                    enemy.eraseSpeed();
                    enemy.setCurrentState(States.State.DIED);
                }
            }
        }

        // Enemy projectiles colliding with the player
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy shootingEnemy = (ShootingEnemy) enemy;
                List<Projectile> shootingEnemyProjectiles = shootingEnemy.getProjectiles();
                for (Projectile projectile : shootingEnemyProjectiles) {
                    if (player.intersects(projectile.getBoundingBox())) {
                        if (player.isAnimationFinished()) {
                            player.setCurrentState(States.State.DIED);
                        }
                    }
                }
            }
        }

        // Enemies colliding with the player
        for (Enemy enemy : enemies) {
            if (enemy.intersects(player.getBoundingBox()) && (enemy.getState() != States.State.DIED)) {
                player.setCurrentState(States.State.DIED);
                player.update(Gdx.graphics.getDeltaTime()); // Update the player's death animation
            }
        }

        if (player.getState() == States.State.DIED) {
            player.eraseSpeed();
            if (player.isAnimationFinished()) {
                backgroundMusic.stop();
                game.setScreen(new GameOverScreen(game, (int) player.getScore()));
            }
        }
    }

    private void togglePause() {
        paused = !paused;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        SideScrollerGame.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {
        backgroundMusic.dispose();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        backgroundMusic.dispose();
    }

    @Override
    public void show() {
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        screenShake = new ScreenShake(0.25f, 5f);
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
        game.dispose();
    }
}