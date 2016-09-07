package com.hdwatts.LostFractals.Screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hdwatts.LostFractals.Entities.Particle;
import com.hdwatts.LostFractals.Entities.Player;
import com.hdwatts.LostFractals.Entities.PlayerPreview;
import com.hdwatts.LostFractals.GDXGame;
import com.hdwatts.LostFractals.GameCollision;
import com.hdwatts.LostFractals.ParticleManager;
import com.hdwatts.LostFractals.TileMaps.MapGenerator;
import com.hdwatts.LostFractals.TileMaps.MapManager;
import com.hdwatts.LostFractals.TweenAccessors.CameraAccessor;
import com.hdwatts.LostFractals.TweenAccessors.ParticleAccessor;
import com.hdwatts.LostFractals.UIManager;

/**
 * Created by Dean Watts on 1/22/2015.
 */
public class GameScreen implements Screen, InputProcessor {
    float PHYS_RATIO = 32;
    float W;
    float H;
    final float GAMEWORLD_WIDTH = 12.5f;
    final float GAMEWORLD_HEIGHT = 7.5f;

    OrthographicCamera camera;
    OrthographicCamera uiCamera;
    MapManager mapMan;
    MapGenerator mG;
    int currLevel;
    World world;
    float worldStep;
    Vector2 drawProj;
    Player player;
    SpriteBatch batch;
    SpriteBatch ui;
    Viewport viewport;
    Viewport uiViewport;
    UIManager uiManager;
    Texture tiles;
    Texture ui_assets;
    Texture flag;
    Texture background;
    Texture background3;
    Texture background2;
    Texture background1;
    Texture laserBackground;
    Texture laserOverlay;
    Texture ball;
    BitmapFont font;
    TextureRegion flagBackground;
    TweenManager tManager;
    ParticleManager pManager;
    ScreenManager sM;


    public GameScreen(ScreenManager sM){
        W = Gdx.graphics.getWidth();
        H = Gdx.graphics.getHeight();
        this.sM = sM;
        tiles = GDXGame.aM.get("workingassets/golftiles.png", Texture.class);
        ui_assets = GDXGame.aM.get("workingassets/ui_assets.png", Texture.class);
        ball = GDXGame.aM.get("workingassets/golfball.png", Texture.class);
        flag = GDXGame.aM.get("workingassets/flag.png", Texture.class);
        background = GDXGame.aM.get("workingassets/background_back.png", Texture.class);
        background3 = GDXGame.aM.get("workingassets/background_layer3.png", Texture.class);
        background2 = GDXGame.aM.get("workingassets/background_layer2.png", Texture.class);
        background1 = GDXGame.aM.get("workingassets/background_layer1.png", Texture.class);


        flagBackground = new TextureRegion(ui_assets, 0, 64 ,64 ,64);


        font = new BitmapFont();
        //float aspectRatio = W/H;

        mG = new MapGenerator(tiles);

        this.camera = GDXGame.camera;
        this.viewport = GDXGame.viewport;
        this.uiViewport = GDXGame.uiViewport;
        this.uiCamera = GDXGame.uiCamera;
        this.batch = GDXGame.batch;
        this.ui = GDXGame.ui;

        drawProj = new Vector2(0,0);

        world = new World(new Vector2(0,-10), true);
        world.setContactListener(new GameCollision());
        //debugRenderer = new Box2DDebugRenderer();
        worldStep = 1/60f;

        currLevel = sM.startLevel;
        uiManager = new UIManager(ui_assets, font);
        long x = sM.fM.getSeed(sM.startLevel);
        int h = sM.fM.getHeight(sM.startLevel);
        mapMan = new MapManager(mG, world, PHYS_RATIO, x, h, sM.fM);
        //long time = System.currentTimeMillis();
        //for(int b = 0; b < 1000; b++)
        //  mapMan.nextMap();
        //time = System.currentTimeMillis() - time;
        //System.out.println("LOADED IN: "+time);

        player = new Player(world, (mapMan.lastHole.x * 64)+(32), ((mapMan.lastHole.y + 1) * 64));

        this.tManager = GDXGame.tManager;
        this.pManager = GDXGame.pManager;

    }

