package com.malachi.sidescrollergame;

public abstract class Enemy extends Character {
    State state;
    float stateTime;

    public Enemy(float movementSpeed, float width, float height,
                 float posX, float posY, float timeBetweenShots) {
        super(movementSpeed, width, height, posX, posY, timeBetweenShots);
        state = State.MOVING;
        stateTime = 0;
    }

    // State management
    public void setCurrentState(State newState) {
        if (state != newState) {
            stateTime = 0;
        }
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