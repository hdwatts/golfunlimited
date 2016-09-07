package com.hdwatts.LostFractals.TileMaps;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.hdwatts.LostFractals.FileManager;

import java.util.Random;

import static com.badlogic.gdx.maps.tiled.TiledMapTileLayer.*;

/**
 * Created by Dean Watts on 12/29/2014.
 */
public class MapManager {
    int currentMap;
    public MapGenerator mG;
    public MapBodyManager mBm;
    CustomMapRenderer tMr;
    public Vector2 lastHole;
    //private Random rand;
    public Vector2 currHole;
    public TiledMap bigMap;
    TiledMap[] map;
    FileManager fM;
    public int[] widths;
    public int[] heights;
    public long seed;
    Vector2[] holes;

    public long[] seeds;

    public MapManager(MapGenerator mg, World world, float PHYS_RATIO, long seed2, int height, FileManager fM){
        this.mG = mg;
        map = new TiledMap[5];
        widths = new int[5];
        heights = new int[5];
        holes = new Vector2[5];
        seeds = new long[5];
        seed = seed2;
        this.fM = fM;

        mBm = new MapBodyManager(world, PHYS_RATIO, null, Logger.DEBUG);
        currentMap = 0;

        Random rand = new Random(seed);
        map[0] = mG.generateMap(rand, height);
        heights[0] = height;
        seeds[0] = seed;
        seed = rand.nextLong();

        rand = new Random(seed);
        map[1] = mG.generateMap(rand,getHeight(map[0]));
        seeds[1] = seed;
        heights[1] = getHeight(map[0]);
        seed = rand.nextLong();

        rand = new Random(seed);
        map[2] = mG.generateMap(rand,getHeight(map[1]));
        seeds[2] = seed;
        heights[2] = getHeight(map[1]);
        seed = rand.nextLong();

        rand = new Random(seed);
        map[3] = mG.generateMap(rand,getHeight(map[2]));
        seeds[3] = seed;
        heights[3] = getHeight(map[2]);
        seed = rand.nextLong();

        rand = new Random(seed);
        map[4] = mG.generateMap(rand,getHeight(map[3]));
        seeds[4] = seed;
        heights[4] = getHeight(map[3]);
        this.seed = rand.nextLong();



        widths[0] = getWidth(1);
        widths[1] = getWidth(2);
        widths[2] = getWidth(3);
        widths[3] = getWidth(4);
        widths[4] = getWidth(5);

        holes[0] = (Vector2)((map[0].getLayers().get("tiles"))).getProperties().get("holeCoord");
        holes[1] = (Vector2)((map[1].getLayers().get("tiles"))).getProperties().get("holeCoord");
        holes[2] = (Vector2)((map[2].getLayers().get("tiles"))).getProperties().get("holeCoord");
        holes[3] = (Vector2)((map[3].getLayers().get("tiles"))).getProperties().get("holeCoord");
        holes[4] = (Vector2)((map[4].getLayers().get("tiles"))).getProperties().get("holeCoord");



        lastHole = (Vector2)((TiledMapTileLayer)(map[0].getLayers().get("tiles"))).getProperties().get("holeCoord");

        //createPhysics();
        genBigMap();
    }


    public void render(OrthographicCamera camera){
        tMr.setView(camera);
        tMr.render();
    }


    public boolean testPreviewCollision(float x, float y){
        int xTile = (int) x / 64;
        int yTile = (int) y / 64;
        //System.out.println(xTile+ " tiles "+ yTile);
        if(((TiledMapTileLayer)(bigMap.getLayers().get("tiles"))).getCell(xTile,yTile) == null){
            return true;
        }else {
            return ((TiledMapTileLayer) (bigMap.getLayers().get("tiles"))).getCell(xTile, yTile).getTile().getId() == 0;
        }
    }

    public void createPhysics(MapLayer x, boolean isCurrentHole){
        mBm.destroyPhysics();
        bigMap.getLayers().remove(bigMap.getLayers().get("physics"));
        bigMap.getLayers().add(x);
        mBm.createPhysics(bigMap, "physics", isCurrentHole);
    }

    private int getMaxHeight(){

        int max = 0;
        for(int a = 0; a < 5; a++){
            max = Math.max(((TiledMapTileLayer) (map[a].getLayers().get("tiles"))).getHeight(), max);
        }
        return max;
    }

