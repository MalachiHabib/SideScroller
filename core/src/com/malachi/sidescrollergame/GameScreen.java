package com.malachi.sidescrollergame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;


class GameScreen implements Screen {
    public static final int WORLD_WIDTH = 128;
    public static final int WORLD_HEIGHT = 72;
    private final Camera camera;
    private final Viewport viewport;
    private final Texture[] backgrounds;
    private final float[] bgOffsets = {0, 0, 0, 0};
    private float bgSpeed;
    private float accelerationRate = .1f;


    private final Player player;
    private final Enemy[] enemies = new Enemy[2];

    public GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        backgrounds = new Texture[4];
        backgrounds[0] = new Texture("Layer1.png");
        backgrounds[1] = new Texture("Layer2.png");
        backgrounds[2] = new Texture("Layer3.png");
        backgrounds[3] = new Texture("Layer4.png");

        bgSpeed = (float) WORLD_HEIGHT / 4;
        player = new Player(40, 10, 10, (float) WORLD_WIDTH / 8, (float) WORLD_HEIGHT / 4, 0.5f);

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Enemy(20, 10, 10, (float) WORLD_WIDTH * 1 * (i + 1), (float) WORLD_HEIGHT / (float) (2 + (Math.random() * (9 - 2))), 0.5f);
        }

    }

    @Override
    public void render(float delta) {
        SideScrollerGame.batch.begin();
        detectInput(delta);
        player.update(delta);

        renderBackground(delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }

        player.draw(SideScrollerGame.batch);

        for (Enemy enemy : enemies) {
            enemy.draw(SideScrollerGame.batch);
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
        float rightBoundary = (float) WORLD_WIDTH * 2 / 3 - player.boundingBox.x - player.boundingBox.width;
        float topBoundary = WORLD_HEIGHT - player.boundingBox.y - player.boundingBox.height;

        float xMovement = 0f, yMovement = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            xMovement = Math.min(player.speed * delta, rightBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            xMovement = Math.max(-player.speed * delta, leftBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            yMovement = Math.min(player.speed * delta, topBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            yMovement = Math.max(-player.speed * delta, bottomBoundary);

        player.translate(xMovement, yMovement);

        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            Vector2 playerCenter = new Vector2(player.boundingBox.x + player.boundingBox.width / 2, player.boundingBox.y + player.boundingBox.height / 2);

            float TOUCH_THRESHOLD = 5f;
            if (touchPosition.dst(playerCenter) > TOUCH_THRESHOLD) {
                float xTouchDifference = touchPosition.x - playerCenter.x;
                float yTouchDifference = touchPosition.y - playerCenter.y;
                float distance = touchPosition.dst(playerCenter);

                float touchXMovement = xTouchDifference / distance * player.speed * delta;
                float touchYMovement = yTouchDifference / distance * player.speed * delta;

                touchXMovement = (touchXMovement > 0) ? Math.min(touchXMovement, rightBoundary) : Math.max(touchXMovement, leftBoundary);
                touchYMovement = (touchYMovement > 0) ? Math.min(touchYMovement, topBoundary) : Math.max(touchYMovement, bottomBoundary);

                player.translate(touchXMovement, touchYMovement);
            }
        }
    }

    private void renderProjectiles(float delta) {
        player.fireProjectile(delta);
        for (Enemy enemy : enemies) {
            enemy.fireProjectile(delta);
        }
    }

    private void detectCollisions() {
        List<Projectile> projectiles = player.getProjectiles();

        for (Projectile projectile : projectiles) {
            for (Enemy enemy : enemies) {
                if (enemy.intersects(projectile.boundingBox)) {
                    enemy.speed = 0;
                    enemy.setCurrentState(Character.State.DIED);
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.intersects(player.boundingBox) && (enemy.getState() != Enemy.State.DIED)) {
                player.setCurrentState(Character.State.DIED);
                //add game over screen or something
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
