package com.malachi.sidescrollergame;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

class GameScreen implements Screen {

    public static final int WORLD_WIDTH = 128;
    public static final int WORLD_HEIGHT = 72;
    private final Camera camera;
    private final Viewport viewport;
    private final Texture[] backgrounds;
    private final float[] bgOffsets = {0, 0, 0, 0, 0};
    private final Player player;
    private final Enemy[] enemies = new Enemy[4];
    public SideScrollerGame game;
    float ogCamPosX;
    float ogCamPosY;
    private Music backgroundMusic;
    private OnScreenController onScreenController;
    private ScreenShake screenShake;
    private float bgSpeed;

    public GameScreen(SideScrollerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        ogCamPosX = camera.position.x;
        ogCamPosY = camera.position.y;

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

        for (int i = 0; i < 4; i++) {
            float randomOffset = (float) (Math.random() * WORLD_WIDTH / 4);
            float xPos = WORLD_WIDTH + WORLD_WIDTH / 2 * ((i % 2) + 1) + randomOffset;
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            bgOffsets[i] += delta * bgSpeed / (8 / (i + 1));
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
            enemy.update(delta);
            enemy.draw(SideScrollerGame.batch);
        }

        if (player.getState() != Character.State.DIED) {
            player.score += delta * 10;
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
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.canShoot()) {
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
                if (enemy.intersects(projectile.boundingBox)) {
                    player.score += 100;
                    enemy.speed = 0;
                    enemy.setCurrentState(Character.State.DIED);
                }
            }
        }

        // Enemy projectiles colliding with the player
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShootingEnemy) {
                ShootingEnemy shootingEnemy = (ShootingEnemy) enemy;
                List<Projectile> shootingEnemyProjectiles = shootingEnemy.getProjectiles();
                for (Projectile projectile : shootingEnemyProjectiles) {
                    if (player.intersects(projectile.boundingBox)) {
                        if (player.isAnimationFinished()) {
                            player.setCurrentState(Character.State.DIED);
                        }
                    }
                }
            }
        }

        // Enemies colliding with the player
        for (Enemy enemy : enemies) {
            if (enemy.intersects(player.boundingBox) && (enemy.getState() != Character.State.DIED)) {
                player.setCurrentState(Character.State.DIED);
                player.update(Gdx.graphics.getDeltaTime()); // Update the player's death animation
            }
        }

        if (player.getState() == Character.State.DIED) {
            if (player.isAnimationFinished()) {
                backgroundMusic.stop();
                game.setScreen(new GameOverScreen(game, (int) player.score));
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        SideScrollerGame.batch.setProjectionMatrix(camera.combined);
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
    public void show() {
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0f);
        backgroundMusic.play();
        screenShake = new ScreenShake(0.25f, 5f);
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
    }
}