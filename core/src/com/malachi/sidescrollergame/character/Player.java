package com.malachi.sidescrollergame.character;

import static com.malachi.sidescrollergame.screens.GameScreen.WORLD_HEIGHT;
import static com.malachi.sidescrollergame.screens.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.malachi.sidescrollergame.util.OnScreenController;
import com.malachi.sidescrollergame.util.ScreenShake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Character {
    float score = 0;
    private final TextureAtlas playerAtlas = new TextureAtlas("CharacterAssets/PlayerAtlas.atlas");
    private final TextureAtlas projectileAtlas = new TextureAtlas("CharacterAssets/Projectiles.atlas");
    private final List<Projectile> projectiles;
    private float dashDistance = 15f;
    private float dashCoolDown = 3f;
    private float timeSinceLastDash = 0;
    private States.State state;
    private float stateTime;
    private Sound fireSound, death, dash;
    private boolean hasPlayedDeathSound = false;

    public Player(float movementSpeed, float width, float height,
                  float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        idleAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-MovingNIdle"), Animation.PlayMode.LOOP);
        dieAnimation = new Animation<TextureRegion>(0.1f, playerAtlas.findRegions("skeleton-Destroy"), Animation.PlayMode.LOOP);
        stateTime = 0;
        projectiles = new ArrayList<>();
        state = States.State.IDLE;
        fireSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/rlaunch.wav"));
        death = Gdx.audio.newSound(Gdx.files.internal("Sounds/explosion07.wav"));
        dash = Gdx.audio.newSound(Gdx.files.internal("Sounds/swoosh.wav"));
    }


    public boolean isAnimationFinished() {
        return dieAnimation.isAnimationFinished(stateTime);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public States.State getState() {
        return state;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float newScore) {
        score += newScore;
    }


    public void setCurrentState(States.State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

    @Override
    public void teleport(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime += delta;

        Animation<TextureRegion> currentAnimation;
        switch (state) {
            case DIED:
                if (!hasPlayedDeathSound) {
                    death.play();
                    hasPlayedDeathSound = true;
                }
                currentAnimation = dieAnimation;
                break;
            default:
                currentAnimation = idleAnimation;
                break;
        }
        characterTexture = currentAnimation.getKeyFrame(stateTime);
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

    public boolean dashWithScreenShake(float xMovement, float yMovement) {
        if (timeSinceLastDash >= dashCoolDown) {
            dash(xMovement, yMovement);
            timeSinceLastDash = 0;
            return true;
        }
        return false;
    }


    public void addNewProjectile() {
        fireSound.play(0.2f);
        Projectile projectile = new Projectile(boundingBox.x + 11f, boundingBox.y + .5f, 6, 2, speed * 2.5f, projectileAtlas.findRegion("04"));
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

    /**
     * Performs a quick dash in the specified direction.
     *
     * @param xMovement Horizontal movement factor.
     * @param yMovement Vertical movement factor.
     *                  The method checks if the dash cooldown has passed, calculates the new position for the character
     *                  considering world boundaries, and updates the character's bounding box accordingly.
     */
    public void dash(float xMovement, float yMovement) {
        dash.play();
        if (timeSinceLastDash >= dashCoolDown) {
            float dashX = xMovement * dashDistance;
            float dashY = yMovement * dashDistance;
            float newX = boundingBox.x + dashX;
            float newY = boundingBox.y + dashY;

            // Check boundaries
            newX = Math.min(Math.max(newX, 0), WORLD_WIDTH - boundingBox.width);
            newY = Math.min(Math.max(newY, 0), WORLD_HEIGHT - boundingBox.height);

            boundingBox.setPosition(newX, newY);
            timeSinceLastDash = 0;
        }
    }

    /**
     * @param delta              Time since the last frame in seconds.
     * @param onScreenController The on-screen controller object for Android input.
     * @brief Basic input detect for player movement and ability
     * Checks for input via keyboard or onscreen controller
     * Calculates the players position based on the input, respecting the world boundaries
     * Updates the players position
     **/
    public void detectInput(float delta, OnScreenController onScreenController, ScreenShake screenShake) {
        float leftBoundary = -boundingBox.x;
        float bottomBoundary = -boundingBox.y;
        float rightBoundary = (float) WORLD_WIDTH / 2 - boundingBox.x - boundingBox.width;
        float topBoundary = WORLD_HEIGHT - boundingBox.y - boundingBox.height;

        float xMovement = 0f, yMovement = 0f;

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Vector2 movement = onScreenController.getPlayerControlInput(delta, this);
            xMovement = movement.x;
            yMovement = movement.y;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                xMovement = speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                xMovement = -speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                yMovement = speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                yMovement = -speed * delta;
        }

        xMovement = (xMovement > 0) ? Math.min(xMovement, rightBoundary) : Math.max(xMovement, leftBoundary);
        yMovement = (yMovement > 0) ? Math.min(yMovement, topBoundary) : Math.max(yMovement, bottomBoundary);

        teleport(xMovement, yMovement);

        timeSinceLastDash += delta;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (Math.abs(xMovement) > 0 || Math.abs(yMovement) > 0) {
                if (dashWithScreenShake(xMovement / (speed * delta), yMovement / (speed * delta))) {
                    screenShake.reset();
                }
            }
        }
    }
}