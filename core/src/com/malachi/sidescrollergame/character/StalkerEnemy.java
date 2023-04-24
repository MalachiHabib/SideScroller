package com.malachi.sidescrollergame.character;

import static com.malachi.sidescrollergame.screens.GameScreen.WORLD_HEIGHT;
import static com.malachi.sidescrollergame.screens.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class StalkerEnemy extends Enemy {
    private final TextureAtlas enemyAtlas = new TextureAtlas("CharacterAssets/WigglyEnemy.atlas");
    private States.State state;
    private float stateTime;
    private Player player;
    private Sound death;
    private boolean hasPlayedDeathSound = false;

    public StalkerEnemy(float movementSpeed, float width, float height,
                        float posX, float posY, Player player) {
        super(movementSpeed, width, height, posX, posY, 0);
        stateTime = 0;
        this.player = player;
        state = States.State.MOVING;
        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);
        death = Gdx.audio.newSound(Gdx.files.internal("Sounds/explosion07.wav"));
    }

    public void setCurrentState(States.State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime += delta;
        // Follow the player's Y position if enemy is alive
        if (state != States.State.DIED) {
            boundingBox.x -= speed * delta;
            float yDifference = player.getBoundingBox().y - boundingBox.y;
            boundingBox.y += yDifference * delta;
        }

        // Translate the enemy to just beyond the world width
        if (boundingBox.x < -10f) {
            this.teleport(WORLD_WIDTH + (float) (20 + Math.random() * 25), MathUtils.random(0, WORLD_HEIGHT));
        }

        Animation<TextureRegion> currentAnimation;
        switch (state) {
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case DIED:
                currentAnimation = dieAnimation;
                if(!hasPlayedDeathSound) {
                    death.play();
                    hasPlayedDeathSound = true;
                }
                if (dieAnimation.isAnimationFinished(stateTime)) {
                    speed = 30;
                    setCurrentState(States.State.MOVING);
                    this.teleport(WORLD_WIDTH - boundingBox.x + (float) (Math.random() * 40), MathUtils.random(0, WORLD_HEIGHT));
                }
                break;
            default:
                currentAnimation = movingAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
    }


    @Override
    public void teleport(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);

    }
}