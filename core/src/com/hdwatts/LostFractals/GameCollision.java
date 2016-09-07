package com.hdwatts.LostFractals;

import com.badlogic.gdx.physics.box2d.*;
import com.hdwatts.LostFractals.Entities.Player;
import com.hdwatts.LostFractals.Entities.PlayerPreview;
import com.hdwatts.LostFractals.TileMaps.TileType;

/**
 * Created by Dean Watts on 12/4/2014.
 */
public class GameCollision implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();
        a.getUserData();
        //System.out.println(a.getUserData().getClass().getName() + " " + b.getUserData().getClass().getName());
        if(b.getUserData() instanceof Player && a.getUserData() instanceof TileType){
            Player p = (Player) (b.getUserData());
            p.isGrounded++;

            if(a.getUserData().toString().equals("hole")){
                p.inHole = true;
            }
        }

        if(a.getUserData() instanceof Player && b.getUserData() instanceof TileType){
            Player p = (Player) (a.getUserData());
            p.isGrounded++;


            if(b.getUserData().toString().equals("hole")){
                p.inHole = true;
            }
        }

        if(a.getUserData() instanceof PlayerPreview && b.getUserData() instanceof TileType){
            PlayerPreview p = (PlayerPreview) a.getUserData();
            p.collision = p.getBody().getPosition();
            p.collides += 1;

        }
        if(b.getUserData() instanceof PlayerPreview && a.getUserData() instanceof TileType){

            PlayerPreview p = (PlayerPreview) b.getUserData();
            p.collision = p.getBody().getPosition();
            p.collides += 1;

        }

    }

    @Override
    public void endContact(Contact contact) {
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();
        a.getUserData();
        //System.out.println(a.getUserData().getClass().getName() + " " + b.getUserData().getClass().getName());
        if(b.getUserData() instanceof Player && a.getUserData() instanceof TileType){
            Player p = (Player) (b.getUserData());
            p.isGrounded--;

            if(a.getUserData().toString().equals("hole")){
                p.inHole = false;
            }
        }
        if(a.getUserData() instanceof Player && b.getUserData() instanceof TileType){
            Player p = (Player) (a.getUserData());
            p.isGrounded--;

            if(b.getUserData().toString().equals("hole")){
                p.inHole = false;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
