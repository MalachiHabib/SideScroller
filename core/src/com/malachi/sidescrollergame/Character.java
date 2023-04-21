package com.malachi.sidescrollergame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


abstract class Character {

    public enum State {
        IDLE,
        MOVING,
        DIED
    }

    Animation<TextureRegion> idleAnimation, movingAnimation, dieAnimation;
    float speed;
    Rectangle boundingBox;
    float timeBetweenShots;
    float timeSinceLastShot = 0;
    TextureRegion characterTexture;
    private State state;

    public Character(float movementSpeed, float width, float height,
                     float posX, float posY,
                     float timeBetweenShots
    ) {
        this.speed = movementSpeed;
        this.boundingBox = new Rectangle(posX - width / 2, posY - height / 2, width, height);
        this.timeBetweenShots = timeBetweenShots;
    }

    public abstract void fireProjectile(float delta);
    public abstract void setCurrentState(State newState);

    public abstract State getState();

    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    public boolean canShoot() {
        return timeSinceLastShot - timeBetweenShots >= 0;
    }

    public boolean intersects(Rectangle rectangle) {
        return boundingBox.overlaps(rectangle);
    }

    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    public void draw(Batch batch) {
        batch.draw(characterTexture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
