package com.lianglliu.model.prize;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.model.hero.Mario;

import java.awt.image.BufferedImage;

public class OneUpMushroom extends BoostItem{

    public OneUpMushroom(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(200);
    }

    @Override
    public void onTouch(Mario mario, GameEngine engine) {
        mario.acquirePoints(getPoint());
        mario.setRemainingLives(mario.getRemainingLives() + 1);
        engine.playOneUp();
    }
}
