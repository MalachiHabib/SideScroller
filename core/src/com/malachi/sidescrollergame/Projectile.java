package com.malachi.sidescrollergame;

import static com.malachi.sidescrollergame.GameScreen.WORLD_WIDTH;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {

    Rectangle boundingBox;
    float movementSpeed;
    TextureRegion textureRegion;

    public Projectile(float xPos, float yPos, float width, float height, float movementSpeed, TextureRegion textureRegion) {
        this.boundingBox = new Rectangle(xPos, yPos, width, height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        System.out.print(textureRegion);
        batch.draw(textureRegion, boundingBox.x - boundingBox.width / 2, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
