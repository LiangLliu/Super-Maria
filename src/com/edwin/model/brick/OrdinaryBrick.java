package com.edwin.model.brick;

import com.edwin.manager.GameEngine;
import com.edwin.model.prize.Prize;
import com.edwin.view.Animation;
import com.edwin.view.ImageLoader;

import java.awt.image.BufferedImage;

public class OrdinaryBrick extends Brick {

    private Animation animation;
    private boolean breaking;
    private int frames;

    public OrdinaryBrick(double x, double y, BufferedImage style) {
        super(x, y, style);
        setBreakable(true);
        setEmpty(true);

        setAnimation();
        breaking = false;
        frames = animation.getLeftFrames().length;
    }

    private void setAnimation() {
        var imageLoader = new ImageLoader();
        var leftFrames = imageLoader.getBrickFrames();

        animation = new Animation(leftFrames, leftFrames);
    }

    @Override
    public Prize reveal(GameEngine engine) {
        var manager = engine.getMapManager();
        if (!manager.getMario().isSuper())
            return null;

        breaking = true;
        manager.addRevealedBrick(this);

        double newX = getX() - 27, newY = getY() - 27;
        setLocation(newX, newY);

        return null;
    }

    public int getFrames() {
        return frames;
    }

    public void animate() {
        if (breaking) {
            setStyle(animation.animate(3, true));
            frames--;
        }
    }
}
