package com.lianglliu.engine;

/**
 * A raw player intent, independent of the game state. Interpreting it is the
 * input handler's job.
 */
public enum Input {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    CONFIRM,
    CANCEL,
    ACTION,
    STOP
}
