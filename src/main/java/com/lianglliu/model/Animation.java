package com.lianglliu.model;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Cycles through a fixed list of frames, advancing every {@code ticksPerFrame} calls.
 */
public final class Animation {

    private final List<BufferedImage> frames;
    private final int ticksPerFrame;
    private int index;
    private int ticks;

    public Animation(List<BufferedImage> frames, int ticksPerFrame) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("an animation needs at least one frame");
        }
        this.frames = frames;
        this.ticksPerFrame = ticksPerFrame;
    }

    public BufferedImage nextFrame() {
        if (++ticks >= ticksPerFrame) {
            ticks = 0;
            index = (index + 1) % frames.size();
        }
        return frames.get(index);
    }
}
