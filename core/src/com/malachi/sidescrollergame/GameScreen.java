package com.malachi.sidescroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;

class GameScreen implements Screen {
    public static final int WORLD_WIDTH = 128;
    public static final int WORLD_HEIGHT = 72;
    private final float TOUCH_THRESHOLD = 5f;
    private SideScrollerGame game;
    private Camera camera;
    private Viewport viewport;
    private Texture[] backgrounds;
    private float[] backgroundOffsets = {0, 0, 0, 0};
    private float backgroundMaxScrollSpeed;
    private TextureAtlas textureAtlas;
    private TextureRegion playerShipTextureRegion, playerProjectileTextureRegion, enemyShipTextureRegion;
    private Player player;
    private Enemy en;
    private Enemy[] enemies = new Enemy[2];
    private LinkedList<Projectile> playerProjectileList;

    public GameScreen(SideScrollerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        textureAtlas = new TextureAtlas("playerIdleAnimation.atlas");
        playerProjectileTextureRegion = textureAtlas.findRegion("skeleton-MovingNIdle");

        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        backgrounds = new Texture[4];
        backgrounds[0] = new Texture("Layer1.png");
        backgrounds[1] = new Texture("Layer2.png");
        backgrounds[2] = new Texture("Layer3.png");
        backgrounds[3] = new Texture("Layer4.png");

        backgroundMaxScrollSpeed = (float) WORLD_HEIGHT / 4;

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Enemy(20, 10, 10, WORLD_WIDTH * (i + 1) * 2, WORLD_HEIGHT / ((int) (Math.random() * 9) + 2) , 4, 2, 45, 0.5f, playerProjectileTextureRegion);
        }
        player = new Player(40, 10, 10, WORLD_WIDTH / 8, WORLD_HEIGHT / 4, 4, 2, 45, 0.5f, playerProjectileTextureRegion);
        en = new Enemy(20, 10, 10, WORLD_WIDTH, WORLD_HEIGHT * 2, 4, 2, 45, 0.5f, playerProjectileTextureRegion);
        playerProjectileList = new LinkedList<>();
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        detectInput(delta);
        player.update(delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }

        renderBackground(delta);
        player.draw(game.batch);

        for (Enemy enemy : enemies) {
            enemy.draw(game.batch);
        }

        renderProjectiles(delta);
        detectCollisions();
        game.batch.end();
    }

    private void detectInput(float delta) {
        float leftBoundary = -player.boundingBox.x;
        float bottomBoundary = -player.boundingBox.y;
        float rightBoundary = WORLD_WIDTH * 2 / 3 - player.boundingBox.x - player.boundingBox.width;
        float topBoundary = WORLD_HEIGHT - player.boundingBox.y - player.boundingBox.height;

        float xMovement = 0f, yMovement = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            xMovement = Math.min(player.movementSpeed * delta, rightBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            xMovement = Math.max(-player.movementSpeed * delta, leftBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            yMovement = Math.min(player.movementSpeed * delta, topBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            yMovement = Math.max(-player.movementSpeed * delta, bottomBoundary);

        player.translate(xMovement, yMovement);

        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            Vector2 playerCenter = new Vector2(player.boundingBox.x + player.boundingBox.width / 2, player.boundingBox.y + player.boundingBox.height / 2);

            if (touchPosition.dst(playerCenter) > TOUCH_THRESHOLD) {
                float xTouchDifference = touchPosition.x - playerCenter.x;
                float yTouchDifference = touchPosition.y - playerCenter.y;
                float distance = touchPosition.dst(playerCenter);

                float touchXMovement = xTouchDifference / distance * player.movementSpeed * delta;
                float touchYMovement = yTouchDifference / distance * player.movementSpeed * delta;

                touchXMovement = (touchXMovement > 0) ? Math.min(touchXMovement, rightBoundary) : Math.max(touchXMovement, leftBoundary);
                touchYMovement = (touchYMovement > 0) ? Math.min(touchYMovement, topBoundary) : Math.max(touchYMovement, bottomBoundary);

                player.translate(touchXMovement, touchYMovement);
            }
        }
    }


    private void detectCollisions() {
        ListIterator<Projectile> iterator = playerProjectileList.listIterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            for (Enemy enemy : enemies) {
                if (enemy.intersects(projectile.boundingBox)) {
                    enemy.movementSpeed = 0;
                    enemy.setCurrentState(Enemy.State.DIED);
                    iterator.remove();
                }

            }
        }
        for (Enemy enemy : enemies) {
            if (enemy.intersects(player.boundingBox)) {
                game.setScreen(new GameOverScreen(game, 0));
            }
        }
    }

    private void renderProjectiles(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.canShoot()) {
            Projectile[] projectiles = player.fireProjectiles();
            for (Projectile projectile : projectiles) {
                playerProjectileList.add(projectile);
            }
        }

        ListIterator<Projectile> iterator = playerProjectileList.listIterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.draw(game.batch);
            projectile.boundingBox.x += projectile.movementSpeed * delta;
            if (projectile.boundingBox.x > WORLD_WIDTH) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float delta) {
        backgroundOffsets[0] += delta * backgroundMaxScrollSpeed / 8;
        backgroundOffsets[1] += delta * backgroundMaxScrollSpeed / 4;
        backgroundOffsets[2] += delta * backgroundMaxScrollSpeed / 2;
        backgroundOffsets[3] += delta * backgroundMaxScrollSpeed;

        for (int i = 0; i < backgroundOffsets.length; i++) {
            if (backgroundOffsets[i] > WORLD_WIDTH) {
                backgroundOffsets[i] = 0;
            }
            game.batch.draw(backgrounds[i], -backgroundOffsets[i], 0, WORLD_WIDTH, WORLD_HEIGHT);
            game.batch.draw(backgrounds[i], -backgroundOffsets[i] + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        game.batch.setProjectionMatrix(camera.combined);
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
