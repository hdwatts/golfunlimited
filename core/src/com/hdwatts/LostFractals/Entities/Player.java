package com.hdwatts.LostFractals.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Dean Watts on 11/18/2014.
 */
public class Player {

    Body body;
    public boolean isHitting;
    public int isGrounded;
    public boolean inHole;
    public float timeSlow;
    public Vector2 startHit;
    public int tileX;
    public int tileY;
    public Vector2 lastStop;
    public float timeStop;
    public boolean timeSpeed;
    public int score;

    public Player(World world, float x, float y){
        isHitting = false;
        isGrounded = 0;
        inHole = false;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        timeStop = 1.5f;
        timeSpeed = false;
        timeSlow = 0;
        lastStop = new Vector2(x / 32,y / 32);
        startHit = new Vector2(0,0);
        score = 0;

        bodyDef.fixedRotation = false;
        bodyDef.position.set(x / 32, y / 32);
        bodyDef.angularDamping = 1f;

        tileX = (int)x / 64;
        tileY = (int) y / 64;

        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape dynamicCircle = new CircleShape();
        dynamicCircle.setRadius(6 / 32f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = 1;
        fixtureDef.filter.maskBits = 4;
        fixtureDef.shape = dynamicCircle;
        fixtureDef.density = 5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = .6f;
        body.createFixture(fixtureDef);

        dynamicCircle.dispose();;

    }

    public Body getBody(){
        return body;
    }
}
