package com.malachi.sidescroller;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Player extends Ship {
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private TextureAtlas playerAtlas = new TextureAtlas("playerIdleAnimation.atlas");
    public Player(float movementSpeed, float width, float height,
                  float posX, float posY,
                  float projectileWidth, float projectileHeight, float projectileMovementSpeed,
                  float timeBetweenShots,
                  TextureRegion projectileTextureRegion) {
        super(movementSpeed, width, height, posX, posY, projectileWidth, projectileHeight, projectileMovementSpeed, timeBetweenShots, projectileTextureRegion);
        idleAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-MovingNIdle"), Animation.PlayMode.LOOP);
        stateTime = 0;
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stateTime += deltaTime;
        shipTextureRegion = idleAnimation.getKeyFrame(stateTime);
    }


    @Override
    public Projectile[] fireProjectiles() {
        Projectile[] projectiles = new Projectile[1];
        projectiles[0] = new Projectile(boundingBox.x + 8f, boundingBox.y, projectileWidth, projectileHeight, projectileMovementSpeed, projectileTextureRegion);
        timeSinceLastShot = 0;
        return projectiles;
    }
}
