package com.lianglliu.manager;

import java.awt.Color;

/**
 * Legend of the map images: one pixel equals one 48x48 tile of the level.
 * Every color present in a map image must be listed here; the loader reports unknown colors.
 */
public final class Colors {

    /** Empty tile. */
    public static final int empty = Color.BLACK.getRGB();
    /** Editor canvas border. Not solid and not drawn - it only frames the level. */
    public static final int border = Color.WHITE.getRGB();
    /** Walkable floor. Its art is already part of background.png, so it only defines the floor height. */
    public static final int ground = new Color(0x7F3300).getRGB();
    public static final int mario = new Color(160, 160, 160).getRGB();
    public static final int ordinaryBrick = new Color(0, 0, 255).getRGB();
    public static final int surpriseBrick = new Color(255, 255, 0).getRGB();
    public static final int groundBrick = new Color(255, 0, 0).getRGB();
    public static final int pipe = new Color(0, 255, 0).getRGB();
    public static final int goomba = new Color(0, 255, 255).getRGB();
    public static final int koopa = new Color(255, 0, 255).getRGB();
    public static final int end = new Color(160, 0, 160).getRGB();

    private Colors() {
    }
}
