package com.lianglliu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import com.lianglliu.config.GameConfig;
import com.lianglliu.engine.GameEngine;
import com.lianglliu.loader.FontLoader;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.manager.Images;

public class UIManager extends JPanel {

    private final GameEngine engine;
    private final Font gameFont;
    private final MapSelection mapSelection;
    private final UIRenderer uiRenderer;

    public UIManager(GameEngine engine, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        this.engine = engine;

        mapSelection = new MapSelection();
        gameFont = FontLoader.getMarioFont();
        uiRenderer = new UIRenderer(gameFont);
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

                uiRenderer.drawPoints(g2, engine.getScore());
                uiRenderer.drawLives(g2, engine.getRemainingLives());
                uiRenderer.drawCoins(g2, engine.getCoins(), getWidth());
                uiRenderer.drawTime(g2, engine.getRemainingTime());

                if (gameStatus == GameStatus.PAUSED) {
                    drawPauseScreen(g2);
                } else if (gameStatus == GameStatus.MISSION_PASSED) {
                    drawVictoryScreen(g2);
                }
            }
        }

        g2.dispose();
    }

    private void drawVictoryScreen(Graphics2D g2) {
        uiRenderer.drawCenteredText(g2, "YOU WON!", GameConfig.UI.FONT_SIZE_LARGE, Color.WHITE, getHeight() / 2, getWidth());
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
        uiRenderer.drawCenteredText(g2, "PAUSED", GameConfig.UI.FONT_SIZE_LARGE, Color.WHITE, getHeight() / 2, getWidth());
    }

    private void drawStartScreen(Graphics2D g2) {
        int row = engine.getStartScreenSelection().getLineNumber();
        g2.drawImage(Images.startScreenImage, 0, 0, null);
        uiRenderer.drawStartScreenSelectIcon(g2, row);
    }

    private void drawMapSelectionScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(GameConfig.UI.FONT_SIZE_LARGE));
        g2.setColor(Color.WHITE);
        mapSelection.draw(g2);
        uiRenderer.drawMapSelectionIcon(g2, engine.getSelectedMap());
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