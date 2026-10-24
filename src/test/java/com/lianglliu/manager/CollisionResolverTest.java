package com.lianglliu.manager;

import com.lianglliu.core.Camera;
import com.lianglliu.loader.MapLoader;
import com.lianglliu.model.Map;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.Coin;
import com.lianglliu.testing.StubGameContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionResolverTest {

    private Map map;
    private Mario mario;
    private CollisionResolver resolver;
    private StubGameContext context;

    @BeforeEach
    void setUp() {
        map = MapLoader.getSingleton().createMap("maps/Map 1.png", 400);
        mario = map.getMario();
        resolver = new CollisionResolver(new Camera());
        context = new StubGameContext();
    }

    @Test
    void coinIsNotCollectedWithoutContact() {
        var coin = new Coin(9000, 100, null, 50);
        map.addRevealedPrize(coin);

        resolver.resolve(map, context);

        assertEquals(0, mario.getPoints());
        assertEquals(0, mario.getCoins());
        assertEquals(0, context.coins);
        assertTrue(map.getRevealedPrizes().contains(coin));
    }

    @Test
    void coinTouchingMarioIsCollectedExactlyOnce() {
        var coin = new Coin(mario.getX(), mario.getY(), null, 50);
        map.addRevealedPrize(coin);

        resolver.resolve(map, context);

        assertEquals(50, mario.getPoints());
        assertEquals(1, mario.getCoins());
        assertEquals(1, context.coins);
        assertFalse(map.getRevealedPrizes().contains(coin));
    }

    @Test
    void flagBonusIsAwardedOnceAndScalesWithHeight() {
        var flag = map.getEndPoint();
        mario.setY(400);
        mario.setX(flag.getX() + 10);

        resolver.resolve(map, context);
        int bonus = mario.getPoints();
        resolver.resolve(map, context);

        assertTrue(bonus > 0);
        assertEquals(bonus, mario.getPoints());
        assertTrue(flag.isTouched());
    }
}
