package com.malachi.sidescrollergame.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Character {
    protected Animation<TextureRegion> idleAnimation, movingAnimation, dieAnimation;
    protected States.State state;

    protected float speed;
    protected Rectangle boundingBox;
    protected float timeBetweenShots;
    protected float timeSinceLastShot = 0;
    protected TextureRegion characterTexture;
    protected float stateTime;

    public Character(float movementSpeed, float width, float height,
                     float posX, float posY,
                     float timeBetweenShots
    ) {
        this.speed = movementSpeed;
        this.boundingBox = new Rectangle(posX - width / 2, posY - height / 2, width, height);
        this.timeBetweenShots = timeBetweenShots;
    }

    public void eraseSpeed() {
        speed = 0;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float newSpeed) {
        speed += newSpeed;
    }

    public abstract void setCurrentState(States.State newState);

    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    public boolean canShoot() {
        return timeSinceLastShot - timeBetweenShots >= 0;
    }

    public boolean intersects(Rectangle rectangle) {
        return boundingBox.overlaps(rectangle);
    }

    public abstract void teleport(float xChange, float yChange);

    public void draw(Batch batch) {
        batch.draw(characterTexture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
