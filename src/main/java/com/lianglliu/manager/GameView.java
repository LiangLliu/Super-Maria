package com.lianglliu.manager;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * The port the Swing view consumes. Defined here (not in {@code view}) so the view
 * never imports the engine - the dependency points from engine to view only.
 */
public interface GameView {

    GameStatus status();

    int startOptionRow();

    int selectedMap();

    List<String> mapNames();

    int score();

    int lives();

    int coins();

    int remainingTime();

    Point cameraLocation();

    void drawMap(Graphics2D g2);
}
