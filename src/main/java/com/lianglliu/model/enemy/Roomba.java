package com.lianglliu.model.enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Roomba extends Enemy {

    private BufferedImage rightImage;

    public Roomba(double x, double y, BufferedImage style) {
        super(x, y, style);
        setVelX(3);
    }

    @Override
    public void draw(Graphics g) {
        if (getVelX() > 0) {
            g.drawImage(rightImage, (int) getX(), (int) getY(), null);
        } else {
            super.draw(g);
        }

    }

    public void setRightImage(BufferedImage rightImage) {
        this.rightImage = rightImage;
    }
}
