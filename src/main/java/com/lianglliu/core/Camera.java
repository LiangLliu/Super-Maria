package com.lianglliu.core;

public class Camera {

    private static final double SHAKE_AMPLITUDE = 4;
    private static final int SHAKE_FRAMES = 60;

    private volatile double x, y;
    private int frameNumber;
    private boolean shaking;

    public Camera() {
        x = 0;
        y = 0;
        frameNumber = 0;
        shaking = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public synchronized void reset() {
        x = 0;
        y = 0;
        frameNumber = 0;
        shaking = false;
    }

    public synchronized void shakeCamera() {
        shaking = true;
        frameNumber = SHAKE_FRAMES;
    }

    public synchronized void moveCam(double xAmount, double yAmount) {
        if (shaking) {
            if (frameNumber > 0) {
                int direction = (frameNumber % 2 == 0) ? 1 : -1;
                x = x + SHAKE_AMPLITUDE * direction;
                frameNumber--;
            } else {
                shaking = false;
            }
        } else {
            x = x + xAmount;
            y = y + yAmount;
        }
    }
}
