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


    // Movement
    @Override
    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);
    }
}
