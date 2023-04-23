package com.malachi.sidescrollergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class OnScreenController {
    public boolean up, down, left, right, shoot;
    private SpriteBatch batch;
    private Texture upButtonTexture, downButtonTexture, leftButtonTexture, rightButtonTexture, shootButtonTexture;
    private Rectangle upButtonBounds, downButtonBounds, leftButtonBounds, rightButtonBounds, shootButtonBounds;

    public OnScreenController(SpriteBatch batch) {
        this.batch = batch;
        // Initialize the button textures
        upButtonTexture = new Texture("gray_tile.png");
        downButtonTexture = new Texture("gray_tile.png");
        leftButtonTexture = new Texture("gray_tile.png");
        rightButtonTexture = new Texture("gray_tile.png");
        shootButtonTexture = new Texture("gray_tile.png");

        // Initialize the button bounds
        float buttonSize = 10;
        upButtonBounds = new Rectangle(10, 10, buttonSize, buttonSize);
        downButtonBounds = new Rectangle(10, 0, buttonSize, buttonSize);
        leftButtonBounds = new Rectangle(0, 5, buttonSize, buttonSize);
        rightButtonBounds = new Rectangle(20, 5, buttonSize, buttonSize);
        shootButtonBounds = new Rectangle(50, 5, buttonSize, buttonSize);
    }

    public void render() {
        System.out.print("rendered");
        // Draw the buttons
        batch.draw(upButtonTexture, upButtonBounds.getX(), upButtonBounds.getY(), upButtonBounds.getWidth(), upButtonBounds.getHeight());
        batch.draw(downButtonTexture, downButtonBounds.getX(), downButtonBounds.getY(), downButtonBounds.getWidth(), downButtonBounds.getHeight());
        batch.draw(leftButtonTexture, leftButtonBounds.getX(), leftButtonBounds.getY(), leftButtonBounds.getWidth(), leftButtonBounds.getHeight());
        batch.draw(rightButtonTexture, rightButtonBounds.getX(), rightButtonBounds.getY(), rightButtonBounds.getWidth(), rightButtonBounds.getHeight());
        batch.draw(shootButtonTexture, shootButtonBounds.getX(), shootButtonBounds.getY(), shootButtonBounds.getWidth(), shootButtonBounds.getHeight());
    }

    public Vector2 getPlayerControlInput(float delta, Player player) {
        float xMovement = 0f, yMovement = 0f;

        if (up)
            yMovement = player.speed * delta;
        if (down)
            yMovement = -player.speed * delta;
        if (left)
            xMovement = -player.speed * delta;
        if (right)
            xMovement = player.speed * delta;
        return new Vector2(xMovement, yMovement);
    }

    public boolean pressedShoot() {
        return shoot;
    }

    public void update(Viewport viewport) {
        // Reset button states
        up = down = left = right = shoot = false;

        // Check if any of the buttons are being touched
        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPosition);

            if (upButtonBounds.contains(touchPosition)) {
                up = true;
            }
            if (downButtonBounds.contains(touchPosition)) {
                down = true;
            }
            if (leftButtonBounds.contains(touchPosition)) {
                left = true;
            }
            if (rightButtonBounds.contains(touchPosition)) {
                right = true;
            }
            if (shootButtonBounds.contains(touchPosition)) {
                shoot = true;
            }
        }
    }

    public void dispose() {
        // Dispose the textures when no longer needed
        upButtonTexture.dispose();
        downButtonTexture.dispose();
        leftButtonTexture.dispose();
        rightButtonTexture.dispose();
    }
}
