package com.hdwatts.LostFractals.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hdwatts.LostFractals.GDXGame;

/**
 * Created by Dean Watts on 1/22/2015.
 */
public class LoadingScreen implements Screen {

    int progress;
    BitmapFont font;
    SpriteBatch ui;

    public LoadingScreen(){
        progress = 0;
        font = new BitmapFont();
        ui = GDXGame.ui;
    }

    @Override
    public void render(float delta) {
        ui.begin();
            font.setScale(5);
            font.draw(ui, "Loading: "+GDXGame.aM.getProgress()*100+"%", (Gdx.graphics.getWidth()/2) - (40*6), Gdx.graphics.getHeight()/2);
        ui.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
