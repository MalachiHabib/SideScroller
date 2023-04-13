package com.malachi.sidescrollergame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Character {
    float speed;
    Rectangle boundingBox;
    float timeBetweenShots;
    float timeSinceLastShot = 0;
    TextureRegion characterTexture;

    public Character(float movementSpeed, float width, float height,
                     float posX, float posY,
                     float timeBetweenShots
    ) {
        this.speed = movementSpeed;
        this.boundingBox = new Rectangle(posX - width / 2, posY - height / 2, width, height);
        this.timeBetweenShots = timeBetweenShots;
    }

    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    public boolean canShoot() {
         return timeSinceLastShot - timeBetweenShots >= 0;
    }

    public void hit(Projectile projectile) {

    }

    public boolean intersects(Rectangle rectangle) {
        return boundingBox.overlaps(rectangle);
    }

    public abstract Projectile[] fireProjectiles();

    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    public void draw(Batch batch) {
        batch.draw(characterTexture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
