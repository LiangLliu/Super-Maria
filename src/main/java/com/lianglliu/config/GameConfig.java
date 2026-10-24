package com.lianglliu.config;

/**
 * Central place for the game's tuning constants.
 */
public final class GameConfig {

    // Window size
    public static final int GAME_WIDTH = 1268;
    public static final int GAME_HEIGHT = 708;

    // Game loop
    public static final double TARGET_FPS = 60.0;
    public static final double NANOSECONDS_PER_SECOND = 1_000_000_000.0;
    /** Upper bound on fixed-step updates per frame, so a long stall cannot trigger a catch-up spiral. */
    public static final int MAX_UPDATES_PER_FRAME = 5;
    public static final int TIMER_UPDATE_INTERVAL_MS = 1000;

    // Mario
    public static final double MARIO_MOVE_SPEED = 5.0;
    public static final double MARIO_JUMP_VELOCITY = 10.0;
    public static final int MARIO_DEFAULT_LIVES = 3;
    public static final int MARIO_SIZE = 48;

    // Camera
    public static final int CAMERA_FOLLOW_OFFSET = 600;

    // Physics
    public static final int PIXEL_MULTIPLIER = 48;
    public static final double BOOST_ITEM_SPEED = 2.0;
    public static final double FIREBALL_SPEED = 10.0;

    // UI
    public static final class UI {
        public static final float FONT_SIZE_SMALL = 25f;
        public static final float FONT_SIZE_MEDIUM = 30f;
        public static final float FONT_SIZE_LARGE = 50f;

        public static final int HEART_ICON_X = 50;
        public static final int HEART_ICON_Y = 10;
        public static final int LIVES_TEXT_X = 100;
        public static final int LIVES_TEXT_Y = 50;

        public static final int COIN_ICON_X_OFFSET = 115;
        public static final int COIN_ICON_Y = 10;
        public static final int COIN_TEXT_X_OFFSET = 65;
        public static final int COIN_TEXT_Y = 50;

        public static final int POINTS_TEXT_X = 300;
        public static final int POINTS_TEXT_Y = 50;

        public static final int TIME_TEXT_X = 750;
        public static final int TIME_TEXT_Y = 50;

        public static final int SELECT_ICON_X = 375;
        public static final int START_SCREEN_OPTION_Y_OFFSET = 440;
        public static final int START_SCREEN_OPTION_Y_SPACING = 70;

        public static final int MAP_SELECTION_Y_OFFSET = 300;
        public static final int MAP_SELECTION_Y_SPACING = 100;
    }

    // Scoring
    public static final class Scoring {
        public static final int ENEMY_STOMP_POINTS = 100;
        public static final int FLAG_HEIGHT_MULTIPLIER = 2;
    }

    private GameConfig() {
    }
}
