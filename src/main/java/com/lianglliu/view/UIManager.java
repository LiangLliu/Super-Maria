package com.lianglliu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import com.lianglliu.config.GameConfig;
import com.lianglliu.manager.GameView;
import com.lianglliu.loader.FontLoader;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.loader.Images;

public class UIManager extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int MAP_TITLE_Y = 150;

    private final transient GameView view;
    private final Font gameFont;
    private final transient UIRenderer uiRenderer;
    private Throwable renderFailure;

    public UIManager(GameView view, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        this.view = view;

        
        gameFont = FontLoader.getMarioFont();
        uiRenderer = new UIRenderer(gameFont);
    }

    @Override
    public void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            render(g);
        } catch (RuntimeException | LinkageError e) {
            drawRenderFailure(g);
            reportOnce(e);
        }
    }

    private void render(Graphics g) {
        var g2 = (Graphics2D) g.create();
        try {
            var gameStatus = view.status();

            switch (gameStatus) {
                case START_SCREEN -> drawStartScreen(g2);
                case MAP_SELECTION -> drawMapSelectionScreen(g2);
                case ABOUT_SCREEN -> drawAboutScreen(g2);
                case HELP_SCREEN -> drawHelpScreen(g2);
                case GAME_OVER -> drawGameOverScreen(g2);
                default -> {
                    var camLocation = view.cameraLocation();
                    g2.translate(-camLocation.x, -camLocation.y);
                    view.drawMap(g2);
                    g2.translate(camLocation.x, camLocation.y);

                    uiRenderer.drawPoints(g2, view.score());
                    uiRenderer.drawLives(g2, view.lives());
                    uiRenderer.drawCoins(g2, view.coins(), getWidth());
                    uiRenderer.drawTime(g2, view.remainingTime());

                    if (gameStatus == GameStatus.PAUSED) {
                        drawPauseScreen(g2);
                    } else if (gameStatus == GameStatus.MISSION_PASSED) {
                        drawVictoryScreen(g2);
                    }
                }
            }
        } finally {
            g2.dispose();
        }
    }

    /** Swing retries painting forever, so a broken frame is reported once and shown as text. */
    private void reportOnce(Throwable failure) {
        if (renderFailure != null) {
            return;
        }
        renderFailure = failure;
        failure.printStackTrace();
    }

    private void drawRenderFailure(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        g.drawString("Render failed - see console for the stack trace", 20, 40);
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
        var acquiredPoints = "Score: " + view.score();
        var stringLength = g2.getFontMetrics().stringWidth(acquiredPoints);
        var stringHeight = g2.getFontMetrics().getHeight();
        g2.drawString(acquiredPoints, (getWidth() - stringLength) / 2, getHeight() - stringHeight * 2);
    }

    private void drawPauseScreen(Graphics2D g2) {
        uiRenderer.drawCenteredText(g2, "PAUSED", GameConfig.UI.FONT_SIZE_LARGE, Color.WHITE, getHeight() / 2, getWidth());
    }

    private void drawStartScreen(Graphics2D g2) {
        int row = view.startOptionRow();
        g2.drawImage(Images.startScreenImage, 0, 0, null);
        uiRenderer.drawStartScreenSelectIcon(g2, row);
    }

    private void drawMapSelectionScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setFont(gameFont.deriveFont(GameConfig.UI.FONT_SIZE_LARGE));

        var title = "Select a Map";
        g2.setColor(Color.YELLOW);
        g2.drawString(title, (getWidth() - g2.getFontMetrics().stringWidth(title)) / 2, MAP_TITLE_Y);

        g2.setColor(Color.WHITE);
        List<String> names = view.mapNames();
        for (int i = 0; i < names.size(); i++) {
            String label = names.get(i).substring(0, names.get(i).lastIndexOf('.'));
            int y = GameConfig.UI.MAP_SELECTION_Y_OFFSET + i * GameConfig.UI.MAP_SELECTION_Y_SPACING;
            g2.drawString(label, (getWidth() - g2.getFontMetrics().stringWidth(label)) / 2, y);
        }
        uiRenderer.drawMapSelectionIcon(g2, view.selectedMap());
    }
}
