package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShootingEnemy extends Enemy {
    private State state;
    private float stateTime;
    private final TextureAtlas enemyAtlas = new TextureAtlas("enemyAtlas.atlas");
    private final TextureAtlas projectileAtlas = new TextureAtlas("Projectiles.atlas");
    private final List<Projectile> projectiles;

    public ShootingEnemy(float movementSpeed, float width, float height,
                         float posX, float posY, float timeBetweenShots){
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        stateTime = 0;
        projectiles = new ArrayList<>();
        state = State.MOVING;
        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        System.out.println(boundingBox.y);
        boundingBox.x -= speed * delta;
        stateTime += delta;

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

    public void setCurrentState(State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

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
//            projectile.boundingBox.x = boundingBox.x + 1f; // Update the projectile's x position based on the enemy's x position
//            projectile.boundingBox.y = boundingBox.y + 3.5f; // Update the projectile's y position based on the enemy's y position (optional)
            projectile.boundingBox.x -= projectile.movementSpeed * delta; // Move the projectile to the left
        }
    }


    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }

    public void addNewProjectile() {
        TextureAtlas.AtlasRegion originalRegion = projectileAtlas.findRegion("11");
        TextureRegion flippedProjectileRegion = new TextureRegion(originalRegion);
        flippedProjectileRegion.flip(true, false);

        Projectile projectile = new Projectile(boundingBox.x + 1f, boundingBox.y + 5f, 4f, 1.5f, speed * 3f, flippedProjectileRegion);
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
    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);
    }
}

