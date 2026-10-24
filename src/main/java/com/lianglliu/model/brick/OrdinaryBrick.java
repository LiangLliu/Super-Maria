package com.lianglliu.model.brick;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.loader.ImageLoader;
import com.lianglliu.model.prize.Prize;

import java.awt.image.BufferedImage;

public class OrdinaryBrick extends Brick {

    private static final BufferedImage[] SHATTER_FRAMES = ImageLoader.getSingleton().getBrickFrames();
    private static final int TICKS_PER_FRAME = 4; // 4 frames at 60 fps is roughly 270 ms

    private int ticks;
    private int frameIndex = -1; // -1 while the brick is intact

    public OrdinaryBrick(double x, double y, BufferedImage style) {
        super(x, y, style);
    }

    @Override
    public Prize reveal(GameEngine engine) {
        if (frameIndex >= 0 || !engine.getMapManager().isMarioSuper()) {
            return null;
        }

        frameIndex = 0;
        setStyle(SHATTER_FRAMES[0]);
        // The shatter sprite is 105x105 while the tile is 48x48, so center it on the tile.
        setLocation(getX() - 27, getY() - 27);
        engine.getMapManager().addRevealedBrick(this);
        return null;
    }

    public void animate() {
        if (frameIndex < 0 || isFinished() || ++ticks % TICKS_PER_FRAME != 0) {
            return;
        }
        frameIndex++;
        if (!isFinished()) {
            setStyle(SHATTER_FRAMES[frameIndex]);
        }
    }

    public boolean isFinished() {
        return frameIndex >= SHATTER_FRAMES.length;
    }
}
