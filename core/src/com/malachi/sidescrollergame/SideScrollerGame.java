package com.malachi.sidescrollergame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class SideScrollerGame extends Game {

	public static SpriteBatch batch;
	public StartScreen startScreen;

	@Override
	public void create() {
		batch = new SpriteBatch();
		startScreen = new StartScreen(this);
		setScreen(startScreen);
	}

	@Override
	public void dispose() {
		startScreen.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		startScreen.resize(width, height);
	}
}
