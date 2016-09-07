package com.hdwatts.LostFractals.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hdwatts.LostFractals.DBManager;
import com.hdwatts.LostFractals.GDXGame;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

/**
 * Created by Dean Watts on 1/22/2015.
 */
public class MenuScreen implements Screen, InputProcessor {


    Texture mainBackground;
    Texture levelScreen;
    Texture ui_assets;
    Texture optionsScreen;
    TextureRegion rightOn;
    TextureRegion rightOff;
    TextureRegion leftOn;
    TextureRegion leftOff;
    TextureRegion goButton;
    int currScreen;
    BitmapFont font;
    ScreenManager sM;
    int levelPage;
    int levelPick;
    public final int HOMESCREEN = 0;
    public final int LEVELSELECT = 1;
    public final int OPTIONS = 2;

    private String usernameToTest;
    private String emailToTest;
    private String passwordToTest;

    public MenuScreen(ScreenManager sM) {
        mainBackground = GDXGame.aM.get("workingassets/menuscreen.png", Texture.class);
        ui_assets = GDXGame.aM.get("workingassets/ui_assets.png", Texture.class);
        levelScreen = GDXGame.aM.get("workingassets/levelScreen.png", Texture.class);
        optionsScreen = GDXGame.aM.get("workingassets/optionsScreen.png", Texture.class);
        rightOn = new TextureRegion(ui_assets, 128, 0, 64, 64);
        leftOn = new TextureRegion(ui_assets, 192, 0, 64, 64);
        rightOff = new TextureRegion(ui_assets, 128, 64, 64, 64);
        leftOff = new TextureRegion(ui_assets, 192, 64, 64, 64);
        goButton = new TextureRegion(ui_assets, 0, 128, 64, 64);
        levelPage = 0;
        currScreen = 0;
        this.sM = sM;
        font = new BitmapFont();
        levelPick = -1;

        usernameToTest = null;
        passwordToTest = null;
/*-----------------------------------------------------------------*/
/*-----------------------------------------------------------------*/
/*-----------------------------------------------------------------*/


    }

    @Override
    public void render(float delta) {
        GDXGame.ui.setProjectionMatrix(GDXGame.uiCamera.combined);
        GDXGame.ui.begin();
        switch (currScreen) {
            case HOMESCREEN:
                GDXGame.ui.draw(mainBackground, 0, 0);

                //if (GDXGame.dbManager.isLoggedIn) {
//                    String username = GDXGame.dbManager.username;
//                    float b = 2;
//                    float c = 16;
//                    while (username.length() * c > 100) {
//                        b = b - .1f;
//                        c = (16 * b) / 2;
//                    }
//                    font.setScale(b);
//                    font.draw(GDXGame.ui, GDXGame.dbManager.username, 30, 70);
//                } else {
                    font.setScale(2);
                    font.draw(GDXGame.ui, "LOGIN", 30, 70);
//                }
                font.setScale(1);
                break;
            case LEVELSELECT:
                GDXGame.ui.draw(levelScreen, 0, 0);
                renderLevelSelect();
                break;
            case OPTIONS:
                GDXGame.ui.draw(optionsScreen, 0, 0);
                font.setColor(Color.WHITE);
                font.setScale(2);
                font.draw(GDXGame.ui, "Delete", 600, 125);
                break;
        }
        GDXGame.ui.end();
    }

    public void renderLevelSelect() {
        int numX = 64;
        int numY = 245;
        font.setScale(2);
        font.setColor(Color.WHITE);
        if (levelPick == -1) {
            font.draw(GDXGame.ui, "Select A Level", 144, 400);
        } else {
            font.draw(GDXGame.ui, "Hole: " + levelPick, 144, 400);
            font.draw(GDXGame.ui, "Score: " + (sM.fM.getScore(levelPick) == -1 ? "Incomplete" : sM.fM.getScore(levelPick)), 514, 464);
            //try {
                //font.draw(GDXGame.ui, "Par: " + GDXGame.dbManager.getAverageScore(levelPick), 514, 464 - 48);
            //} catch (SQLException e) {
                font.draw(GDXGame.ui, "Par: Calculating...", 514, 464 - 48);
           // }
            GDXGame.ui.draw(goButton, 514, 464 - 145);
            font.draw(GDXGame.ui, "Go!", 523, 464 - 100);
        }


        font.setColor(new Color(102 / 255f, 187 / 255f, 106 / 255f, 1f));
        for (int a = 0; a < 11; a++) {
            for (int b = 0; b < 3; b++) {
                if (sM.fM.currLevel < (a + (11 * b) + 1 + (33 * levelPage))) {
                    //System.out.println(sM.fM.currLevel + " " + (a + (11 * b) + 1 + (33 * levelPage)));
                    font.setColor(Color.BLACK);
                } else {
                    font.setColor(new Color(102 / 255f, 187 / 255f, 106 / 255f, 1f));
                }

                int strLength = ("" + (a + (11 * b) + 1 + (33 * levelPage))).length();
                if ((a + (11 * b) + 1 + (33 * levelPage)) >= 1000) {
                    font.setScale(1);
                    strLength = 0;
                } else {
                    font.setScale(2);
                }
                font.draw(GDXGame.ui, "" + (a + (11 * b) + 1 + (33 * levelPage)), numX + (64 * a) - (strLength / 2 * 8), numY - (64 * b));
            }
        }
        GDXGame.ui.draw(rightOn, 743, 136);
        if (levelPage == 0) {
            GDXGame.ui.draw(leftOff, -2, 136);
        } else {
            GDXGame.ui.draw(leftOn, -2, 136);
        }

    }

