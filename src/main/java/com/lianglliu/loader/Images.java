package com.lianglliu.loader;

import java.awt.image.BufferedImage;
import java.util.List;

import com.lianglliu.model.hero.MarioForm;
import com.lianglliu.model.hero.MarioSprites;

import static com.lianglliu.loader.ImageLoader.getSingleton;

public final class Images {

    public static final BufferedImage sprite = getSingleton().getSprite();
    public static final BufferedImage backgroundImage = getSingleton().loadImage("background.png");
    public static final BufferedImage[] brickShatterFrames = getSingleton().getBrickFrames();

    public static final BufferedImage heartIcon = getSingleton().loadImage("heart-icon.png");
    public static final BufferedImage coinIcon = getSingleton().getSubImage(sprite, 1, 5, 48, 48);
    public static final BufferedImage selectIcon = getSingleton().loadImage("select-icon.png");
    public static final BufferedImage startScreenImage = getSingleton().loadImage("start-screen.png");
    public static final BufferedImage helpScreenImage = getSingleton().loadImage("help-screen.png");
    public static final BufferedImage aboutScreenImage = getSingleton().loadImage("about-screen.png");
    public static final BufferedImage gameOverScreen = getSingleton().loadImage("game-over.png");

    public static final BufferedImage surpriseBrickStyle = getSingleton().getSubImage(sprite, 1, 2, 48, 48);
    public static final BufferedImage superMushroom = getSingleton().getSubImage(sprite, 2, 5, 48, 48);
    public static final BufferedImage oneUpMushroom = getSingleton().getSubImage(sprite, 3, 5, 48, 48);
    public static final BufferedImage fireFlower = getSingleton().getSubImage(sprite, 4, 5, 48, 48);
    public static final BufferedImage coin = getSingleton().getSubImage(sprite, 1, 5, 48, 48);
    public static final BufferedImage ordinaryBrick = getSingleton().getSubImage(sprite, 1, 1, 48, 48);
    public static final BufferedImage surpriseBrick = getSingleton().getSubImage(sprite, 2, 1, 48, 48);
    public static final BufferedImage groundBrick = getSingleton().getSubImage(sprite, 2, 2, 48, 48);
    public static final BufferedImage pipe = getSingleton().getSubImage(sprite, 3, 1, 96, 96);
    public static final BufferedImage goombaLeft = getSingleton().getSubImage(sprite, 2, 4, 48, 48);
    public static final BufferedImage goombaRight = getSingleton().getSubImage(sprite, 5, 4, 48, 48);
    public static final BufferedImage koopaLeft = getSingleton().getSubImage(sprite, 1, 3, 48, 64);
    public static final BufferedImage koopaRight = getSingleton().getSubImage(sprite, 4, 3, 48, 64);
    public static final BufferedImage endFlag = getSingleton().getSubImage(sprite, 5, 1, 48, 48);

    /** Indexed by {@link MarioForm#SMALL}, {@link MarioForm#SUPER} and {@link MarioForm#FIRE}. */
    public static final MarioSprites[] marioSprites = {
            sprites(MarioForm.SMALL),
            sprites(MarioForm.SUPER),
            sprites(MarioForm.FIRE),
    };

    private static MarioSprites sprites(int form) {
        var loader = getSingleton();
        return new MarioSprites(frames(loader.getLeftFrames(form)), frames(loader.getRightFrames(form)),
                loader.getSubImage(sprite, 3, 4, 24, 24));
    }

    private static List<BufferedImage> frames(BufferedImage[] frames) {
        return List.of(frames);
    }

    private Images() {
    }
}