    @Override
    public void render(float delta) {
        Vector2 pos = this.player.getBody().getPosition();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.player.getBody().applyLinearImpulse(-1f/PHYS_RATIO, 0, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.player.getBody().applyLinearImpulse(1f/PHYS_RATIO, 0, pos.x, pos.y, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            //  this.player.getBody().applyLinearImpulse(0f, 1f/PHYS_RATIO, pos.x, pos.y, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            //this.player.getBody().applyLinearImpulse(0f, -25f, pos.x, pos.y, true);
        }

        if(Math.abs(player.getBody().getLinearVelocity().x) < 1 && Math.abs(player.getBody().getLinearVelocity().y) < 1 && player.isGrounded > 0){

            player.timeSlow += Gdx.graphics.getDeltaTime();
            //System.out.println(player.timeSlow);
            if(player.timeSlow > player.timeStop) {
                player.getBody().setLinearVelocity(0, 0);
                player.getBody().setAngularVelocity(0);
                player.getBody().setAwake(false);
                player.getBody().setActive(false);
                //System.out.println("Last Stop: "+player.lastStop.x + " " +player.lastStop.y);
            }
        }else if(player.timeSlow != 0){
            player.timeSlow = 0;
        }
        if(!player.getBody().isAwake() && player.inHole){
            resetMap();
            player.inHole = false;
        }

        if(player.getBody().getPosition().y < -5){
            resetBall();
        }

        doPhysicsStep(Gdx.graphics.getDeltaTime(), null);
        //world.step(1/60f, 6, 2);

        cameraUpdate();

        camera.update();
        float cameraX = camera.position.x - (camera.viewportWidth)/2;
        float cameraY = camera.position.y - (camera.viewportHeight)/2;
        ui.begin();
        ui.draw(background, 0, 0);
        ui.end();

        float zoom = camera.zoom;
        camera.zoom = 1f;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background3, camera.viewportWidth/2 + camera.position.x + -(cameraX / 4f) % (800*3), cameraY);
        batch.draw(background3, camera.viewportWidth/2 + camera.position.x + -(cameraX / 4f + 800) % (800*3), cameraY);
        batch.draw(background3, camera.viewportWidth/2 + camera.position.x + -(cameraX / 4f - 800) % (800*3), cameraY);
        batch.draw(background2, camera.viewportWidth/2 + camera.position.x + -(cameraX / 3f) % (800*3), cameraY);
        batch.draw(background2, camera.viewportWidth/2 + camera.position.x + -(cameraX / 3f + (800)) % (800*3), cameraY);
        batch.draw(background2, camera.viewportWidth/2 + camera.position.x + -(cameraX / 3f - 800) % (800*3), cameraY);
        batch.draw(background1, camera.viewportWidth/2 + camera.position.x + -(cameraX / 2.5f) % (800*2f), cameraY);
        batch.draw(background1, camera.viewportWidth/2 + camera.position.x + -(cameraX / 2.5f + 800) % (800 * 2f), cameraY);
        batch.draw(background1, camera.viewportWidth/2 + camera.position.x + -(cameraX / 2.5f - 800) % (800 * 2f), cameraY);

        batch.end();
        camera.zoom = zoom;
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        mapMan.render(camera);

        batch.begin();
        batch.setColor(Color.WHITE);

        batch.draw(ball, (player.getBody().getPosition().x ) * PHYS_RATIO - 6, (player.getBody().getPosition().y) * PHYS_RATIO - 6);
        batch.draw(flag, (mapMan.currHole.x * 64)+ 40, ((mapMan.currHole.y + 1) * 64));
        if(player.isHitting) {
            float a = 0;
            float b = 0;
            PlayerPreview preview = new PlayerPreview(world, player.getBody().getPosition().x, player.getBody().getPosition().y, drawProj.x, -1*drawProj.y);
            int c = preview.collides + 1;
            boolean firstRun = true;

            while(preview.collides < c && a < 3){
                doPhysicsStep(1/30f, preview);
                if(firstRun){
                    c = preview.collides + 1;
                    firstRun = false;
                }
                if(b % 3 == 0) {

                    batch.draw(ball, preview.getBody().getPosition().x * PHYS_RATIO - 6, (preview.getBody().getPosition().y * PHYS_RATIO - 6));
                }
                b++;
                a += 1/30f;
                world.clearForces();

            }

            //System.out.println(preview.collides);
            //System.out.println(drawProj.x + " " + drawProj.y);
            if(a<3)
                batch.draw(ball, preview.getBody().getPosition().x * PHYS_RATIO - 6, preview.getBody().getPosition().y * PHYS_RATIO - 6);
            preview.remove(world);
            //while(b < 2 && a < 3){
                /*if(!mapMan.testPreviewCollision(getProjX(a) * PHYS_RATIO -6, (getProjY(a) * PHYS_RATIO -6))){
                  //  b++;
                }else{
                    b = 0;
                }
                batch.draw(ball, getProjX(a) * PHYS_RATIO -6, (getProjY(a) * PHYS_RATIO -6));
                a += .1f;*/
            //}

        }
        //System.out.println(v.x+", "+ v.y);
        if(pManager.getParticles("hitBall")!=null){
            for(int p = 0; p < pManager.getParticles("hitBall").length; p++) {
                batch.draw(ball, pManager.getParticles("hitBall")[p].getX() * PHYS_RATIO - 6,  pManager.getParticles("hitBall")[p].getY() * PHYS_RATIO - 6, 3f, 3f);
            }
        }
        batch.end();
        //batch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        //batch.begin();
        //batch.setColor(Color.CYAN);
        //batch.draw(laserBackground, player.getBody().getPosition().x * PHYS_RATIO, player.getBody().getPosition().y * PHYS_RATIO - 12,0 ,0, 64, 128, 1, 1, 90, 0,0,64,64,false,false);
        //batch.draw(laserOverlay,  player.getBody().getPosition().x * PHYS_RATIO, player.getBody().getPosition().y * PHYS_RATIO - 12,0 ,0, 64, 128, 1, 1, 90, 0,0,64,64,false,false);
        //batch.end();

        ui.setProjectionMatrix(uiCamera.combined);
        ui.begin();
        font.setColor(Color.WHITE);

        //font.draw(ui, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        if(!player.getBody().isAwake() && !player.isHitting){
            font.draw(ui, "Ready to hit!", viewport.getWorldWidth()/2 - 48, 80);
        }
        font.draw(ui, "Strokes: "+player.score, viewport.getWorldWidth()/2 - 8*(("Strokes: "+player.score).length()/2), 40);
        font.draw(ui, "FPS: "+Gdx.graphics.getFramesPerSecond(), 10, 100);