    private void genBigMap(){
        bigMap = new TiledMap();
        int width = getWidth(5);
        int height = getMaxHeight();
        TiledMapTileLayer bigLayer = new TiledMapTileLayer(width, height, 64, 64);
        bigLayer.getProperties().put("width",width);
        bigLayer.getProperties().put("height",height);
        int c = 0;
        int d = 0;
        int currMap = 0;
        int mapWidth = ((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getWidth();
        int mapHeight = ((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getHeight();

        bigLayer.setName("tiles");

        for(int a = 0; a < width; a++){
            if(c == mapWidth){
                c = 0;
                currMap++;

                if(currMap == 1){
                    currHole = holes[1];
                    currHole.x = currHole.x + widths[0];
                    System.out.println("FLAG X: "+currHole.x);
                }
                if(currMap == 5){
                    break;
                }
                mapWidth = ((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getWidth();
                mapHeight = ((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getHeight();
            }
            for(int b = 0; b < width; b++){
                if(d==mapHeight){
                    d = 0;
                    break;
                }
                if(currMap != 0){
                    bigLayer.setCell(a,b,((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getCell(c,d));
                }else{
                    if(c == lastHole.x && d == lastHole.y){
                        Cell cell = new Cell();
                        TextureRegion[][] splitTiles = TextureRegion.split(mG.tiles, 64, 64);
                        cell.setTile(new StaticTiledMapTile(splitTiles[0][1]));
                        cell.getTile().getProperties().put("TextureCoords", new Vector2(1,0));
                        cell.getTile().setId(1);

                        bigLayer.setCell(a,b,cell);
                    }else{
                        bigLayer.setCell(a,b,((TiledMapTileLayer)(map[currMap].getLayers().get("tiles"))).getCell(c,d));
                    }
                }

                d++;
            }
            c++;
        }


        bigMap.getLayers().add(bigLayer);

        tMr = new CustomMapRenderer(bigMap);
        for(int a = 0; a < 5; a++){
            map[a] = null;
        }
    }

    public TiledMap genMapFromBig(TiledMap bigMap, int startX, int width){
        TiledMap smallMap = new TiledMap();
        TiledMapTileLayer layer = new TiledMapTileLayer(width-startX, Integer.parseInt(bigMap.getLayers().get(0).getProperties().get("height").toString()), 64, 64);
        layer.setName("tiles");
        TiledMapTileLayer bigLayer = (TiledMapTileLayer)bigMap.getLayers().get("tiles");
        int c = 0;
        for(int a = startX; a < width; a++){
            for(int b = 0; b < bigLayer.getHeight(); b++){
                layer.setCell(c,b,bigLayer.getCell(a,b));
            }
            c++;
        }
        smallMap.getLayers().add(layer);
        return smallMap;
    }

    public void nextMap(){
        mBm.destroyPhysics();


        MapLayer objects;
        MapObjects obj;
        //int prevWidth = widths[0];
        map[0] = genMapFromBig(bigMap, widths[0], widths[1]);
        lastHole = holes[1];
        lastHole.x = lastHole.x - widths[0];

        //map[0].getLayers().remove(map[0].getLayers().get("physics"));
/*
        objects = new MapLayer();
        objects.setName("physics");
        obj = objects.getObjects();
        //mG.genPhysics((TiledMapTileLayer)(map[0].getLayers().get("tiles")),obj, 0);
        map[0].getLayers().add(objects);
*/

        map[1] = genMapFromBig(bigMap, widths[1], widths[2]);
        //map[1].getLayers().remove(map[1].getLayers().get("physics"));
/*
        objects = new MapLayer();
        objects.setName("physics");
        obj = objects.getObjects();
        //mG.genPhysics((TiledMapTileLayer)(map[1].getLayers().get("tiles")),obj, getWidth(1));
        map[1].getLayers().add(objects);
*/

        map[2] = genMapFromBig(bigMap, widths[2], widths[3]);
       // map[2].getLayers().remove(map[2].getLayers().get("physics"));
/*
        objects = new MapLayer();
        objects.setName("physics");
        obj = objects.getObjects();
        //mG.genPhysics((TiledMapTileLayer)(map[2].getLayers().get("tiles")),obj, getWidth(2));
        map[2].getLayers().add(objects);
*/

        map[3] = genMapFromBig(bigMap, widths[3], widths[4]);
        //map[3].getLayers().remove(map[3].getLayers().get("physics"));
/*
        objects = new MapLayer();
        objects.setName("physics");
        obj = objects.getObjects();
        //mG.genPhysics((TiledMapTileLayer)(map[3].getLayers().get("tiles")),obj, getWidth(3));
        map[3].getLayers().add(objects);
*/
        holes[0] = holes[1];
        holes[1] = holes[2];
        holes[2] = holes[3];
        holes[3] = holes[4];

        seeds[0] = seeds[1];
        seeds[1] = seeds[2];
        seeds[2] = seeds[3];
        seeds[3] = seeds[4];

        heights[0] = heights[1];
        heights[1] = heights[2];
        heights[2] = heights[3];
        heights[3] = heights[4];
        fM.addSeed(seeds[0],heights[0]);


        Random rand = new Random(this.seed);
        map[4] = mG.generateMap(rand,getHeight(map[3]));
        seeds[4] = this.seed;
        this.seed = rand.nextLong();
        heights[4] = getHeight(map[3]);
        holes[4] = (Vector2)map[4].getLayers().get("tiles").getProperties().get("holeCoord");

        widths[0] = getWidth(1);
        widths[1] = getWidth(2);
        widths[2] = getWidth(3);
        widths[3] = getWidth(4);
        widths[4] = getWidth(5);


        // map[4].getLayers().remove(map[4].getLayers().get("physics"));
        //objects = new MapLayer();
        //objects.setName("physics");
        //obj = objects.getObjects();
        //mG.genPhysics((TiledMapTileLayer)(map[4].getLayers().get("tiles")),obj, getWidth(4));
        //map[4].getLayers().add(objects);

        //createPhysics();
        genBigMap();
    }

    public int getWidth(int num){
        int sum = 0;
        for(int a = 0; a < num; a++){
            sum = sum + ((TiledMapTileLayer) map[a].getLayers().get("tiles")).getWidth();
        }
        return sum;
    }

    public int getHeight(TiledMap map){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("tiles");
        int x = layer.getWidth() - 1;
        for(int b = 0; b < layer.getHeight(); b++){
            if(layer.getCell(x,b) == null){
                if(b == 0){
                    x--;
                    b = 0;
                    continue;
                }
                return b-1;
            }


            if(layer.getCell(x,b).getTile().getId() == 0){
                if(b == 0){
                    x--;
                    b = 0;
                    continue;
                }
                return b;
            }
        }
        return layer.getHeight()-1;
    }

}
