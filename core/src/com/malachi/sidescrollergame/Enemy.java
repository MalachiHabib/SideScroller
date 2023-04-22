package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Enemy extends Character {
    private State state;
    private final List<Projectile> projectiles;

    public Enemy(float movementSpeed, float width, float height,
                 float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        projectiles = new ArrayList<>();
        state = State.MOVING;
    }

    // State management
    public void setCurrentState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }

    // Projectiles
    public List<Projectile> getProjectiles() {
        return projectiles;
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
            projectile.boundingBox.x -= projectile.movementSpeed * delta; // Move the projectile to the left
        }
    }

    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }

    public void addNewProjectile() {
        TextureRegion originalRegion = new TextureRegion(new Texture("projectiles/11.png"));
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

    // Movement
    @Override
    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);
    }
}
