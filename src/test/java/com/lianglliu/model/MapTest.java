package com.lianglliu.model;

import com.lianglliu.loader.MapLoader;
import com.lianglliu.model.hero.Fireball;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapTest {

    @Test
    void fireballsExpireOnceTheyLeaveTheWorld() {
        var map = MapLoader.getSingleton().createMap("maps/Map 1.png", 400);
        map.addFireball(new Fireball(12000, 300, null, true));
        map.addFireball(new Fireball(600, 300, null, true));

        for (int frame = 0; frame < 40; frame++) {
            map.updateLocations();
        }

        assertEquals(1, map.getFireballs().size(), "only the fireball still inside the world remains");
    }
}
