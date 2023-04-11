package com.malachi.sidescroller;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Ship {
    float movementSpeed;
    Rectangle boundingBox;
    float projectileWidth, projectileHeight;
    float projectileMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0;
    protected TextureRegion shipTextureRegion, projectileTextureRegion;

    public Ship(float movementSpeed, float width, float height,
                float posX, float posY,
                float projectileWidth, float projectileHeight, float projectileMovementSpeed, float timeBetweenShots,
                TextureRegion projectileTextureRegion
    ) {
        this.movementSpeed = movementSpeed;
        this.boundingBox = new Rectangle(posX - width / 2, posY - height / 2, width, height);
        this.projectileWidth = projectileWidth;
        this.projectileHeight = projectileHeight;
        this.projectileMovementSpeed = projectileMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.projectileTextureRegion = projectileTextureRegion;
    }

    public void update(float deltaTime) {
        timeSinceLastShot += deltaTime;
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
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
