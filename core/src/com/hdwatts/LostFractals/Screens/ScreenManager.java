package com.hdwatts.LostFractals.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.hdwatts.LostFractals.FileManager;

import java.util.ArrayList;

/**
 * Created by Dean Watts on 1/22/2015.
 */
public class ScreenManager {

    ArrayList<Screen> screens;
    int currScreen;
    public FileManager fM;
    public int LOADINGSCREEN = 0;
    public int MENUSCREEN = 1;
    public int GAMESCREEN = 2;
    public boolean isLoaded;
    public int startLevel;


    public ScreenManager(){
        isLoaded = false;
        screens = new ArrayList<Screen>();
        screens.add(new LoadingScreen());
        LOADINGSCREEN = screens.size() - 1;
        currScreen = LOADINGSCREEN;
        fM = new FileManager();
    }

    public void addMenuScreen(){
        screens.add(new MenuScreen(this));
        MENUSCREEN = screens.size() - 1;
        currScreen = MENUSCREEN;
        changeGameScreen(currScreen);
    }
    public void addGameScreen(int level){
        startLevel = level;
        screens.add(new GameScreen(this));
        GAMESCREEN = screens.size() - 1;
        currScreen = GAMESCREEN;
        changeGameScreen(currScreen);
    }


    public void changeGameScreen(int screen){

        Gdx.input.setInputProcessor((InputProcessor)screens.get(screen));
    }

    public void render(float delta){
        screens.get(currScreen).render(delta);

    }


}
