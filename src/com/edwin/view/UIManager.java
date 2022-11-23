package com.edwin.view;

import com.edwin.manager.GameEngine;
import com.edwin.manager.GameStatus;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class UIManager extends JPanel {

    private GameEngine engine;
    private Font gameFont;
    private BufferedImage startScreenImage, aboutScreenImage, helpScreenImage, gameOverScreen;
    private BufferedImage heartIcon;
    private BufferedImage coinIcon;
    private BufferedImage selectIcon;
    private MapSelection mapSelection;

    public UIManager(GameEngine engine, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        this.engine = engine;
        var loader = engine.getImageLoader();

        mapSelection = new MapSelection();

        var sprite = loader.loadImage("/sprite.png");
        this.heartIcon = loader.loadImage("/heart-icon.png");
        this.coinIcon = loader.getSubImage(sprite, 1, 5, 48, 48);
        this.selectIcon = loader.loadImage("/select-icon.png");
        this.startScreenImage = loader.loadImage("/start-screen.png");
        this.helpScreenImage = loader.loadImage("/help-screen.png");
        this.aboutScreenImage = loader.loadImage("/about-screen.png");
        this.gameOverScreen = loader.loadImage("/game-over.png");


        try {
            InputStream in = getClass().getResourceAsStream("/com/edwin/assets/font/mario-font.ttf");
            gameFont = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (FontFormatException | IOException e) {
            gameFont = new Font("Verdana", Font.PLAIN, 12);
            e.printStackTrace();
        }
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
                Point camLocation = engine.getCameraLocation();
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
        g2.drawImage(helpScreenImage, 0, 0, null);
    }

    private void drawAboutScreen(Graphics2D g2) {
        g2.drawImage(aboutScreenImage, 0, 0, null);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.drawImage(gameOverScreen, 0, 0, null);
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(new Color(130, 48, 48));
        var acquiredPoints = "Score: " + engine.getScore();
        int stringLength = g2.getFontMetrics().stringWidth(acquiredPoints);
        int stringHeight = g2.getFontMetrics().getHeight();
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
        g2.drawImage(coinIcon, getWidth() - 115, 10, null);
        g2.drawString(displayedStr, getWidth() - 65, 50);
    }

    private void drawRemainingLives(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(30f));
        g2.setColor(Color.WHITE);
        var displayedStr = "" + engine.getRemainingLives();
        g2.drawImage(heartIcon, 50, 10, null);
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
        g2.drawImage(startScreenImage, 0, 0, null);
        g2.drawImage(selectIcon, 375, row * 70 + 440, null);
    }

    private void drawMapSelectionScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        mapSelection.draw(g2);
        int row = engine.getSelectedMap();
        int y_location = row * 100 + 300 - selectIcon.getHeight();
        g2.drawImage(selectIcon, 375, y_location, null);
    }

    public String selectMapViaMouse(Point mouseLocation) {
        return mapSelection.selectMap(mouseLocation);
    }

    public String selectMapViaKeyboard(int index) {
        return mapSelection.selectMap(index);
    }

    public int changeSelectedMap(int index, boolean up) {
        return mapSelection.changeSelectedMap(index, up);
    }
}