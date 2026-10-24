package com.lianglliu.model.hero;

import com.lianglliu.loader.Images;
import com.lianglliu.testing.StubGameContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarioTest {

    private static Mario newMario() {
        return new Mario(0, 0, Images.marioSprites);
    }

    @Test
    void powerUpsChangeFormAndSize() {
        var mario = newMario();
        assertEquals(48, mario.getDimension().height);
        assertFalse(mario.isSuper());

        assertTrue(mario.growSuper());
        assertTrue(mario.isSuper());
        assertEquals(96, mario.getDimension().height);
        assertFalse(mario.growSuper(), "already super");

        assertTrue(mario.growFire());
        assertTrue(mario.isFire());
        assertFalse(mario.growFire(), "already fire");
        assertEquals(96, mario.getDimension().height);
    }

    @Test
    void takingDamageShrinksBeforeCostingALife() {
        var context = new StubGameContext();
        var mario = newMario();
        mario.growFire();

        assertFalse(mario.onTouchEnemy(context));
        assertFalse(mario.isSuper());
        assertFalse(mario.isFire());
        assertEquals(3, mario.getRemainingLives());
        assertEquals(1, context.cameraShakes);

        assertTrue(mario.onTouchEnemy(context));
        assertEquals(2, mario.getRemainingLives());
        assertEquals(1, context.deaths);
    }

    @Test
    void fireballsRequireTheFireForm() {
        var mario = newMario();
        assertNull(mario.fire());

        mario.growFire();
        assertNotNull(mario.fire());
    }
}
