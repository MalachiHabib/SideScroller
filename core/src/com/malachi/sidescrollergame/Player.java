package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Character {
    private State state;
    private float stateTime;
    private final TextureAtlas playerAtlas = new TextureAtlas("playerAtlas.atlas");

    //projectiles
    private final List<Projectile> projectiles;

    public Player(float movementSpeed, float width, float height,
                  float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        idleAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-MovingNIdle"), Animation.PlayMode.LOOP);
        dieAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-Destroy"), Animation.PlayMode.LOOP);
        stateTime = 0;
        projectiles = new ArrayList<>();
        state = State.IDLE;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
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
    public void fireProjectile(boolean pressedShoot, float delta) {
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || pressedShoot) && canShoot()) {
            addNewProjectile();
        }
        updateProjectiles(delta);
        renderProjectiles(SideScrollerGame.batch);
        removeOutOfBoundsProjectiles(WORLD_WIDTH);
    }

    public void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            projectile.boundingBox.x += projectile.movementSpeed * delta;
        }
    }

    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }

    public void addNewProjectile() {
        TextureRegion playerProjectileTextureRegion = playerAtlas.findRegion("skeleton-MovingNIdle");
        Projectile projectile = new Projectile(boundingBox.x + 8f, boundingBox.y, 10, 4, 10, playerProjectileTextureRegion);
        timeSinceLastShot = 0;
        projectiles.add(projectile);
    }

    public void removeOutOfBoundsProjectiles(float worldWidth) {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.boundingBox.x > worldWidth) {
                iterator.remove();
            }
        }
    }
}

