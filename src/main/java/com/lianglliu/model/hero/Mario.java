package com.lianglliu.model.hero;

import com.lianglliu.config.GameConfig;
import com.lianglliu.model.GameContext;
import com.lianglliu.model.GameObject;

public final class Mario extends GameObject {

    private int remainingLives;
    private int coins;
    private int points;
    private final MarioSprites[] sprites;
    private MarioForm marioForm;
    private boolean toRight = true;

    public Mario(double x, double y, MarioSprites[] sprites) {
        super(x, y, null);
        this.sprites = sprites;
        remainingLives = GameConfig.MARIO_DEFAULT_LIVES;
        setForm(MarioForm.SMALL);
    }

    @Override
    public void updateLocation() {
        // The style is part of the simulation, not of painting: the EDT only reads it.
        setStyle(marioForm.style(toRight, getVelX() != 0, getVelY() != 0));
        super.updateLocation();
    }

    public void jump(GameContext context) {
        if (!isJumping() && !isFalling()) {
            setJumping(true);
            setVelY(GameConfig.MARIO_JUMP_VELOCITY);
            context.playJump();
        }
    }

    public void move(boolean toRight) {
        setVelX(toRight ? GameConfig.MARIO_MOVE_SPEED : -GameConfig.MARIO_MOVE_SPEED);
        this.toRight = toRight;
    }

    /**
     * @return true when Mario lost a life
     */
    public boolean onTouchEnemy(GameContext context) {
        if (marioForm.isSmall()) {
            loseLife(context);
            return true;
        }
        context.shakeCamera();
        setForm(MarioForm.SMALL);
        return false;
    }

    /** Costs one life, e.g. after touching an enemy as small Mario or falling into a pit. */
    public void loseLife(GameContext context) {
        remainingLives--;
        context.playMarioDies();
    }

    /**
     * @return true when the form changed
     */
    public boolean growSuper() {
        if (!marioForm.isSmall()) {
            return false;
        }
        setForm(MarioForm.SUPER);
        return true;
    }

    /**
     * @return true when the form changed
     */
    public boolean growFire() {
        if (marioForm.isFire()) {
            return false;
        }
        setForm(MarioForm.FIRE);
        return true;
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

    /** True for the super and the fire form; bricks break for both. */
    public boolean isSuper() {
        return !marioForm.isSmall();
    }

    public boolean isFire() {
        return marioForm.isFire();
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

    private void setForm(int form) {
        marioForm = new MarioForm(sprites[form], form);
        setDimension(GameConfig.MARIO_SIZE, form == MarioForm.SMALL ? GameConfig.MARIO_SIZE : GameConfig.MARIO_TALL_SIZE);
        setStyle(marioForm.style(toRight, false, false));
    }
}
