package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_HEIGHT;
import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class WigglyEnemy extends Enemy {
    private final TextureAtlas enemyAtlas = new TextureAtlas("WigglyEnemy.atlas");
    private final List<Projectile> projectiles;
    //movement for wave formation
    float wavelength = WORLD_WIDTH / 4;
    float frequency = (2 * (float) Math.PI) / (wavelength * 2);
    float amplitude = WORLD_HEIGHT * 0.15f;
    float yOffset;
    private State state;
    private float stateTime;

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
        yOffset = MathUtils.random(0, WORLD_HEIGHT);
        boundingBox.x -= speed * delta;
        boundingBox.y = (float) (Math.sin(boundingBox.x * frequency) * amplitude + yOffset);

        stateTime += delta;
        // Translate the enemy to just beyond the world width
        if (boundingBox.x < -10f) {
            translate(WORLD_WIDTH + (float) (20 + Math.random() * 25), (int) (Math.random() * 43 + 15));
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
        yChange = MathUtils.random(0, WORLD_HEIGHT);
        boundingBox.setPosition(boundingBox.x + xChange, yChange);

    }
}