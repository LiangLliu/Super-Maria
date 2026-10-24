package com.lianglliu.model.brick;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.model.GameObject;
import com.lianglliu.model.prize.Prize;

import java.awt.image.BufferedImage;

public abstract class Brick extends GameObject {

    public Brick(double x, double y, BufferedImage style) {
        super(x, y, style);
        setDimension(48, 48);
    }

    /**
     * Called when Mario hits the brick from below.
     *
     * @return the prize that popped out of the brick, or null
     */
    public Prize reveal(GameEngine engine) {
        return null;
    }
}
