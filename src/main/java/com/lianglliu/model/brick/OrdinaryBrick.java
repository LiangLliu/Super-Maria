package com.lianglliu.model.brick;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.prize.Prize;

import java.awt.image.BufferedImage;

public class OrdinaryBrick extends Brick {

    private static final int TICKS_PER_FRAME = 4; // 4 frames at 60 fps is roughly 270 ms

    private final BufferedImage[] shatterFrames;
    private int ticks;
    private int frameIndex = -1; // -1 while the brick is intact

    public OrdinaryBrick(double x, double y, BufferedImage style, BufferedImage[] shatterFrames) {
        super(x, y, style);
        this.shatterFrames = shatterFrames;
    }

    @Override
    public Prize reveal(GameContext context) {
        if (frameIndex >= 0 || !context.isMarioSuper()) {
            return null;
        }

        frameIndex = 0;
        setStyle(shatterFrames[0]);
        // The shatter sprite is 105x105 while the tile is 48x48, so center it on the tile.
        setLocation(getX() - 27, getY() - 27);
        context.revealBrick(this);
        return null;
    }

    public void animate() {
        if (frameIndex < 0 || isFinished() || ++ticks % TICKS_PER_FRAME != 0) {
            return;
        }
        frameIndex++;
        if (!isFinished()) {
            setStyle(shatterFrames[frameIndex]);
        }
    }

    public boolean isFinished() {
        return frameIndex >= shatterFrames.length;
    }
}
