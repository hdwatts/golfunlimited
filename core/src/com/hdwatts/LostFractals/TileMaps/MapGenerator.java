package com.hdwatts.LostFractals.TileMaps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Dean Watts on 11/5/2014.
 */
public class MapGenerator {
    private TiledMap map;
    public Texture tiles;
    //private Random rand;

    public MapGenerator(Texture tiles){
        this.tiles = tiles;
    }

    public TiledMap generateMap(Random rand, int heights){

        //Create textures of cells
        TextureRegion[][] splitTiles = TextureRegion.split(tiles, 64, 64);
        map = new TiledMap();
        MapLayers layers = map.getLayers();

        //Declare variables
        int waterCount = 0;
        int width = ((rand.nextInt(45)) + 30);
        int height = ((rand.nextInt(11)) + 11);
        int differenceY = (int)((rand.nextInt(4)) + 2);
        int differenceX = (int)((rand.nextInt(6)) + 1);
        int heightX = heights;
        int heightTimes = 0;


        TiledMapTileLayer layer = new TiledMapTileLayer(width, height, 64, 64);
        MapProperties props = layer.getProperties();
        layer.setName("tiles");
        props.put("height", height);
        props.put("width", width);
        props.put("differenceY", differenceY);
        props.put("differenceX", differenceX);

        System.out.println("HEIGHT: " + Integer.parseInt(props.get("height").toString()));
        System.out.println("WIDTH: " + Integer.parseInt(props.get("width").toString()));
        System.out.println("DIFFERENCEY: " + Integer.parseInt(props.get("differenceY").toString()));
        System.out.println("DIFFERENCEX: " + Integer.parseInt(props.get("differenceX").toString()));


        for (int x = 0; x < width; x++) {
            int nextYUp = rand.nextInt(differenceY);
            int nextYDown = rand.nextInt(differenceY);
            heightTimes++;
            if(heightTimes >= differenceX || waterCount > 0) {
                do {
                    heightX += nextYUp - nextYDown;
                    if(waterCount > 7){
                        heightX = 2;
                    }
                } while (waterCount > 7 && heightX <= 1);
                heightTimes = 0;
            }

            if(heightX <= 1){
                waterCount++;
                Cell cell = new Cell();
                cell.setTile(new StaticTiledMapTile(splitTiles[0][0]));
                cell.getTile().getProperties().put("heightX", 0);
                cell.getTile().getProperties().put("TextureCoords", new Vector2(0,0));
                cell.getTile().setId(0);
                layer.setCell(x, 0, cell);

                for(int y3 = 1; y3 < height; y3++){
                    cell = new Cell();
                    cell.setTile(new StaticTiledMapTile(splitTiles[0][0]));
                    cell.getTile().getProperties().put("TextureCoords", new Vector2(0,0));
                    cell.getTile().setId(0);
                    layer.setCell(x, y3, cell);
                }
                continue;
            }
            waterCount = 0;
            if(heightX >= height){
                heightX = height-1;
            }

            int ty = 0;
            int tx = 1;

            Cell cell = new Cell();
            cell.setTile(new StaticTiledMapTile(splitTiles[7][0]));
            cell.getTile().getProperties().put("TextureCoords", new Vector2(0, 7));
            cell.getTile().getProperties().put("heightX", heightX);
            cell.getTile().setId(1);
            layer.setCell(x, 0, cell);


            for (int y = 1; y < heightX - 1; y++) {
                //int ty = (int)(Math.random() * splitTiles.length);
                //int tx = (int)(Math.random() * splitTiles[ty].length);
                cell = new Cell();
                cell.setTile(new StaticTiledMapTile(splitTiles[7][0]));
                cell.getTile().getProperties().put("TextureCoords", new Vector2(0, 7));
                cell.getTile().setId(1);
                layer.setCell(x, y, cell);

            }
            cell = new Cell();
            cell.setTile(new StaticTiledMapTile(splitTiles[7][0]));
            cell.getTile().getProperties().put("TextureCoords", new Vector2(0,7));
            cell.getTile().setId(1);
            layer.setCell(x, heightX-1, cell);

            for(int y2 = heightX; y2 < height; y2++){
                ty = 0;
                tx = 0;

                cell = new Cell();
                cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                cell.getTile().getProperties().put("TextureCoords", new Vector2(0,0));
                cell.getTile().setId(0);
                layer.setCell(x, y2, cell);

            }
        }

        genFirstPass(layer, props, splitTiles, rand);
        boolean isValid = true;
        do{
          isValid = genSecondPass(layer,splitTiles);

        }while(isValid == false);
        addHole(layer,props,splitTiles, rand);
        genLastPass(layer, props, splitTiles);
        layers.add(layer);

        return map;
    }
    private void genLastPass(TiledMapTileLayer layer, MapProperties props, TextureRegion[][] splitTiles){
        int width = Integer.parseInt(props.get("width").toString());
        int height = Integer.parseInt(props.get("height").toString());

        for(int a = 0; a < width; a++){
            for(int b = 0; b < height; b++) {

                if(b == height-1){
                    Vector2 vect = ((Vector2)(layer.getCell(a,b).getTile().getProperties().get("TextureCoords")));
                    if (vect.x == 1 && vect.y == 0) {
                        Cell cell = new Cell();
                        cell.setTile(new StaticTiledMapTile(splitTiles[0][1]));
                        cell.getTile().getProperties().put("TextureCoords", new Vector2(1, 0));
                        cell.getTile().setId(1);
                        layer.setCell(a, b, cell);
                    }

                }else {
                    Vector2 vect = ((Vector2)(layer.getCell(a,b+1).getTile().getProperties().get("TextureCoords")));
                    Vector2 vect2 = ((Vector2)(layer.getCell(a,b).getTile().getProperties().get("TextureCoords")));
                    //System.out.println(vect.x + ", "+ vect.y + " - " + vect2.x + ", "+vect2.y);
                    if (vect.x == 0 && vect.y == 0 && vect2.x == 0 && vect2.y == 7) {
                        Cell cell = new Cell();
                        cell.setTile(new StaticTiledMapTile(splitTiles[0][1]));
                        cell.getTile().getProperties().put("TextureCoords", new Vector2(1, 0));
                        cell.getTile().setId(1);
                        layer.setCell(a, b, cell);
                    }
                }
            }
        }
    }

