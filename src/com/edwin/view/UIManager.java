package com.edwin.view;

import com.edwin.engine.GameEngine;
import com.edwin.loader.FontLoader;
import com.edwin.manager.GameStatus;
import com.edwin.manager.Images;

import javax.swing.*;
import java.awt.*;

public class UIManager extends JPanel {

    private final GameEngine engine;
    private final Font gameFont;
    private final MapSelection mapSelection;

    public UIManager(GameEngine engine, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        this.engine = engine;

        mapSelection = new MapSelection();
        gameFont = FontLoader.getMarioFont();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2 = (Graphics2D) g.create();
        var gameStatus = engine.getGameStatus();

        switch (gameStatus) {
            case START_SCREEN -> drawStartScreen(g2);
            case MAP_SELECTION -> drawMapSelectionScreen(g2);
            case ABOUT_SCREEN -> drawAboutScreen(g2);
            case HELP_SCREEN -> drawHelpScreen(g2);
            case GAME_OVER -> drawGameOverScreen(g2);
            default -> {
                var camLocation = engine.getCameraLocation();
                g2.translate(-camLocation.x, -camLocation.y);
                engine.drawMap(g2);
                g2.translate(camLocation.x, camLocation.y);

                drawPoints(g2);
                drawRemainingLives(g2);
                drawAcquiredCoins(g2);
                drawRemainingTime(g2);

                if (gameStatus == GameStatus.PAUSED) {
                    drawPauseScreen(g2);
                } else if (gameStatus == GameStatus.MISSION_PASSED) {
                    drawVictoryScreen(g2);
                }
            }
        }

        g2.dispose();
    }

    private void drawRemainingTime(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(25f));
        g2.setColor(Color.WHITE);
        var displayedStr = "TIME: " + engine.getRemainingTime();
        g2.drawString(displayedStr, 750, 50);
    }

    private void drawVictoryScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        var displayedStr = "YOU WON!";
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        g2.drawString(displayedStr, (getWidth() - stringLength) / 2, getHeight() / 2);
    }

    private void drawHelpScreen(Graphics2D g2) {
        g2.drawImage(Images.helpScreenImage, 0, 0, null);
    }

    private void drawAboutScreen(Graphics2D g2) {
        g2.drawImage(Images.aboutScreenImage, 0, 0, null);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.drawImage(Images.gameOverScreen, 0, 0, null);
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(new Color(130, 48, 48));
        var acquiredPoints = "Score: " + engine.getScore();
        var stringLength = g2.getFontMetrics().stringWidth(acquiredPoints);
        var stringHeight = g2.getFontMetrics().getHeight();
        g2.drawString(acquiredPoints, (getWidth() - stringLength) / 2, getHeight() - stringHeight * 2);
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        var displayedStr = "PAUSED";
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        g2.drawString(displayedStr, (getWidth() - stringLength) / 2, getHeight() / 2);
    }

    private void drawAcquiredCoins(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(30f));
        g2.setColor(Color.WHITE);
        var displayedStr = "" + engine.getCoins();
        g2.drawImage(Images.coinIcon, getWidth() - 115, 10, null);
        g2.drawString(displayedStr, getWidth() - 65, 50);
    }

    private void drawRemainingLives(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(30f));
        g2.setColor(Color.WHITE);
        var displayedStr = "" + engine.getRemainingLives();
        g2.drawImage(Images.heartIcon, 50, 10, null);
        g2.drawString(displayedStr, 100, 50);
    }

    private void drawPoints(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(25f));
        g2.setColor(Color.WHITE);
        var displayedStr = "Points: " + engine.getScore();
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        //g2.drawImage(coinIcon, 50, 10, null);
        g2.drawString(displayedStr, 300, 50);
    }

    private void drawStartScreen(Graphics2D g2) {
        int row = engine.getStartScreenSelection().getLineNumber();
        g2.drawImage(Images.startScreenImage, 0, 0, null);
        g2.drawImage(Images.selectIcon, 375, row * 70 + 440, null);
    }

    private void drawMapSelectionScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        mapSelection.draw(g2);
        int row = engine.getSelectedMap();
        int y_location = row * 100 + 300 - Images.selectIcon.getHeight();
        g2.drawImage(Images.selectIcon, 375, y_location, null);
    }

    public String selectMapViaMouse(Point mouseLocation) {
        return mapSelection.selectMap(mouseLocation);
    }

    public String selectMapViaKeyboard(int index) {
        return mapSelection.selectMap(index);
    }

    public int changeSelectedMap(int index, boolean up) {
        return mapSelection.changeSelect(index, up);
    }
}