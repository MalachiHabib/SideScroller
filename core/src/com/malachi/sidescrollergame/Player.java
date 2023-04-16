package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Player extends Character {
    private State state;
    private float stateTime;
    private TextureAtlas playerAtlas = new TextureAtlas("playerAtlas.atlas");

    public Player(float movementSpeed, float width, float height,
                  float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        idleAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-MovingNIdle"), Animation.PlayMode.LOOP);
        dieAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-Destroy"), Animation.PlayMode.LOOP);
        stateTime = 0;

        state = State.IDLE;
    }


    public void setCurrentState(State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

    public State getState() {
        return state;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime += delta;

        Animation<TextureRegion> currentAnimation;
        switch (state) {
            case DIED:
                currentAnimation = dieAnimation;
                break;
            default:
                currentAnimation = idleAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
    }

    @Override
    public Projectile[] fireProjectiles() {
        TextureRegion playerProjectileTextureRegion = playerAtlas.findRegion("skeleton-MovingNIdle");
        Projectile[] projectiles = new Projectile[1];
        projectiles[0] = new Projectile(boundingBox.x + 8f, boundingBox.y, 10, 4, 10, playerProjectileTextureRegion);
        timeSinceLastShot = 0;
        return projectiles;
    }
}