    public void handleCreate(){
        System.out.println("Creating!");
        Gdx.input.getTextInput(new Input.TextInputListener() {
            public void input(String text) {
                emailToTest = text;

                Gdx.input.getTextInput(new Input.TextInputListener() {
                 @Override
                public void input(String text) {
                    usernameToTest = text;

                    Gdx.input.getTextInput(new Input.TextInputListener() {
                        @Override
                        public void input(String text) {
                            passwordToTest = text;

                            if (usernameToTest != null && passwordToTest != null && emailToTest !=null) {
                                //try {
                                    //if (!GDXGame.dbManager.addUser(usernameToTest, passwordToTest, emailToTest)) {
                                    //    System.out.println("Username or Password has been used before");
                                    //    new Dialog("Username or Password has been used before", new Skin(Gdx.files.internal("uiskin.json")));
                                    //}else{
                                    //    GDXGame.dbManager.login(usernameToTest,passwordToTest);
                                    //}
                                //} catch (SQLException e) {
                                //    System.out.println(e.toString());
                                //}
                            }


                            usernameToTest = null;
                            passwordToTest = null;
                            emailToTest = null;
                        }

                        @Override
                        public void canceled() {
                            usernameToTest = null;
                            passwordToTest = null;
                            emailToTest = null;
                        }
                    }, "Password", "");
                }

                @Override
                public void canceled() {
                    usernameToTest = null;
                    emailToTest = null;
                }
            }, "Username", "");}

            public void canceled(){
                emailToTest = null;
            }

        }, "Email","");
    }

    public void handleLogin(){

        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                usernameToTest = text;

                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        passwordToTest = text;

                        if (usernameToTest != null && passwordToTest != null) {
                            try {
                                if (!GDXGame.dbManager.login(usernameToTest, passwordToTest)) {
                                    System.out.println("Invalid Username or Password");
                                    new Dialog("Invalid Username or Password", new Skin(Gdx.files.internal("uiskin.json")));
                                }
                            } catch (SQLException e) {
                                System.out.println(e.toString());
                            }
                        }


                        usernameToTest = null;
                        passwordToTest = null;

                    }

                    @Override
                    public void canceled() {
                        usernameToTest = null;
                        passwordToTest = null;
                    }
                }, "Password", "");
            }

            @Override
            public void canceled() {
                usernameToTest = null;
            }
        }, "Username", "");

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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }


    public boolean checkCollisions(float x, float y, OrthographicCamera camera, Viewport vp) {


        //y = camera.viewportHeight - y;
        float gutterLeft = vp.getLeftGutterWidth();
        float gutterRight = vp.getRightGutterWidth();
        float gutterNorth = vp.getTopGutterHeight();
        float gutterSouth = vp.getBottomGutterHeight();


        if (!(x > gutterLeft && x < vp.getScreenWidth() + gutterRight) || !(y > gutterSouth && y < vp.getScreenHeight() + gutterNorth)) {
            return false;
        }


        //x -= gutterLeft;
        //y -= gutterNorth;

        Vector3 proj = vp.unproject(new Vector3(x, y, 0));
        switch (currScreen) {
            case HOMESCREEN:
                if (proj.x <= 170 + 600 && proj.y <= 47 + 97 && proj.x >= 600 && proj.y >= 97) {
                    currScreen = LEVELSELECT;
                    return true;
                }
                if (proj.x <= 723 + 45 && proj.y <= 76 && proj.x >= 723 && proj.y >= 32) {
                    currScreen = OPTIONS;
                    return true;
                }

                if (proj.x >= 21 && proj.x <= 131 && proj.y <= 76 && proj.y >= 32) {
                    handleLogin();
                    return true;
                }

                if (proj.x >= 150 && proj.x <= 260 && proj.y <= 76 && proj.y >= 32) {
                    handleCreate();
                    return true;
                }
                break;
            case LEVELSELECT:

                if (proj.x < 66 && proj.y > 420) {
                    currScreen = HOMESCREEN;
                    return true;
                }

                if (proj.x > 520 && proj.y > 325 && proj.x < 570 && proj.y < 374) {
                    //sM.setLevel(levelPick);
                    sM.addGameScreen(levelPick);
                    return true;
                }

                if (proj.x > 49 + (64 * 11) && proj.y <= 263 && proj.y >= 263 - (64 * 3)) {
                    levelPage++;
                    return true;
                }

                if (proj.x < 49 && proj.y <= 263 && proj.y >= 263 - (64 * 3)) {
                    if (levelPage > 0) {
                        levelPage--;
                        return true;
                    }
                }

                if (proj.x >= 49 && proj.x <= 49 + (64 * 11) && proj.y <= 263 && proj.y >= 263 - (64 * 3)) {
                    int temp = levelPick;
                    levelPick = 1 + (int) ((proj.x - 49) / 64) + ((int) ((proj.y - 263) / 64) * -11) + (33 * levelPage);
                    if (levelPick > sM.fM.currLevel) {
                        levelPick = temp;
                        return false;
                    }
                    return true;
                }
                //if (proj.x <= 170 + 600 && proj.y <= 47 + 97 && proj.x >= 600 && proj.y >= 97) {
                //sM.changeGameScreen(sM.GAMESCREEN);
                //return true;
                //}
                break;
            case OPTIONS:
                if (proj.x < 66 && proj.y > 420) {
                    currScreen = HOMESCREEN;
                    return true;
                }
                if (proj.x >= 558 && proj.y <= 135 && proj.x <= 750 && proj.y >= 88) {
                    sM.fM.delete();
                }
                break;
        }


        /*if(proj.x >= vp.getWorldWidth() - zoomButton.getRegionWidth() && proj.y <= zoomButton.getRegionHeight()){
            return true;
        }*/
        return false;

    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        checkCollisions(screenX, screenY, GDXGame.uiCamera, GDXGame.uiViewport);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
