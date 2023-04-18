package com.malachi.sidescrollergame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends Character {
    private State state;
    private float stateTime;
    private final TextureAtlas enemyAtlas = new TextureAtlas("enemyAtlas.atlas");

    public Enemy(float movementSpeed, float width, float height,
                 float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);

        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);
        stateTime = 0;

        // moving is default state for enemies in this game
        state = State.MOVING;
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
        //moves the enemy faster if they are not on screen
        if (boundingBox.x > 128) boundingBox.x -= ((int) (Math.random() * 10) + 1) * delta;
        else boundingBox.x -= speed * delta;

        stateTime += delta;

        Animation<TextureRegion> currentAnimation;
        switch (state) {
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case DIED:
                currentAnimation = dieAnimation;
                if (dieAnimation.isAnimationFinished(stateTime)) {
                    float minY = -boundingBox.y;
                    float maxY = GameScreen.WORLD_HEIGHT - boundingBox.y - boundingBox.height;
                    // Generate a random float value between minY and maxY
                    float randomY = minY + SideScrollerGame.random.nextFloat() * (maxY - minY);

                    setCurrentState(State.MOVING);
                    speed = 20;
                    translate(boundingBox.x + 80f, randomY);
                }
                break;
            default:
                currentAnimation = movingAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
    }
}
