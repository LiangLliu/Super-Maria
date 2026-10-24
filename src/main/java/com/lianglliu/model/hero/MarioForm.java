package com.lianglliu.model.hero;

import com.lianglliu.loader.ImageLoader;
import com.lianglliu.view.Animation;

import java.awt.image.BufferedImage;

public class MarioForm {

    public static final int SMALL = 0, SUPER = 1, FIRE = 2;

    private final Animation animation;
    private final boolean isSuper;
    private final boolean isFire; //note: fire form has priority over super form
    private final BufferedImage fireballStyle;

    public MarioForm(Animation animation, boolean isSuper, boolean isFire) {
        this.animation = animation;
        this.isSuper = isSuper;
        this.isFire = isFire;

        var imageLoader = ImageLoader.getSingleton();
        fireballStyle = imageLoader.getSubImage(imageLoader.getSprite(), 3, 4, 24, 24);
    }

    public BufferedImage getCurrentStyle(boolean toRight, boolean movingInX, boolean movingInY) {

        BufferedImage style;

        if (movingInY && toRight) {
            style = animation.getRightFrames()[0];
        } else if (movingInY) {
            style = animation.getLeftFrames()[0];
        } else if (movingInX) {
            style = animation.animate(5, toRight);
        } else {
            if (toRight) {
                style = animation.getRightFrames()[1];
            } else {
                style = animation.getLeftFrames()[1];
            }
        }

        return style;
    }

    public MarioForm onTouchEnemy() {

        var imageLoader = ImageLoader.getSingleton();

        var leftFrames = imageLoader.getLeftFrames(SMALL);
        var rightFrames = imageLoader.getRightFrames(SMALL);

        var newAnimation = new Animation(leftFrames, rightFrames);

        return new MarioForm(newAnimation, false, false);
    }

    public Fireball fire(boolean toRight, double x, double y) {
        if (isFire) {
            return new Fireball(x, y + 48, fireballStyle, toRight);
        }
        return null;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public boolean isFire() {
        return isFire;
    }
}
