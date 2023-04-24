package com.malachi.sidescrollergame.character;

public abstract class Enemy extends Character {
    public Enemy(float movementSpeed, float width, float height,
                 float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        state = States.State.MOVING;
        stateTime = 0;
    }

    // State management
    public void setCurrentState(States.State newState) {
        if (state != newState) {
            stateTime = 0;
        }
        state = newState;
    }

    public States.State getState() {
        return state;
    }

    // Movement
    @Override
    public void teleport(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, yChange);
    }
}