    private void addHole(TiledMapTileLayer layer, MapProperties props, TextureRegion[][] splitTiles, Random rand){
        int width = Integer.parseInt(props.get("width").toString());
        int minWidth = width - (width / 5);
        int y = -1;
        ArrayList<Vector2> possibleHoles = new ArrayList<Vector2>();
        Vector2 test;
        do {
            for (int a = minWidth; a < width; a++) {
                y = -1;
                for (int b = 0; b < Integer.parseInt(props.get("height").toString()); b++) {
                    test = (Vector2) (layer.getCell(a, b).getTile().getProperties().get("TextureCoords"));

                    if (test.x == 0 && test.y == 0) {
                        y = b - 1;
                        break;
                    }
                }

                if (y != -1) {
                    test = (Vector2) (layer.getCell(a, y).getTile().getProperties().get("TextureCoords"));
                    if ((test.x == 1 && test.y == 0) || (test.x == 0 && test.y == 7)){
                        possibleHoles.add(new Vector2(a, y));
                    }
                }
            }
            width = minWidth;
            minWidth = minWidth - (width / 5);
        }while(possibleHoles.size() == 0);

        Vector2 setHole = possibleHoles.get(rand.nextInt(possibleHoles.size()));
        System.out.println("ADDING HOLE TO: "+setHole.x + " " + setHole.y);
        Cell cell = new Cell();
        cell.setTile(new StaticTiledMapTile(splitTiles[1][0]));
        cell.getTile().getProperties().put("TextureCoords", new Vector2(0,1));
        cell.getTile().setId(3);
        layer.setCell((int)setHole.x, (int)setHole.y, cell);
        layer.getProperties().put("holeCoord",setHole);
    }


