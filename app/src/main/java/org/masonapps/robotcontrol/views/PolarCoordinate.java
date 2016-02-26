package org.masonapps.robotcontrol.views;

import android.graphics.PointF;

/**
 * Created by Bob on 2/24/2016.
 */
public class PolarCoordinate {
    public float r; //radius
    public float a; //angle in radians

    public PolarCoordinate(){
        r = 0f;
        a = 0f;
    }

    public PolarCoordinate(float radius, float angle){
        r = radius;
        a = angle;
    }
    
    public static PolarCoordinate fromCartesian(float x, float y){
        final PolarCoordinate polarCoordinate = new PolarCoordinate();
        polarCoordinate.setCartesian(x, y);
        return polarCoordinate;
    }

    public float getAngleDegress() {
        return (float) Math.toDegrees(a);
    }
    
    public void setCartesian(PointF point) {
        r = (float) Math.sqrt(point.x * point.x + point.y * point.y);
        a = (float) Math.atan2(point.y, point.x);
    }
    
    public void toCartesian(PointF point) {
        point.set(getX(), getY());
    }

    public float getX() {
        return (float) Math.cos(a) * r;
    }

    public float getY() {
        return (float) Math.sin(a) * r;
    }

    public void setCartesian(float x, float y) {
        r = (float) Math.sqrt(x * x + y * y);
        a = (float) Math.atan2(y, x);
    }

    public void set(float radius, float angle) {
        r = radius;
        a = angle;
    }

    public void setDegrees(float radius, float angle) {
        r = radius;
        a = (float) Math.toRadians(angle);
    }
}
