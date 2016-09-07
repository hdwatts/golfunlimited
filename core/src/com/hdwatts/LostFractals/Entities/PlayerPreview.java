package com.hdwatts.LostFractals.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Dean Watts on 1/5/2015.
 */
public class PlayerPreview {
    Body body;
    public Vector2 collision;
    public int collides;
    public int tileX;
    public int tileY;


    public PlayerPreview(World world, float x, float y, float xVect, float yVect){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = false;
        bodyDef.position.set(x, y);
        bodyDef.angularDamping = 1f;
        body = world.createBody(bodyDef);
        body.setUserData(this);
        CircleShape dynamicCircle = new CircleShape();
        dynamicCircle.setRadius(6 / 32f);

        tileX = (int)x / 64;
        tileY = (int) y / 64;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dynamicCircle;
        fixtureDef.density = 5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = .6f;
        fixtureDef.filter.categoryBits = 2;
        fixtureDef.filter.maskBits = 4;
        body.createFixture(fixtureDef);
        collides =  0;

        dynamicCircle.dispose();
        world.step(1/30f, 6, 2);
        body.setLinearVelocity(xVect, yVect);

    }

    public Body getBody(){
        return body;
    }

    public void remove(World world){
        world.destroyBody(body);
    }

}