        //int zoomText = (int)(camera.zoom * 100);
        //font.draw(ui, new String("Zoom: " + camera.zoom).substring(0,9), 10, 50);
        if(!camera.frustum.boundsInFrustum(mapMan.currHole.x * 64,((mapMan.currHole.y + 1) * 64),0,64f, 128f,0)){
            float flagButtonX = 0;
            float flagButtonY = 0;
            if(player.getBody().getPosition().x * PHYS_RATIO > (mapMan.currHole.x) * 64){
                flagButtonX = 10;
                flagButtonY = ((mapMan.currHole.y + 1) * 64);
            }else if(player.getBody().getPosition().x * PHYS_RATIO <= (mapMan.currHole.x) * 64){
                flagButtonX= viewport.getWorldWidth() - flagBackground.getRegionWidth() - 10;
                flagButtonY = ((mapMan.currHole.y + 1) * 64);
            }
            //Vector3 proj = new Vector3(flagButtonX, flagButtonY, 0);
            Vector3 proj = camera.project(new Vector3(flagButtonX, flagButtonY, 0));

            proj = uiCamera.unproject(proj);

            proj.y = uiCamera.viewportHeight - proj.y;
            //System.out.println("AFTER UNPROJECTION: "+proj.x + ", "+proj.y);

            if(player.getBody().getPosition().x * PHYS_RATIO - viewport.getWorldWidth()/2 < mapMan.currHole.x * 64 &&
                    player.getBody().getPosition().x * PHYS_RATIO + viewport.getWorldWidth()/2 > mapMan.currHole.x * 64){
                System.out.println("ABOVE AND BEYOND");
                flagButtonX = (mapMan.currHole.x) * 64;
                flagButtonY = 10;
                proj = camera.project(new Vector3(flagButtonX, flagButtonY, 0) );
                proj = uiCamera.unproject(proj);
                flagButtonX = Math.max(10, Math.min(proj.x, viewport.getWorldWidth() - flagBackground.getRegionWidth()));
                proj.y = 10;
            }


            ui.draw(flagBackground, flagButtonX, Math.min(Math.max(proj.y, 10), uiViewport.getWorldHeight() - flagBackground.getRegionHeight() - 10));
            float x1 = player.getBody().getPosition().x;
            float y1 = player.getBody().getPosition().y;
            float x2 = mapMan.currHole.x * 64 / PHYS_RATIO;
            float y2 = mapMan.currHole.y * 64 / PHYS_RATIO;

            int distance = (int)Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
            font.setColor(Color.CYAN);
            font.draw(ui, ""+distance+" ft.", flagButtonX + 10, Math.min(Math.max(proj.y, 10), uiViewport.getWorldHeight() - flagBackground.getRegionHeight() - 10) + 30);
        }
        uiManager.render(ui, uiViewport);
        ui.end();
        if(uiManager.isMap) {
            uiManager.drawMap(mapMan.bigMap, uiCamera, player, mapMan.currHole);
        }

