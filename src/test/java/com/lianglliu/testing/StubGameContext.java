package com.lianglliu.testing;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.brick.OrdinaryBrick;

import java.util.ArrayList;
import java.util.List;

/** Records every callback so tests can assert on side effects without any audio or UI. */
public final class StubGameContext implements GameContext {

    public int jumps;
    public int coins;
    public int stomps;
    public int deaths;
    public int oneUps;
    public int superMushrooms;
    public int cameraShakes;
    public boolean superMario;
    public final List<OrdinaryBrick> revealedBricks = new ArrayList<>();

    @Override
    public void playJump() {
        jumps++;
    }

    @Override
    public void playCoin() {
        coins++;
    }

    @Override
    public void playStomp() {
        stomps++;
    }

    @Override
    public void playFireball() {
    }

    @Override
    public void playOneUp() {
        oneUps++;
    }

    @Override
    public void playSuperMushroom() {
        superMushrooms++;
    }

    @Override
    public void playMarioDies() {
        deaths++;
    }

    @Override
    public void shakeCamera() {
        cameraShakes++;
    }

    @Override
    public void resetCamera() {
    }

    @Override
    public boolean isMarioSuper() {
        return superMario;
    }

    @Override
    public void revealBrick(OrdinaryBrick brick) {
        revealedBricks.add(brick);
    }
}
