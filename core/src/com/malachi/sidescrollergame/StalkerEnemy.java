package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_HEIGHT;
import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class StalkerEnemy extends Enemy {
    private final TextureAtlas enemyAtlas = new TextureAtlas("WigglyEnemy.atlas");
    private State state;
    private float stateTime;
    private Player player;
    public StalkerEnemy(float movementSpeed, float width, float height,
                        float posX, float posY, float timeBetweenShots, Player player) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        stateTime = 0;
        this.player = player;
        state = State.MOVING;
        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);

    }

    public void setCurrentState(State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        boundingBox.x -= speed * delta;
        stateTime += delta;

        // Follow the player's Y position
        float yDifference = player.boundingBox.y - boundingBox.y;
        boundingBox.y += yDifference * delta;

        // Translate the enemy to just beyond the world width
        if (boundingBox.x < -10f) {
            translate(WORLD_WIDTH + (float) (20 + Math.random() * 25), MathUtils.random(0, WORLD_HEIGHT));
        }

        Animation<TextureRegion> currentAnimation;
        switch (state) {
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case DIED:
                currentAnimation = dieAnimation;
                if (dieAnimation.isAnimationFinished(stateTime)) {
                    speed = 30;
                    setCurrentState(State.MOVING);
                    translate(WORLD_WIDTH - boundingBox.x + (float) (Math.random() * 40), MathUtils.random(0, WORLD_HEIGHT));
                }
                break;
            default:
                currentAnimation = movingAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
    }


    @Override
    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);

    }
}