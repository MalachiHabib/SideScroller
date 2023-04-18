package com.malachi.sidescrollergame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class SideScrollerGame extends Game {

	public static SpriteBatch batch;
	public GameScreen gameScreen;

	public static Random random = new Random();
	@Override
	public void create() {
		batch = new SpriteBatch();
		gameScreen = new GameScreen();
		setScreen(gameScreen);
	}


	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}
