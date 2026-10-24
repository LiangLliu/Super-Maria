package com.lianglliu.model.prize;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.hero.Mario;

import java.awt.image.BufferedImage;

public class SuperMushroom extends BoostItem {

    public SuperMushroom(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(125);
    }

    @Override
    public void onTouch(Mario mario, GameContext context) {
        mario.acquirePoints(getPoint());
        if (mario.growSuper()) {
            context.playSuperMushroom();
        }
    }
}
