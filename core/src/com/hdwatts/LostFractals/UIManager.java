package com.hdwatts.LostFractals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hdwatts.LostFractals.Entities.Player;


/**
 * Created by Dean Watts on 1/9/2015.
 */
public class UIManager {
    TextureRegion zoomButton;
    TextureRegion mapButton;
    TextureRegion zoomSlider;
    BitmapFont font;
    Texture ui_assets;
    public boolean isZoom;
    public boolean isMap;
    ShapeRenderer sR;

    public UIManager(Texture ui_assets, BitmapFont font){
        this.ui_assets = ui_assets;
        zoomButton = new TextureRegion(ui_assets, 0, 0 ,64 ,64);
        zoomSlider = new TextureRegion(ui_assets, 64, 64 ,64 ,64);
        mapButton = new TextureRegion(ui_assets, 64, 0 ,64 ,64);
        sR = new ShapeRenderer();
        this.font = font;
    }

    public boolean checkCollisions(float x, float y, OrthographicCamera camera, Viewport vp, boolean isAwake){


        //y = camera.viewportHeight - y;
        float gutterLeft = vp.getLeftGutterWidth();
        float gutterRight = vp.getRightGutterWidth();
        float gutterNorth = vp.getTopGutterHeight();
        float gutterSouth = vp.getBottomGutterHeight();



        if(!(x > gutterLeft && x < vp.getScreenWidth() + gutterRight) || !(y > gutterSouth && y < vp.getScreenHeight() + gutterNorth)){
            return false;
        }



        //x -= gutterLeft;
        //y -= gutterNorth;

        Vector3 proj = vp.unproject(new Vector3(x, y, 0));

        if(proj.x <=mapButton.getRegionWidth() && proj.y <=mapButton.getRegionHeight()){
            if(isAwake){
                return false;
            }
            isMap = true;
            isZoom = false;
            return true;
        }

        if(proj.x >= vp.getWorldWidth() - zoomButton.getRegionWidth() && proj.y <= zoomButton.getRegionHeight()){
            isZoom = true;
            isMap = false;
            return true;
        }
        isMap = false;
        isZoom = false;
        return false;

    }

    public void drawMap(TiledMap tileMap, OrthographicCamera camera, Player player, Vector2 currHole){
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        int mapTileSize = 6;
        sR.setProjectionMatrix(camera.combined);
        sR.begin(ShapeRenderer.ShapeType.Filled);
        sR.setColor(0f, 0f, 0f, .75f);
        sR.rect(0, 0, 800, 160);
        //sR.setColor(0f, 210 / 255f, 255f, 1f);
        //sR.rect(0f,0f,800f,10f);
        sR.setColor(80f / 255f, 187f / 255f, 114f / 255f, 1f);
        TiledMapTileLayer layer = (TiledMapTileLayer)tileMap.getLayers().get("tiles");
        for(int a = 0; a < layer.getWidth(); a++){
            for(int b = 0; b < layer.getHeight(); b++){

                if(a == player.tileX && b == player.tileY){
                    sR.setColor(1, 1, 1, 1f);
                    sR.rect((a * mapTileSize),(b * mapTileSize), mapTileSize, mapTileSize);
                    sR.setColor(80f/255f, 187f/255f, 114f/255f, 1f);
                    continue;
                }
                if(a == currHole.x && b == currHole.y){
                    sR.setColor(1, 1, 0, 1f);
                    sR.rect((a * mapTileSize), (b * mapTileSize), mapTileSize, mapTileSize);
                    sR.setColor(80f/255f, 187f/255f, 114f/255f, 1f);
                    continue;
                }

                if(layer.getCell(a,b) != null && layer.getCell(a,b).getTile().getId() != 0) {
                    sR.rect((a * mapTileSize), (b * mapTileSize), mapTileSize, mapTileSize);
                }

            }
        }

        sR.end();

    }

    public void render(Batch batch, Viewport vp){
        if(!isMap) {
            batch.draw(zoomButton, vp.getWorldWidth() - zoomButton.getRegionWidth(), 0);
            batch.draw(mapButton, 0, 0);
        }else{
            font.setColor(1,1,1,1);
            font.draw(batch, "<Tap Anywhere to Continue>", vp.getWorldWidth()/2 - "<Tap Anywhere to Continue>".length()/2*8, vp.getWorldHeight()/2);
        }
    }
}
