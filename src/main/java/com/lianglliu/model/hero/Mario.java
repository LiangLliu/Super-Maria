package com.lianglliu.model.hero;

import java.awt.Graphics;

import com.lianglliu.config.GameConfig;
import com.lianglliu.engine.GameEngine;
import com.lianglliu.loader.ImageLoader;
import com.lianglliu.model.GameObject;
import com.lianglliu.view.Animation;

public class Mario extends GameObject {

    private int remainingLives;
    private int coins;
    private int points;
    private MarioForm marioForm;
    private boolean toRight = true;

    public Mario(double x, double y) {
        super(x, y, null);
        setDimension(GameConfig.MARIO_SIZE, GameConfig.MARIO_SIZE);

        remainingLives = GameConfig.MARIO_DEFAULT_LIVES;
        points = 0;
        coins = 0;

        var imageLoader = ImageLoader.getSingleton();
        var leftFrames = imageLoader.getLeftFrames(MarioForm.SMALL);
        var rightFrames = imageLoader.getRightFrames(MarioForm.SMALL);

        var animation = new Animation(leftFrames, rightFrames);
        marioForm = new MarioForm(animation, false, false);
        setStyle(marioForm.getCurrentStyle(toRight, false, false));
    }

    @Override
    public void draw(Graphics g) {
        boolean movingInX = (getVelX() != 0);
        boolean movingInY = (getVelY() != 0);

        setStyle(marioForm.getCurrentStyle(toRight, movingInX, movingInY));

        super.draw(g);
    }

    public void jump(GameEngine engine) {
        if (!isJumping() && !isFalling()) {
            setJumping(true);
            setVelY(GameConfig.MARIO_JUMP_VELOCITY);
            engine.playJump();
        }
    }

    public void move(boolean toRight) {
        setVelX(toRight ? GameConfig.MARIO_MOVE_SPEED : -GameConfig.MARIO_MOVE_SPEED);
        this.toRight = toRight;
    }

    public boolean onTouchEnemy(GameEngine engine) {

        if (!marioForm.isSuper() && !marioForm.isFire()) {
            remainingLives--;
            engine.playMarioDies();
            return true;
        } else {
            engine.shakeCamera();
            marioForm = marioForm.onTouchEnemy();
            setDimension(GameConfig.MARIO_SIZE, GameConfig.MARIO_SIZE);
            return false;
        }
    }

    public Fireball fire() {
        return marioForm.fire(toRight, getX(), getY());
    }

    public void acquireCoin() {
        coins++;
    }

    public void acquirePoints(int point) {
        points = points + point;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    public int getPoints() {
        return points;
    }

    public int getCoins() {
        return coins;
    }

    public MarioForm getMarioForm() {
        return marioForm;
    }

    public void setMarioForm(MarioForm marioForm) {
        this.marioForm = marioForm;
    }

    public boolean isSuper() {
        return marioForm.isSuper();
    }

    public boolean getToRight() {
        return toRight;
    }

    public void resetLocation() {
        setVelX(0);
        setVelY(0);
        setX(50);
        setJumping(false);
        setFalling(true);
    }
}
