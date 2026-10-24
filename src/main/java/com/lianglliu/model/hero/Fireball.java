package com.lianglliu.model.hero;

import com.lianglliu.config.GameConfig;
import com.lianglliu.model.GameObject;

import java.awt.image.BufferedImage;

public class Fireball extends GameObject {

    public Fireball(double x, double y, BufferedImage style, boolean toRight) {
        super(x, y, style);
        setDimension(24, 24);
        setFalling(false);
        setJumping(false);
        setVelX(toRight ? GameConfig.FIREBALL_SPEED : -GameConfig.FIREBALL_SPEED);
    }
}
