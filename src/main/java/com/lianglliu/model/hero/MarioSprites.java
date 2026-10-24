package com.lianglliu.model.hero;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Sprite frames of one Mario form.
 * Frame order is: 0 = jump, 1 = stand, 2.. = run cycle.
 */
public record MarioSprites(List<BufferedImage> leftFrames, List<BufferedImage> rightFrames, BufferedImage fireball) {

    public MarioSprites {
        leftFrames = List.copyOf(leftFrames);
        rightFrames = List.copyOf(rightFrames);
    }
}
