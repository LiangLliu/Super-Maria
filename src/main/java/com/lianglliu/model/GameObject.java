package com.lianglliu.model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class GameObject {

    private double x, y;

    private double velX, velY;

    private Dimension dimension;

    private BufferedImage style;

    private double gravityAcc;

    private boolean falling;
    private boolean jumping;

    public GameObject(double x, double y, BufferedImage style) {
        // Direct field initialization: the constructor must not call overridable setters.
        this.x = x;
        this.y = y;
        this.style = style;
        if (style != null) {
            this.dimension = new Dimension(style.getWidth(), style.getHeight());
        }
        this.velX = 0;
        this.velY = 0;
        this.gravityAcc = 0.38;
        this.jumping = false;
        this.falling = true;
    }

    public void draw(Graphics g) {
        var style = getStyle();

        if (style != null) {
            g.drawImage(style, (int) x, (int) y, null);
        }
    }

    public void updateLocation() {
        if (jumping && velY <= 0) {
            jumping = false;
            falling = true;
        } else if (jumping) {
            velY = velY - gravityAcc;
            y = y - velY;
        }

        if (falling) {
            y = y + velY;
            velY = velY + gravityAcc;
        }

        x = x + velX;
    }

    public final void setLocation(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public final void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public final void setY(double y) {
        this.y = y;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public final void setDimension(int width, int height) {
        this.dimension = new Dimension(width, height);
    }

    public BufferedImage getStyle() {
        return style;
    }

    public final void setStyle(BufferedImage style) {
        this.style = style;
    }

    public double getVelX() {
        return velX;
    }

    public final void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public final void setVelY(double velY) {
        this.velY = velY;
    }

    public Rectangle getTopBounds() {
        return new Rectangle((int) x + dimension.width / 6, (int) y, 2 * dimension.width / 3, dimension.height / 2);
    }

    public Rectangle getBottomBounds() {
        return new Rectangle((int) x + dimension.width / 6, (int) y + dimension.height / 2, 2 * dimension.width / 3, dimension.height / 2);
    }

    public Rectangle getLeftBounds() {
        return new Rectangle((int) x, (int) y + dimension.height / 4, dimension.width / 4, dimension.height / 2);
    }

    public Rectangle getRightBounds() {
        return new Rectangle((int) x + 3 * dimension.width / 4, (int) y + dimension.height / 4, dimension.width / 4, dimension.height / 2);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, dimension.width, dimension.height);
    }

    public boolean isFalling() {
        return falling;
    }

    public final void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isJumping() {
        return jumping;
    }

    public final void setJumping(boolean jumping) {
        this.jumping = jumping;
    }
}
