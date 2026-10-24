package com.lianglliu.model.prize;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.hero.Mario;

import java.awt.image.BufferedImage;

public class FireFlower extends BoostItem {

    public FireFlower(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(150);
    }

    @Override
    public void onTouch(Mario mario, GameContext context) {
        mario.acquirePoints(getPoint());
        mario.growFire();
    }

    @Override
    public void updateLocation() {
    }
}
