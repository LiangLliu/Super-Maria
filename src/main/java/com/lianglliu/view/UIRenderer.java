package com.lianglliu.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.lianglliu.config.GameConfig;
import com.lianglliu.manager.Images;

/**
 * UI渲染器 - 集中处理UI绘制逻辑，减少重复代码
 */
public class UIRenderer {
    
    private final Font gameFont;
    
    public UIRenderer(Font gameFont) {
        this.gameFont = gameFont;
    }
    
    /**
     * 绘制居中文本
     */
    public void drawCenteredText(Graphics2D g2, String text, float fontSize, Color color, int yPosition, int containerWidth) {
        g2.setFont(gameFont.deriveFont(fontSize));
        g2.setColor(color);
        int stringLength = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (containerWidth - stringLength) / 2, yPosition);
    }
    
    /**
     * 绘制带图标的数值
     */
    public void drawIconWithValue(Graphics2D g2, Object icon, String value, int iconX, int iconY, int textX, int textY, float fontSize) {
        g2.setFont(gameFont.deriveFont(fontSize));
        g2.setColor(Color.WHITE);
        
        if (icon instanceof java.awt.image.BufferedImage image) {
            g2.drawImage(image, iconX, iconY, null);
        }
        g2.drawString(value, textX, textY);
    }
    
    /**
     * 绘制生命值
     */
    public void drawLives(Graphics2D g2, int lives) {
        drawIconWithValue(g2, Images.heartIcon, String.valueOf(lives),
                GameConfig.UI.HEART_ICON_X, GameConfig.UI.HEART_ICON_Y,
                GameConfig.UI.LIVES_TEXT_X, GameConfig.UI.LIVES_TEXT_Y,
                GameConfig.UI.FONT_SIZE_MEDIUM);
    }
    
    /**
     * 绘制金币数量
     */
    public void drawCoins(Graphics2D g2, int coins, int containerWidth) {
        drawIconWithValue(g2, Images.coinIcon, String.valueOf(coins),
                containerWidth - GameConfig.UI.COIN_ICON_X_OFFSET, GameConfig.UI.COIN_ICON_Y,
                containerWidth - GameConfig.UI.COIN_TEXT_X_OFFSET, GameConfig.UI.COIN_TEXT_Y,
                GameConfig.UI.FONT_SIZE_MEDIUM);
    }
    
    /**
     * 绘制分数
     */
    public void drawPoints(Graphics2D g2, int points) {
        g2.setFont(gameFont.deriveFont(GameConfig.UI.FONT_SIZE_SMALL));
        g2.setColor(Color.WHITE);
        g2.drawString("Points: " + points, GameConfig.UI.POINTS_TEXT_X, GameConfig.UI.POINTS_TEXT_Y);
    }
    
    /**
     * 绘制剩余时间
     */
    public void drawTime(Graphics2D g2, int time) {
        g2.setFont(gameFont.deriveFont(GameConfig.UI.FONT_SIZE_SMALL));
        g2.setColor(Color.WHITE);
        g2.drawString("TIME: " + time, GameConfig.UI.TIME_TEXT_X, GameConfig.UI.TIME_TEXT_Y);
    }
    
    /**
     * 绘制选择图标
     */
    public void drawSelectIcon(Graphics2D g2, int row, int yOffset, int ySpacing) {
        int yLocation = row * ySpacing + yOffset - Images.selectIcon.getHeight();
        g2.drawImage(Images.selectIcon, GameConfig.UI.SELECT_ICON_X, yLocation, null);
    }
    
    /**
     * 绘制开始屏幕选择图标
     */
    public void drawStartScreenSelectIcon(Graphics2D g2, int row) {
        int yLocation = row * GameConfig.UI.START_SCREEN_OPTION_Y_SPACING + GameConfig.UI.START_SCREEN_OPTION_Y_OFFSET;
        g2.drawImage(Images.selectIcon, GameConfig.UI.SELECT_ICON_X, yLocation, null);
    }
    
    /**
     * 绘制地图选择图标
     */
    public void drawMapSelectionIcon(Graphics2D g2, int selectedMap) {
        drawSelectIcon(g2, selectedMap, GameConfig.UI.MAP_SELECTION_Y_OFFSET, GameConfig.UI.MAP_SELECTION_Y_SPACING);
    }
} 