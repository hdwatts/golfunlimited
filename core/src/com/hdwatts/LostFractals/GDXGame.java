package com.hdwatts.LostFractals;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hdwatts.LostFractals.Entities.Particle;
import com.hdwatts.LostFractals.Entities.Player;
import com.hdwatts.LostFractals.Entities.PlayerPreview;
import com.hdwatts.LostFractals.Screens.GameScreen;
import com.hdwatts.LostFractals.Screens.ScreenManager;
import com.hdwatts.LostFractals.TileMaps.*;
import com.hdwatts.LostFractals.TweenAccessors.CameraAccessor;
import com.hdwatts.LostFractals.TweenAccessors.ParticleAccessor;

public class GDXGame extends ApplicationAdapter implements InputProcessor {
	private static boolean showFps = false;
    private static boolean showBox2dDebug = false;

    public static void setShowFps(boolean showFps) {
        GDXGame.showFps = showFps;
    }

    public static boolean isShowFps() {
        return showFps;
    }

    public static boolean isShowBox2dDebug(){
        return showBox2dDebug;
    }

    public static void setShowBox2dDebug(boolean showBox2dDebug){
        GDXGame.showBox2dDebug = showBox2dDebug;
    }


    //Texture tiles;
    //Texture ball;
    //TextureRegion flagBackground;
    //Texture flag;
    //Texture background;
    //Texture ui_assets;
    public static OrthographicCamera camera;
    public static Viewport viewport;
    public static OrthographicCamera uiCamera;
    public static Viewport uiViewport;
    public static TweenManager tManager;
    public static DBManager dbManager;
    //MapBodyManager mBm;
    public static SpriteBatch batch;
    public static SpriteBatch ui;
    //Box2DDebugRenderer debugRenderer;
    ScreenManager sM;
    public static ParticleManager pManager;
    public static AssetManager aM;

    @Override
	public void create () {
        aM = new AssetManager();
        aM.load("workingassets/golftiles.png", Texture.class);
        aM.load("workingassets/golfball.png", Texture.class);
        aM.load("workingassets/flag.png", Texture.class);
        aM.load("workingassets/background_back.png", Texture.class);
        aM.load("workingassets/background_layer1.png", Texture.class);
        aM.load("workingassets/background_layer2.png", Texture.class);
        aM.load("workingassets/background_layer3.png", Texture.class);
        aM.load("workingassets/ui_assets.png", Texture.class);
        aM.load("workingassets/menuscreen.png", Texture.class);
        aM.load("workingassets/optionsScreen.png", Texture.class);
        aM.load("workingassets/levelScreen.png", Texture.class);

        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        Tween.registerAccessor(Particle.class, new ParticleAccessor());
        tManager = new TweenManager();
        pManager = new ParticleManager();

        camera = new OrthographicCamera();
        //camera.setToOrtho(false, 800, 480);
        //camera.update();
        viewport = new FitViewport(800, 480, camera);
        viewport.apply(true);

        uiCamera = new OrthographicCamera();
        //uiCamera.setToOrtho(false, 800, 480);
        //uiCamera.update();
        uiViewport = new FitViewport(800, 480, uiCamera);
        uiViewport.apply();
        uiCamera.translate(uiCamera.viewportWidth / 2, uiCamera.viewportHeight / 2);
        uiCamera.update();

        boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();
        if(!isLocAvailable){
            System.out.println("No storage available!");
        }

        //map = mG.generateMap();

        //tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
        batch = new SpriteBatch();
        ui= new SpriteBatch();
        sM = new ScreenManager();
        //dbManager = new DBManager(sM);

	}

	@Override
	public void render () {
        Gdx.gl.glClearColor(0,0,0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(aM.update() && !sM.isLoaded){
            sM.isLoaded = true;
            sM.addMenuScreen();
        }
        sM.render(Gdx.graphics.getDeltaTime());
        tManager.update(Gdx.graphics.getDeltaTime());

        //batch.setTransformMatrix(camera.view);

        //Matrix4 debugMatrix = new Matrix4(camera.combined);
        //debugMatrix.scale(PHYS_RATIO, PHYS_RATIO, 1f);
        //debugRenderer.render(world, debugMatrix);

    }




    @Override
    public boolean keyDown(int keycode) {
/*
        Vector2 pos = player.getBody().getPosition();
        if(keycode == Input.Keys.D) {
            camera.translate(64,0);
        }
        if(keycode == Input.Keys.A)
            camera.translate(-64,0);

        if(keycode == Input.Keys.W)
            camera.translate(0,64);
            //player.getBody().applyForceToCenter(0f,10f,true);
        if(keycode == Input.Keys.S)
            camera.translate(0,-64);
            //player.getBody().applyForceToCenter(0f, -10f, true);
        if(keycode == Input.Keys.SPACE) {
            //System.out.println("Speeding up! " + player.isGrounded);
            worldStep = 1 / 120f;
        }
*/

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
/*

        if(keycode == Input.Keys.NUM_1) {
            resetMap();
        }
        if(keycode == Input.Keys.NUM_2) {
            //  tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        }
        if(keycode == Input.Keys.SPACE){
            worldStep = 1/60f;
        }
*/
        return false;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
/*
        //System.out.println(player.getBody().getLinearVelocity().x + " " + player.getBody().getLinearVelocity().y +" "+  player.isGrounded);
        // if(screenX == )
        if(button == Input.Buttons.LEFT) {
            if (!uiManager.isMap) {
                if (!uiManager.checkCollisions(screenX, screenY, uiCamera, uiViewport, player.getBody().isAwake())) {
                    if (!player.getBody().isAwake()) {
                        player.isHitting = true;
                        player.startHit = new Vector2(screenX, screenY);
                    }
                }
            }else{
                uiManager.isMap = false;
            }

        }

        if(button == Input.Buttons.RIGHT){
            if(player.isHitting){
                player.isHitting = false;

            }
        }*/
        //player.getBody().applyForce(1f,1f,screenX,screenY,true);

        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
/*
        if(player.isHitting){
            hitBall();
        }
*/
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
/*
        if(player.isHitting){
            drawProj.x = (player.startHit.x - screenX) / PHYS_RATIO;
            drawProj.y = (player.startHit.y - screenY) / PHYS_RATIO;
        }
        if(uiManager.isZoom){
            int x = (int)(uiViewport.unproject(new Vector3(screenX,screenY,0)).y / 30);
            float x2 = x / 10f;

            x2 = Math.min(1+x2, 2.6f);


            Tween.to(camera, CameraAccessor.ZOOM, .1f)
                    .target((x2))
                    .start(tManager);
        }
*/
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
/*
        if(amount > 0){
            camera.zoom += .1f;
        }
        if(amount < 0){
            camera.zoom -= .1f;
        }
*/
        return false;
    }
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width,height);
    }

}