        //System.out.println(camY);

    }

    private void resetBall(){
        System.out.println("Reset from " + player.getBody().getPosition().x + " " + player.getBody().getPosition().y + " LS: " + player.lastStop.x + " " + player.lastStop.y);

        player.getBody().setTransform(player.lastStop, 0f);
        System.out.println("Reset to " + player.getBody().getPosition().x + " " + player.getBody().getPosition().y);

        player.getBody().setLinearVelocity(0,0);
        player.getBody().setAngularVelocity(0);
        player.getBody().setAwake(false);
        player.getBody().setActive(false);
    }

    private void resetMap(){
        mapMan.nextMap();
        sM.fM.addScore(currLevel,player.score);
        currLevel++;
        //tiledMapRenderer = new OrthogonalTiledMapRenderer(mapMan.);
        System.out.println("Changing Map");
        player.getBody().setTransform(((mapMan.lastHole.x + .1f) * 64) / PHYS_RATIO, ((mapMan.lastHole.y + 1.25f) * 64) / PHYS_RATIO, 0);
        player.lastStop = new Vector2(player.getBody().getPosition().x, player.getBody().getPosition().y);
        player.getBody().setLinearVelocity(0,10f/3);
        player.getBody().setAngularVelocity(0);
        player.getBody().setActive(true);
        player.score = 0;
        player.getBody().setAwake(true);
    }

    private void cameraUpdate(){
        camera.position.set(player.getBody().getPosition().x * PHYS_RATIO, player.getBody().getPosition().y * PHYS_RATIO, camera.position.z);

        if(camera.zoom >= 2.6){
            camera.zoom = 2.6f;
        }
        if(camera.zoom <= 1){
            camera.zoom = 1;
        }

        float camX = camera.position.x;
        float camY = camera.position.y;


        Vector2 camMin = new Vector2(camera.viewportWidth, camera.viewportHeight);
        camMin.scl(camera.zoom /2);

        int maxX = mapMan.widths[4] * 64;

        Vector2 camMax = new Vector2(maxX, 1900);
        camMax.sub(camMin);
        camX = Math.min(camMax.x, Math.max(camX, camMin.x));
        camY = Math.min(camMax.y, Math.max(camY, camMin.y));

        camera.position.set(Math.round(camX), Math.round(camY), camera.position.z);

    }


    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime, PlayerPreview preview) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        float timestep = worldStep;
        accumulator += frameTime;
        while (accumulator >= timestep) {
            int playerX = (int) (player.getBody().getPosition().x * PHYS_RATIO / 64);
            int playerY = (int) (player.getBody().getPosition().y * PHYS_RATIO / 64);
            int previewX = 0;
            int previewY = 0;
            if(preview != null) {
                previewX = (int) (preview.getBody().getPosition().x * PHYS_RATIO / 64);
                previewY = (int) (preview.getBody().getPosition().y * PHYS_RATIO / 64);
            }
            MapLayer objects = null;
            MapObjects obj = null;
            boolean currHole;
            if(playerX != player.tileX || playerY != player.tileY || ((preview != null) && (previewX != preview.tileX || previewY != preview.tileY))) {
                objects = new MapLayer();
                obj = objects.getObjects();
                objects.setName("physics");
                currHole = mapMan.widths[0] < playerX;
                mapMan.mG.genPhysics((TiledMapTileLayer) mapMan.bigMap.getLayers().get("tiles"), obj, playerX, playerY, currHole);
                if(preview != null) {
                    mapMan.mG.genPhysics((TiledMapTileLayer) mapMan.bigMap.getLayers().get("tiles"), obj, previewX, previewY, currHole);

                }
                currHole = mapMan.widths[0] < playerX && mapMan.widths[1] > playerX;
                mapMan.createPhysics(objects, currHole);

                player.tileX = playerX;
                player.tileY = playerY;
                if(preview != null){
                    preview.tileY = previewY;
                    preview.tileX = previewX;
                    //System.out.println("Collides: "+preview.collides);
                }

            }


            world.step(timestep, 6, 2);
            accumulator -= timestep;
        }
    }

    public boolean keyDown(int keycode) {
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
            System.out.println("Speeding up! " + player.isGrounded);
            //worldStep = 1 / 120f;
            world.step(worldStep, 6, 2);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        if(keycode == Input.Keys.NUM_1) {
            resetMap();
        }
        if(keycode == Input.Keys.NUM_2) {
            //  tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        }
        if(keycode == Input.Keys.SPACE){
            //worldStep = 1/60f;
        }
        return false;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

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
        }
        //player.getBody().applyForce(1f,1f,screenX,screenY,true);

        return false;
    }

    public void hitBall(){
        player.isHitting = false;
        player.lastStop = new Vector2(player.getBody().getPosition().x, player.getBody().getPosition().y);
        float xVect = drawProj.x;
        float yVect = -1* drawProj.y;
        player.getBody().setActive(true);
        player.getBody().setLinearVelocity(xVect, yVect);
        player.score++;
        Particle[] p = new Particle[10];
        int maxY = 7;
        int minY = 1;
        int minX = -10;
        int maxX = 10;
        for(int a = 0; a < 10; a++){
            p[a] = new Particle();
            p[a].setX(player.getBody().getPosition().x);
            p[a].setY(player.getBody().getPosition().y);
            Tween.to(p[a], ParticleAccessor.POSITION_XY, 1.0f)
                    .target((float) (p[a].getX() + (Math.random()*(maxX - minX) + minX)/32f), (float) (p[a].getY() - (Math.random()*(maxY - minY) + minY)/32f))
                    .setCallbackTriggers(TweenCallback.COMPLETE)
                    .setCallback(new TweenCallback(){
                        public void onEvent(int type, BaseTween<?> source)
                        {
                            if(type == TweenCallback.COMPLETE)
                                pManager.removeParticles("hitBall");
                        }
                    })
                    .start(tManager);
        }
        pManager.addParticles(p, "hitBall");
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(player.isHitting){
            hitBall();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
        return false;
    }

    public float getProjX(float t)
    {
        return drawProj.x*t + player.getBody().getPosition().x;
    }

    public float getProjY(float t)
    {
        return (-((.5f*10 * (t * t)) + drawProj.y*t) + player.getBody().getPosition().y);
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount > 0){
            camera.zoom += .1f;
        }
        if(amount < 0){
            camera.zoom -= .1f;
        }
        return false;
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
