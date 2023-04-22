package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_HEIGHT;
import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WigglyEnemy extends Enemy {
    private State state;
    private float stateTime;
    private final TextureAtlas enemyAtlas = new TextureAtlas("enemyAtlas.atlas");
    private final TextureAtlas projectileAtlas = new TextureAtlas("Projectiles.atlas");
    private final List<Projectile> projectiles;

    private int randomChangeCounter = 0;
    private final int randomChangeInterval = 20; // The number of updates before changing random values
    private float currentFrequencyOffset = 0;
    private float currentAmplitudeOffset = 0;
    private float targetFrequencyOffset = 0;
    private float targetAmplitudeOffset = 0;
    private float lerpFactor = 0.1f;


    public WigglyEnemy(float movementSpeed, float width, float height,
                       float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        stateTime = 0;
        projectiles = new ArrayList<>();
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
        System.out.println(boundingBox.y);
        boundingBox.x -= speed * delta;

        // Fixed frequency and amplitude values
        float frequency = 0.1f;
        float amplitude = WORLD_HEIGHT * 0.15f;
        float yOffset = WORLD_HEIGHT / 2;

        // Update random values and apply linear interpolation
        if (randomChangeCounter == 0) {
            targetFrequencyOffset = (float) (Math.random() * 0.05 - 0.025);
            targetAmplitudeOffset = (float) (Math.random() * 0.1 - 0.05) * WORLD_HEIGHT;
        }

        currentFrequencyOffset += (targetFrequencyOffset - currentFrequencyOffset) * lerpFactor;
        currentAmplitudeOffset += (targetAmplitudeOffset - currentAmplitudeOffset) * lerpFactor;

        // Sine wave logic for vertical movement with randomization and smooth transitions
        float newY = (float) (Math.sin((boundingBox.x * (frequency + currentFrequencyOffset))) * (amplitude + currentAmplitudeOffset) + yOffset);
        boundingBox.y = newY;

        stateTime += delta;
        randomChangeCounter = (randomChangeCounter + 1) % randomChangeInterval;

        if (boundingBox.x < 0) {
            // Translate the enemy to just beyond the world width
            translate(WORLD_WIDTH + (float) (Math.random() * 10), (int) (Math.random() * 43 + 15));
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
                    translate(WORLD_WIDTH - boundingBox.x + (float) (Math.random() * 40), (int) (Math.random() * 43 + 15));
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

