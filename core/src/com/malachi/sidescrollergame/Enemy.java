package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enemy extends Character {
    private State state;
    private float stateTime;
    private final TextureAtlas enemyAtlas = new TextureAtlas("enemyAtlas.atlas");
    private final List<Projectile> projectiles;

    public Enemy(float movementSpeed, float width, float height,
                 float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);

        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);
        stateTime = 0;

        projectiles = new ArrayList<>();
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
    public void fireProjectile(float delta) {
        //TODO: MAKE IT RANDOM INTERVAL OR SOMETHING
        timeSinceLastShot += delta;

        if (timeSinceLastShot >= timeBetweenShots && canShoot() && state != State.DIED && boundingBox.x < WORLD_WIDTH) {
            addNewProjectile();
        }

        updateProjectiles(delta);
        renderProjectiles(SideScrollerGame.batch);
        removeOutOfBoundsProjectiles();
    }

    public void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            projectile.boundingBox.x -= projectile.movementSpeed * delta;
        }
    }

    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }

    public void addNewProjectile() {
        TextureRegion playerProjectileTextureRegion = enemyAtlas.findRegion("skeleton-Destroyed");
        Projectile projectile = new Projectile(boundingBox.x - 8f, boundingBox.y, 10, 4, 10, playerProjectileTextureRegion);
        timeSinceLastShot = 0;
        projectiles.add(projectile);
    }

    public void removeOutOfBoundsProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.boundingBox.x < 0) {
                iterator.remove();
            }
        }
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