    public void genPhysics(TiledMapTileLayer layer, MapObjects obj, float indexX, float indexY, boolean isCurrHole){
        MapProperties props = layer.getProperties();

        float physRatio = 1;

        int xStart = (int)  Math.max(0, (indexX) - 1);
        int yStart = (int)  Math.max(0, (indexY) - 1);
        int xFinish = (int) Math.min(Integer.parseInt(props.get("width").toString()), (indexX) + 1);
        int yFinish = (int) Math.min(Integer.parseInt(props.get("height").toString()), (indexY) + 1);
        Array<Vector2> ignore = new Array<Vector2>();
        //ORDER IS
        //BOTTOM LEFT VERTEX
        //BOTTOM RIGHT VERTEX
        //TOP RIGHT VERTEX
        //TOP LEFT VERTEX

        for(int a = xStart; a <= xFinish; a++){
            for(int b = yStart; b < yFinish; b++){
      /*  for(int a = 0; a < Integer.parseInt(props.get("width").toString()); a++){
            for(int b = 0; b < Integer.parseInt(props.get("height").toString()); b++){

                if(getNumNeighbors(getPhysNeighbors(a, b, layer, layer.getProperties())) == 8){
                    continue;
                }*/
                if(ignore.contains(new Vector2(a,b), false)){
                    continue;
                }
                Vector2 coords;
                if(layer.getCell(a, b)==null) {
                    coords = new Vector2(0,0);
                }else{
                    coords= (Vector2) layer.getCell(a, b).getTile().getProperties().get("TextureCoords");
                }

                if(coords.x == 0 && coords.y == 0){
                    continue;
                }

                float origX = a * layer.getTileWidth() + (0 * layer.getTileWidth());
                float origY = b * layer.getTileHeight();

                //SQUARE BOX
                if(((coords.x == 0 && coords.y == 7) || (coords.x == 1 && coords.y == 0)) || (coords.x == 0 && coords.y == 1 && !isCurrHole)){
                    int con = 1;

                    if (layer.getCell(a + con, b) == null) {
                        coords = new Vector2(0, 0);
                    } else {
                        coords = (Vector2) layer.getCell(a + con, b).getTile().getProperties().get("TextureCoords");
                    }


                    while(((coords.x == 0 && coords.y == 7) || (coords.x == 1 && coords.y == 0)) || (coords.x == 0 && coords.y == 1 && !isCurrHole)) {
                        ignore.add(new Vector2(a + con, b));
                        con++;
                        if (layer.getCell(a + con, b) == null || a + con > xFinish) {
                            coords = new Vector2(0, 0);
                        } else {
                            coords = (Vector2) layer.getCell(a + con, b).getTile().getProperties().get("TextureCoords");
                        }
                    }
                    obj.add(new RectangleMapObject(origX * physRatio, origY * physRatio, (layer.getTileWidth() * con) * physRatio, layer.getTileHeight() * physRatio));
                    continue;
                }
                //ADD HOLE
                if(coords.x == 0 && coords.y == 1 && isCurrHole){
                    obj.add(new RectangleMapObject((origX) * physRatio, (origY) * physRatio, 16 * physRatio, layer.getTileHeight()));
                    obj.add(new RectangleMapObject((origX + 16) * physRatio, (origY) * physRatio, 32 * physRatio, 32 * physRatio));
                    obj.add(new RectangleMapObject((origX + layer.getTileWidth() - 16) * physRatio, (origY) * physRatio, 16 * physRatio, layer.getTileHeight()));

                    RectangleMapObject collHole = new RectangleMapObject((origX + 16)* physRatio, (origY + layer.getTileHeight() - 32) * physRatio, 32 * physRatio, 8 * physRatio);
                    collHole.setName("hole");
                    obj.add(collHole);
                }

                //DOWNRIGHT TRIANGLE
                if(coords.x == 2 && coords.y == 0){
                    float[] x = {origX * physRatio, origY * physRatio,
                                (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                                (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWNLEFT TRIANGLE
                if(coords.x == 3 && coords.y == 0){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //HALF DOWNRIGHT TRIANGLE
                if(coords.x == 4 && coords.y == 0){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth() / 2) * physRatio, (origY) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //HALF DOWNLEFT TRIANGLE
                if(coords.x == 5 && coords.y == 0){
                    float[] x = {(origX + layer.getTileHeight() / 2) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //THIRD DOWNRIGHT TRIANGLE
                    if(coords.x == 6 && coords.y == 0){
                        float[] x = {origX * physRatio, origY * physRatio,
                                (origX + layer.getTileWidth() / 3) * physRatio, (origY) * physRatio,
                                (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                        obj.add(new PolygonMapObject(x));
                        continue;
                }

                //THIRD DOWNLEFT TRIANGLE
                if(coords.x == 7 && coords.y == 0){
                    float[] x = {(origX + (layer.getTileWidth() - layer.getTileWidth() / 3)) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //TOP LEFT INSIDE CURVE
                if(coords.x == 1 && coords.y == 1){
                   /* float[] x = {(origX * physRatio), origY * physRatio,
                                (origX + 4) * physRatio, (origY + 24)* physRatio,
                                (origX + 13) * physRatio, (origY + 39)* physRatio,
                                (origX + 25) * physRatio, (origY + 50)* physRatio,
                                (origX + 41) * physRatio, (origY + 59)* physRatio,
                                (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight())* physRatio,
                                (origX * physRatio), (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));*/
                    continue;
                }

                //BOTTOM LEFT UP TRIANGLE
                if(coords.x == 2 && coords.y == 1){
                    float[] x = {(origX * physRatio), origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight())* physRatio,
                            (origX * physRatio), (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //BOTTOM RIGHT DOWN TRIANGLE
                if(coords.x == 3 && coords.y == 1){
                    float[] x = {(origX + layer.getTileWidth())* physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight())* physRatio,
                            (origX * physRatio), (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN HALF SQUARE SLOPE
                if(coords.x == 4 && coords.y == 1){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth() / 2) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP HALF SQUARE SLOPE
                if(coords.x == 5 && coords.y == 1){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX + layer.getTileWidth() / 2) * physRatio, (origY + layer.getTileHeight()) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN THIRD SQUARE SLOPE
                if(coords.x == 6 && coords.y == 1){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + (2 * layer.getTileWidth()) / 3) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth() / 3) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP THIRD SQUARE SLOPE
                if(coords.x == 7 && coords.y == 1){
                    float[] x = {(origX + (layer.getTileWidth()) / 3) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX + (layer.getTileWidth() - layer.getTileWidth() / 3)) * physRatio, (origY + layer.getTileHeight()) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //BOTTOM LEFT INSIDE CURVE
                if(coords.x == 0 && coords.y == 2){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + 40) * physRatio, (origY + 4f) * physRatio,
                            (origX) * physRatio, (origY + 4f) * physRatio};
                    obj.add(new PolygonMapObject(x));

                    float[] x2 = {origX * physRatio, (origY + 4f) * physRatio,
                            (origX + 40) * physRatio, (origY + 4f) * physRatio,
                            (origX + layer.getTileWidth() - 39f) * physRatio, (origY + 13f) * physRatio,
                            (origX) * physRatio, (origY + 13f) * physRatio};
                    obj.add(new PolygonMapObject(x2));

                    float[] x3 = {origX * physRatio, (origY + 13f) * physRatio,
                            (origX + layer.getTileWidth() - 39f) * physRatio, (origY + 13f) * physRatio,
                            (origX + layer.getTileWidth() - 50f) * physRatio, (origY + 25f) * physRatio,
                            (origX) * physRatio, (origY + 25f) * physRatio};
                    obj.add(new PolygonMapObject(x3));

                    float[] x4 = {origX * physRatio, (origY + 25f) * physRatio,
                            (origX + layer.getTileWidth() - 50f) * physRatio, (origY + 25f) * physRatio,
                            (origX + layer.getTileWidth() - 59f) * physRatio, (origY + 41f) * physRatio,
                            (origX) * physRatio, (origY + 41f) * physRatio};
                    obj.add(new PolygonMapObject(x4));

                    float[] x5 = {origX * physRatio, (origY + 41f) * physRatio,
                            (origX + layer.getTileWidth() - 59f) * physRatio, (origY + 41f) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x5));

                    //
                    continue;
                }

                //BOTTOM RIGHT INSIDE CURVE
                if(coords.x == 1 && coords.y == 2){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 4f) * physRatio,
                            (origX + 24) * physRatio, (origY + 4f) * physRatio};
                    obj.add(new PolygonMapObject(x));

                    float[] x2 = {(origX + 24) * physRatio, (origY + 4f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY +4f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 13f) * physRatio,
                            (origX + 39f) * physRatio, (origY + 13f) * physRatio};
                    obj.add(new PolygonMapObject(x2));

                    float[] x3 = {(origX + 39f) * physRatio, (origY + 13f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 13f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 25f) * physRatio,
                            (origX + 50f) * physRatio, (origY + 25f) * physRatio};
                    obj.add(new PolygonMapObject(x3));

                    float[] x4 = {(origX +  50f) * physRatio, (origY + 25f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 25f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 41f) * physRatio,
                            (origX +  59f) * physRatio, (origY + 41f) * physRatio};
                    obj.add(new PolygonMapObject(x4));

                    float[] x5 = {(origX + 59f) * physRatio, (origY + 41f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + 41f) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x5));

                    continue;
                }
                //DOWN THIRD SQUARE SLOPE BOT
                if(coords.x == 6 && coords.y == 2){
                    float[] x = {origX * physRatio, origY * physRatio,
                            (origX + (layer.getTileWidth())) * physRatio, (origY) * physRatio,
                            (origX + (2 *layer.getTileWidth()) / 3) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP THIRD SQUARE SLOPE BOT
                if(coords.x == 7 && coords.y == 2){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight()) * physRatio,
                            (origX + (layer.getTileWidth() / 3)) * physRatio, (origY + layer.getTileHeight()) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }
                //HALF BLOCK LEFT
                if(coords.x == 6 && coords.y == 3){
                    obj.add(new RectangleMapObject(origX * physRatio, origY * physRatio, layer.getTileWidth() / 2 * physRatio, layer.getTileHeight() * physRatio));
                    continue;
                }
                //HALF BLOCK TOP
                if(coords.x == 7 && coords.y == 3){
                    obj.add(new RectangleMapObject(origX * physRatio, (origY + layer.getTileHeight() / 2)* physRatio, layer.getTileWidth() * physRatio, layer.getTileHeight() / 2 * physRatio));
                    continue;
                }
                //HALF BLOCK RIGHT
                if(coords.x == 6 && coords.y == 4){
                    obj.add(new RectangleMapObject((origX + layer.getTileWidth() / 2) * physRatio, origY * physRatio, layer.getTileWidth() / 2 * physRatio, layer.getTileHeight() * physRatio));
                    continue;
                }
                //HALF BLOCK BOT
                if(coords.x == 7 && coords.y == 4){
                    obj.add(new RectangleMapObject(origX * physRatio, (origY)* physRatio, layer.getTileWidth() * physRatio, layer.getTileHeight() /2 * physRatio));
                    continue;
                }

                //THIRD BLOCK LEFT
                if(coords.x == 2 && coords.y == 4){
                    obj.add(new RectangleMapObject(origX * physRatio, origY * physRatio, layer.getTileWidth() / 3 * physRatio, layer.getTileHeight() * physRatio));
                    continue;
                }
                //THIRD BLOCK TOP
                if(coords.x == 1 && coords.y == 4){
                    obj.add(new RectangleMapObject(origX * physRatio, (origY + 2 * layer.getTileHeight() / 3)* physRatio, layer.getTileWidth() * physRatio, layer.getTileHeight() / 3 * physRatio));
                    continue;
                }
                //THIRD BLOCK RIGHT
                if(coords.x == 0 && coords.y == 4){
                    obj.add(new RectangleMapObject((origX + 2 * layer.getTileWidth() / 3) * physRatio, origY * physRatio, layer.getTileWidth() / 3 * physRatio, layer.getTileHeight() * physRatio));
                    continue;
                }
                //THIRD BLOCK BOT
                if(coords.x == 3 && coords.y == 4){
                    obj.add(new RectangleMapObject(origX * physRatio, (origY)* physRatio, layer.getTileWidth() * physRatio, layer.getTileHeight() /3 * physRatio));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE FIRST
                if(coords.x == 0 && coords.y == 5){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (2 * layer.getTileHeight() / 3)) * physRatio,
                            (origX) * physRatio, (origY + layer.getTileHeight()) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE MID
                if(coords.x == 1 && coords.y == 5){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight() / 3)) * physRatio,
                            (origX) * physRatio, (origY + (2 * layer.getTileHeight() / 3)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE LAST
                if(coords.x == 2 && coords.y == 5){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight() / 3)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT HALF SQUARE SLOPE FIRST
                if(coords.x == 3 && coords.y == 5){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight() / 2)) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight())) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT HALF SQUARE SLOPE LAST
                if(coords.x == 4 && coords.y == 5){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight() / 2)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE FIRST
                if(coords.x == 5 && coords.y == 6){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight() / 3)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE MID
                if(coords.x == 6 && coords.y == 6){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (2 * layer.getTileHeight() / 3)) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight() / 3)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //DOWN RIGHT THIRD SQUARE SLOPE LAST
                if(coords.x == 7 && coords.y == 6){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight())) * physRatio,
                            (origX) * physRatio, (origY + (2 * layer.getTileHeight() / 3)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP RIGHT HALF SQUARE SLOPE FIRST
                if(coords.x == 3 && coords.y == 7){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight())) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight() / 2)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP RIGHT HALF SQUARE SLOPE LAST
                if(coords.x == 2 && coords.y == 7){
                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight() / 2)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                //UP RIGHT OUTTER CIRCLE
                if(coords.x == 6 && coords.y == 7){


                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY + (layer.getTileHeight())) * physRatio,
                            (origX + layer.getTileWidth() - 24) * physRatio, (origY + (layer.getTileHeight() - 5)) * physRatio,
                            (origX + layer.getTileWidth() - 39) * physRatio, (origY + (layer.getTileHeight() - 13)) * physRatio,
                            (origX + layer.getTileWidth() - 50) * physRatio, (origY + (layer.getTileHeight() - 25)) * physRatio,
                            (origX + layer.getTileWidth() - 59) * physRatio, (origY + (layer.getTileHeight() - 41)) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }
                //DOWN RIGHT OUTTER CIRCLE
                if(coords.x == 7 && coords.y == 7){


                    float[] x = {(origX) * physRatio, origY * physRatio,
                            (origX + layer.getTileWidth()) * physRatio, (origY) * physRatio,
                            (origX + layer.getTileWidth() - 5) * physRatio, (origY + 24) * physRatio,
                            (origX + layer.getTileWidth() - 13) * physRatio, (origY + 39) * physRatio,
                            (origX + layer.getTileWidth() - 25) * physRatio, (origY + 50) * physRatio,
                            (origX + layer.getTileWidth() - 41) * physRatio, (origY + 59) * physRatio,
                            (origX) * physRatio, (origY + (layer.getTileHeight())) * physRatio};

                    obj.add(new PolygonMapObject(x));
                    continue;
                }

                /* float[] x = {(origX * physRatio), origY * physRatio,
                                (origX + 4) * physRatio, (origY + 24)* physRatio,
                                (origX + 13) * physRatio, (origY + 39)* physRatio,
                                (origX + 25) * physRatio, (origY + 50)* physRatio,
                                (origX + 41) * physRatio, (origY + 59)* physRatio,
                                (origX + layer.getTileWidth()) * physRatio, (origY + layer.getTileHeight())* physRatio,
                                (origX * physRatio), (origY + layer.getTileHeight()) * physRatio};
                    obj.add(new PolygonMapObject(x));*/
            }
        }
    }



    private boolean genSecondPass(TiledMapTileLayer layer, TextureRegion[][] splitTiles) {
        MapProperties props = layer.getProperties();

        for (int a = 0; a < Integer.parseInt(props.get("width").toString()); a++) {
            for (int b = 0; b < Integer.parseInt(props.get("height").toString()); b++) {

                if(layer.getCell(a,b).getTile().getId() == 2){
                    int[][] neighbors = getNeighbors(a,b,layer,props);
                    if(!processNeighbors(neighbors).contains(layer.getCell(a,b).getTile().getProperties().get("TextureCoords"))){
                        Cell cell = new Cell();
                        cell.setTile(new StaticTiledMapTile(splitTiles[0][0]));
                        cell.getTile().setId(0);
                        cell.getTile().getProperties().put("TextureCoords", new Vector2(0,0));
                        layer.setCell(a,b,cell);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private void genFirstPass(TiledMapTileLayer layer, MapProperties props, TextureRegion[][] splitTiles, Random rand){
        int[][] neighbors;
        int heightX;

        for(int a = 0; a < Integer.parseInt(props.get("width").toString()); a++){
            heightX = Integer.parseInt(layer.getCell(a, 0).getTile().getProperties().get("heightX").toString());
            if(heightX > 1) {
                neighbors = getNeighbors(a, heightX, layer, props);
                ArrayList<Vector2> list = processNeighbors(neighbors);
                Vector2 split = list.get(rand.nextInt(list.size()));


                Cell cell = new Cell();
                cell.setTile(new StaticTiledMapTile(splitTiles[(int) split.y][(int) split.x]));
                cell.getTile().getProperties().put("TextureCoords", split);
                if (split.y == 0 && split.x == 0){
                    cell.getTile().setId(0);
                }else if ((split.y == 0 && split.x == 1) || (split.y == 7 && split.x == 0)){
                    cell.getTile().setId(1);
                }else {

                    cell.getTile().setId(2);
                }
                layer.setCell(a, heightX, cell);
            }
        }
    }



    private ArrayList<Vector2> processNeighbors(int[][] neighbors) {
        ArrayList<Vector2> possiblePoints = new ArrayList<Vector2>();
        possiblePoints.add(new Vector2(0,0));
        //possiblePoints.add(new Vector2(1,0));

        int[][] test = new int[][]{
                {1, 0, 1},
                {1, 0, 1},
                {1, 1, 1}
        };

        if (arrayCompare(test,neighbors)) {
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(7,6));
            //possiblePoints.add(new Vector2(0, 7));
            //possiblePoints.add(new Vector2(1, 7));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(0,2));

        }

        test = new int[][]{
                {0, 0, 0},
                {1, 0, 1},
                {1, 1, 1}
        };
        if (arrayCompare(test,neighbors)) {
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(7,6));
            //possiblePoints.add(new Vector2(0, 7));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(3,7));
        }

        test = new int[][]{
                {1, 0, 0},
                {1, 0, 1},
                {1, 1, 1}
        };

        if (arrayCompare(test,neighbors)) {
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(7,6));
            //possiblePoints.add(new Vector2(0, 7));
            //possiblePoints.add(new Vector2(1, 7));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,7));
        }

        test = new int[][]{
                {1, 0, 0},
                {1, 0, 0},
                {1, 1, 1}
        };

        if(arrayCompare(test, neighbors)){
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(1,5));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(7,7));
        }

        test = new int[][]{
                {1, 0, 0},
                {1, 0, 0},
                {1, 1, 0}
        };

        if(arrayCompare(test, neighbors)){
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(1,5));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(7,7));
        }

        test = new int[][]{
                {0, 0, 0},
                {1, 0, 0},
                {1, 1, 1}
        };
        if(arrayCompare(test, neighbors)){
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(1,5));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(7,7));
        }
        test = new int[][]{
                {0, 0, 0},
                {1, 0, 0},
                {1, 1, 0}
        };
        if(arrayCompare(test, neighbors)){
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(1,5));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(7,7));
        }

        test = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {1, 1, 0}
        };
        if(arrayCompare(test, neighbors)){
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(4,1));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(1,2));
            //possiblePoints.add(new Vector2(2,2));
            //possiblePoints.add(new Vector2(4,2));
            possiblePoints.add(new Vector2(6,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(6,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(0,5));
            possiblePoints.add(new Vector2(1,5));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(3,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(6,7));
            possiblePoints.add(new Vector2(7,7));


        }

        test = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {1, 1, 1}
        };
        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
           // possiblePoints.add(new Vector2(3,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(5,6));
        }
        test = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 1, 1}
        };
        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(4,1));
            possiblePoints.add(new Vector2(6,1));
            possiblePoints.add(new Vector2(0,2));
            //possiblePoints.add(new Vector2(3,2));
            //possiblePoints.add(new Vector2(5,2));
            possiblePoints.add(new Vector2(6,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(6,3));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(2,5));
            possiblePoints.add(new Vector2(4,5));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(6,7));
            possiblePoints.add(new Vector2(7,7));

        }

        test = new int[][]{
                {0, 0, 0},
                {0, 0, 1},
                {1, 1, 1}
        };
        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(4,4));
            possiblePoints.add(new Vector2(6,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(6,7));
        }
        test = new int[][]{
                {0, 0, 1},
                {0, 0, 1},
                {0, 1, 1}
        };
        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(4,4));
            possiblePoints.add(new Vector2(6,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(6,7));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(6,1));

        }

        test = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 1, 0}
        };

        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(0,0));
            possiblePoints.add(new Vector2(2,0));
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(6,7));
            possiblePoints.add(new Vector2(7,7));
        }

        test = new int[][]{
                {0, 0, 1},
                {1, 0, 1},
                {1, 1, 1}
        };

        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(1,0));
            possiblePoints.add(new Vector2(4,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(6,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(4,1));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(6,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(0,2));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(6,1));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(2,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(7,6));
        }

        test = new int[][]{
                {0, 0, 1},
                {0, 0, 1},
                {1, 1, 1}
        };

        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(6,7));

        }
        test = new int[][]{
                {0, 0, 0},
                {0, 0, 1},
                {0, 1, 1}
        };

        if(arrayCompare(test, neighbors)) {
            possiblePoints.add(new Vector2(3,0));
            possiblePoints.add(new Vector2(5,0));
            possiblePoints.add(new Vector2(7,0));
            possiblePoints.add(new Vector2(5,1));
            possiblePoints.add(new Vector2(7,1));
            possiblePoints.add(new Vector2(1,2));
            possiblePoints.add(new Vector2(7,2));
            possiblePoints.add(new Vector2(0,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(7,4));
            possiblePoints.add(new Vector2(3,4));
            possiblePoints.add(new Vector2(5,6));
            possiblePoints.add(new Vector2(6,6));
            possiblePoints.add(new Vector2(7,6));
            possiblePoints.add(new Vector2(2,7));
            possiblePoints.add(new Vector2(3,7));
            possiblePoints.add(new Vector2(6,7));

        }

            return possiblePoints;
    }

    private boolean arrayCompare(int[][] a2, int[][]a3){
/*
        System.out.println(printArray(a2));
        System.out.println("-----------------------\n"+printArray(a3)+"============================\n");
*/
        return printArray(a2).equals(printArray(a3));
    }

    private String printArray(int[][] arr){
        String ret = "";
        for(int a = 0; a < arr.length; a++){
            for(int b = 0; b < arr[a].length; b++){
                ret = ret + arr[a][b] + ", ";
            }
            ret = ret + "\n";
        }
        return ret;
    }

    private int getNumNeighbors(int[][] neighbors){
        int sum = 0;
        for(int a = 0; a < 3; a++){
            for(int b=0; b < 3; b++){
                if(a == 1 && b == 1){
                    continue;
                }
                if(neighbors[a][b] == 1){
                   sum++;
                }
            }
        }
        return sum;
    }

    private int[][] getNeighbors(int x, int y, TiledMapTileLayer layer, MapProperties props){
        int[][] neighbors = new int[3][3];

        int height = Integer.parseInt(props.get("height").toString());
        int width = Integer.parseInt(props.get("width").toString());


        neighbors[1][1] = layer.getCell(x,y).getTile().getId() > 0 ? 0 : 0;

        if(x > 0)
            neighbors[1][0] = layer.getCell(x-1,y).getTile().getId() >= 1 ? 1 : 0;

        if(y > 0)
             neighbors[2][1] = layer.getCell(x,y-1).getTile().getId() >= 1 ? 1 : 0;

        if(x > 0 && y > 0)
            neighbors[2][0] = layer.getCell(x-1,y-1).getTile().getId() >= 1 ? 1 : 0;

        if(x < width-1)
            neighbors[1][2] = layer.getCell(x+1,y).getTile().getId() >= 1 ? 1 : 0;

        if(y < height-1)
            neighbors[0][1] = layer.getCell(x,y+1).getTile().getId() >= 1 ? 1 : 0;


        if(y < height-1 && x < width-1)
            neighbors[0][2] = layer.getCell(x+1,y+1).getTile().getId() >= 1 ? 1 : 0;

        if(x > 0 && y < height-1)
            neighbors[0][0] = layer.getCell(x-1,y+1).getTile().getId() >= 1 ? 1 : 0;

        if(y > 0 && x < width-1)
            neighbors[2][2] = layer.getCell(x+1,y-1).getTile().getId() >= 1 ? 1 : 0;



        return neighbors;
    }


}
