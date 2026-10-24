package com.lianglliu.model.hero;

import com.lianglliu.model.Animation;

import java.awt.image.BufferedImage;

/**
 * Visual state of one Mario form. Mario replaces the whole instance when the form changes,
 * so a form is immutable.
 */
public final class MarioForm {

    public static final int SMALL = 0, SUPER = 1, FIRE = 2;

    private static final int JUMP_FRAME = 0;
    private static final int STAND_FRAME = 1;
    private static final int FIRST_RUN_FRAME = 2;
    private static final int RUN_TICKS_PER_FRAME = 6;

    private final int form;
    private final MarioSprites sprites;
    private final Animation runLeft;
    private final Animation runRight;

    MarioForm(MarioSprites sprites, int form) {
        this.sprites = sprites;
        this.form = form;
        this.runLeft = new Animation(sprites.leftFrames().subList(FIRST_RUN_FRAME, sprites.leftFrames().size()), RUN_TICKS_PER_FRAME);
        this.runRight = new Animation(sprites.rightFrames().subList(FIRST_RUN_FRAME, sprites.rightFrames().size()), RUN_TICKS_PER_FRAME);
    }

    public BufferedImage style(boolean toRight, boolean movingInX, boolean movingInY) {
        if (movingInY) {
            return frame(toRight, JUMP_FRAME);
        }
        if (movingInX) {
            return (toRight ? runRight : runLeft).nextFrame();
        }
        return frame(toRight, STAND_FRAME);
    }

    public Fireball fire(boolean toRight, double x, double y) {
        if (form != FIRE) {
            return null;
        }
        return new Fireball(x, y + 48, sprites.fireball(), toRight);
    }

    private BufferedImage frame(boolean toRight, int index) {
        return (toRight ? sprites.rightFrames() : sprites.leftFrames()).get(index);
    }

    public boolean isSmall() {
        return form == SMALL;
    }

    public boolean isFire() {
        return form == FIRE;
    }
}
