package com.hdwatts.LostFractals.TweenAccessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Dean Watts on 1/17/2015.
 */
public class CameraAccessor implements TweenAccessor<OrthographicCamera> {
    public static final int ZOOM = 1;
    @Override
    public int getValues(OrthographicCamera target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ZOOM: returnValues[0] = target.zoom; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(OrthographicCamera target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ZOOM: target.zoom = newValues[0]; break;
            default: assert false; break;
        }

    }
}
