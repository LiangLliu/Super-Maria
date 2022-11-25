package com.edwin.manager;

import java.awt.image.BufferedImage;

import static com.edwin.loader.ImageLoader.getSingleton;

public final class Images {

    public static final BufferedImage backgroundImage = getSingleton().loadImage("/background.png");
    public static final BufferedImage sprite = getSingleton().loadImage("/sprite.png");

    public static final BufferedImage heartIcon = getSingleton().loadImage("/heart-icon.png");
    public static final BufferedImage coinIcon = getSingleton().getSubImage(sprite, 1, 5, 48, 48);
    public static final BufferedImage selectIcon = getSingleton().loadImage("/select-icon.png");
    public static final BufferedImage startScreenImage = getSingleton().loadImage("/start-screen.png");
    public static final BufferedImage helpScreenImage = getSingleton().loadImage("/help-screen.png");
    public static final BufferedImage aboutScreenImage = getSingleton().loadImage("/about-screen.png");
    public static final BufferedImage gameOverScreen = getSingleton().loadImage("/game-over.png");

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


}
