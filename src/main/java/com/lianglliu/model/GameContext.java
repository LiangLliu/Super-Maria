package com.lianglliu.model;

import com.lianglliu.model.brick.OrdinaryBrick;

/**
 * The slice of the game entities are allowed to talk to. Keeping it in the model package
 * lets entities stay free of engine, manager, view and loader dependencies.
 */
public interface GameContext {

    void playJump();

    void playCoin();

    void playStomp();

    void playFireball();

    void playOneUp();

    void playSuperMushroom();

    void playMarioDies();

    void shakeCamera();

    void resetCamera();

    boolean isMarioSuper();

    void revealBrick(OrdinaryBrick brick);
}
