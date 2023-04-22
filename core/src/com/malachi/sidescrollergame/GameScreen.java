package com.malachi.sidescrollergame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;


class GameScreen implements Screen {
    public static final int WORLD_WIDTH = 128;
    public static final int WORLD_HEIGHT = 72;
    public SideScrollerGame game;
    private final Camera camera;
    private final Viewport viewport;
    private final Texture[] backgrounds;
    private final float[] bgOffsets = {0, 0, 0, 0};
    private float bgSpeed;
    private float accelerationRate = .1f;
    private OnScreenController onScreenController;
    float score = 0;
    private boolean deathAnimationFinished = false;

    private final Player player;
    private final Enemy[] enemies = new Enemy[2];

    public GameScreen(SideScrollerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        backgrounds = new Texture[4];
        backgrounds[0] = new Texture("Layer1.png");
        backgrounds[1] = new Texture("Layer2.png");
        backgrounds[2] = new Texture("Layer3.png");
        backgrounds[3] = new Texture("Layer4.png");

        bgSpeed = (float) WORLD_HEIGHT / 4;
        player = new Player(40, 13, 13, (float) WORLD_WIDTH / 8, (float) WORLD_HEIGHT / 2, 1f);

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Enemy(35, 13, 13, WORLD_WIDTH + (WORLD_WIDTH / 2f) * i,  (int) (Math.random() * 53 + 10), 8f);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            onScreenController = new OnScreenController();
        }
    }

    @Override
    public void render(float delta) {

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            onScreenController.render(SideScrollerGame.batch);
        }
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            SideScrollerGame.batch.begin();
            renderBackground(delta);
            detectInput(delta);
            player.update(delta);
            player.draw(SideScrollerGame.batch);

            for (Enemy enemy : enemies) {
                enemy.update(delta);
                enemy.draw(SideScrollerGame.batch);
            }

            if (player.getState() != Character.State.DIED) {
                score += delta * 10;
            }

            renderProjectiles(delta);
            detectCollisions();
            SideScrollerGame.batch.end();

    }

    private void renderBackground(float delta) {
        //Make the background progressively faster the more time goes on
        bgSpeed += delta * accelerationRate;

        bgOffsets[0] += delta * bgSpeed / 8;
        bgOffsets[1] += delta * bgSpeed / 4;
        bgOffsets[2] += delta * bgSpeed / 2;
        bgOffsets[3] += delta * bgSpeed;

        for (int i = 0; i < bgOffsets.length; i++) {
            if (bgOffsets[i] > WORLD_WIDTH) {
                bgOffsets[i] = 0;
            }
            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i], 0, WORLD_WIDTH, WORLD_HEIGHT);
            SideScrollerGame.batch.draw(backgrounds[i], -bgOffsets[i] + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    private void detectInput(float delta) {
        float leftBoundary = -player.boundingBox.x;
        float bottomBoundary = -player.boundingBox.y;
        float rightBoundary = (float) WORLD_WIDTH / 2 - player.boundingBox.x - player.boundingBox.width;
        float topBoundary = WORLD_HEIGHT - player.boundingBox.y - player.boundingBox.height;

        float xMovement = 0f, yMovement = 0f;

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            onScreenController.update(viewport);
            Vector2 movement = onScreenController.getPlayerControlInput(delta, player);
            xMovement = movement.x;
            yMovement = movement.y;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                xMovement = player.speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                xMovement = -player.speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                yMovement = player.speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                yMovement = -player.speed * delta;
        }

        xMovement = (xMovement > 0) ? Math.min(xMovement, rightBoundary) : Math.max(xMovement, leftBoundary);
        yMovement = (yMovement > 0) ? Math.min(yMovement, topBoundary) : Math.max(yMovement, bottomBoundary);

        player.translate(xMovement, yMovement);
    }

    private void renderProjectiles(float delta) {
        for (Enemy enemy : enemies) {
            enemy.fireProjectile(delta);
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
        List<Projectile> enemyProjectiles;

        // Player projectiles colliding with enemies
        for (Projectile projectile : playerProjectiles) {
            for (Enemy enemy : enemies) {
                if (enemy.intersects(projectile.boundingBox)) {
                    score += 100;
                    enemy.speed = 0;
                    enemy.setCurrentState(Character.State.DIED);
                }
            }
        }

        // Enemy projectiles colliding with the player
        for (Enemy enemy : enemies) {
            enemyProjectiles = enemy.getProjectiles();
            for (Projectile projectile : enemyProjectiles) {
                if (player.intersects(projectile.boundingBox)) {
                    if (!deathAnimationFinished) {
                        player.setCurrentState(Character.State.DIED);
                        player.update(Gdx.graphics.getDeltaTime()); // Update the player's death animation
                    }
                }
            }
        }

        // Enemies colliding with the player
        for (Enemy enemy : enemies) {
            if (enemy.intersects(player.boundingBox) && (enemy.getState() != Enemy.State.DIED)) {
                if (!deathAnimationFinished) {
                    player.setCurrentState(Character.State.DIED);
                    player.update(Gdx.graphics.getDeltaTime()); // Update the player's death animation
                }
            }
        }

        if (player.getState() == Character.State.DIED) {
            if (!deathAnimationFinished && player.isAnimationFinished()) {
                deathAnimationFinished = true;
            } else if (deathAnimationFinished) {
                game.setScreen(new GameOverScreen(game, (int) score));
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

    }

    @Override
    public void dispose() {

    }
}
