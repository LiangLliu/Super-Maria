package com.lianglliu.model.brick;

import com.lianglliu.loader.Images;
import com.lianglliu.testing.StubGameContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdinaryBrickTest {

    @Test
    void smallMarioCannotBreakABrick() {
        var context = new StubGameContext();
        var brick = new OrdinaryBrick(0, 0, null, Images.brickShatterFrames);

        assertNull(brick.reveal(context));
        assertTrue(context.revealedBricks.isEmpty());
        assertFalse(brick.isFinished());
    }

    @Test
    void brickShattersOnceAndRunsAllFrames() {
        var context = new StubGameContext();
        context.superMario = true;
        var brick = new OrdinaryBrick(0, 0, null, Images.brickShatterFrames);

        brick.reveal(context);
        brick.reveal(context);

        assertEquals(1, context.revealedBricks.size(), "a second hit must not restart the shatter");
        assertFalse(brick.isFinished());

        for (int frame = 0; frame < 15; frame++) {
            brick.animate();
        }
        assertFalse(brick.isFinished());

        brick.animate();
        assertTrue(brick.isFinished());
    }
}
