package com.lianglliu.model;

import java.awt.image.BufferedImage;

public class EndFlag extends GameObject {

    private boolean touched = false;
    private int floorY = Integer.MAX_VALUE;

    public EndFlag(double x, double y, BufferedImage style) {
        super(x, y, style);
    }

    @Override
    public void updateLocation() {
        if (touched) {
            if (getY() + getDimension().getHeight() >= floorY) {
                setFalling(false);
                setVelY(0);
                setY(floorY - getDimension().getHeight());
            }
            super.updateLocation();
        }
    }

    /** Y of the top of the walkable floor, supplied by the map. */
    public void setFloorY(int floorY) {
        this.floorY = floorY;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }
}
