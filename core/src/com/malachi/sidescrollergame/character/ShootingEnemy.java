package com.malachi.sidescrollergame.character;

import static com.malachi.sidescrollergame.screens.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.malachi.sidescrollergame.SideScrollerGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShootingEnemy extends Enemy {
    private final TextureAtlas enemyAtlas = new TextureAtlas("CharacterAssets/enemyAtlas.atlas");
    private final TextureAtlas projectileAtlas = new TextureAtlas("CharacterAssets/Projectiles.atlas");
    private final List<Projectile> projectiles;
    private Sound fireSound, death;
    private boolean hasPlayedDeathSound = false;
    public ShootingEnemy(float movementSpeed, float width, float height,
                         float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        projectiles = new ArrayList<>();
        state = States.State.MOVING;
        dieAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Destroyed"), Animation.PlayMode.LOOP);
        movingAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Moving"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<TextureRegion>(0.1f, enemyAtlas.findRegions("skeleton-Idle"), Animation.PlayMode.LOOP);
        fireSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/flaunch.wav"));
        death = Gdx.audio.newSound(Gdx.files.internal("Sounds/explosion01.wav"));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        boundingBox.x -= speed * delta;
        stateTime += delta;

        if (boundingBox.x < -10f) {
            teleport(WORLD_WIDTH + (float) (20 + Math.random() * 25), (int) (Math.random() * 43 + 15));
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
                    teleport(WORLD_WIDTH - boundingBox.x + (float) (Math.random() * 40), (int) (Math.random() * 43 + 15));
                }
                break;
            default:
                currentAnimation = movingAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
    }


    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void fireProjectile(float delta) {

        timeSinceLastShot += delta;
        if (timeSinceLastShot >= timeBetweenShots && canShoot() && state != States.State.DIED && boundingBox.x < WORLD_WIDTH) {
            fireSound.play(0.2f);
            addNewProjectile();
        }
        updateProjectiles(delta);
        renderProjectiles(SideScrollerGame.batch);
        removeOutOfBoundsProjectiles();
    }

    public void updateProjectiles(float delta) {
        for (Projectile projectile : projectiles) {
            projectile.getBoundingBox().x -= projectile.getSpeed() * delta; // Move the projectile to the left
        }
    }

    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }

    public void addNewProjectile() {
        TextureRegion projectileTexture = projectileAtlas.findRegion("11");
        projectileTexture.flip(true, false);

        Projectile projectile = new Projectile(boundingBox.x + 1f, boundingBox.y + 5f, 4f, 1.5f, speed * 3f, projectileTexture);
        timeSinceLastShot = 0;
        projectiles.add(projectile);
    }

    public void removeOutOfBoundsProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.getBoundingBox().x < 0) {
                iterator.remove();
            }
        }
    }
}