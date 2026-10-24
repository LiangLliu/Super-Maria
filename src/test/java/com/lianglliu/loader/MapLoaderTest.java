package com.lianglliu.loader;

import com.lianglliu.model.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapLoaderTest {

    private static Map load(String name) {
        var map = MapLoader.getSingleton().createMap("maps/" + name, 400);
        assertNotNull(map, name);
        return map;
    }

    /** Guards the map format: one pixel per tile, ground pixels define the floor, unknown colors are reported. */
    @Test
    void map1Structure() {
        var map = load("Map 1.png");

        assertEquals(172, map.getBricks().size());
        assertEquals(20, map.getEnemies().size());
        assertEquals(624.0, map.getBottomBorder(), "floor is derived from the ground pixels");
        assertNotNull(map.getMario());
        assertNotNull(map.getEndPoint());
    }

    @Test
    void map2Structure() {
        var map = load("Map 2.png");

        assertEquals(138, map.getBricks().size());
        assertEquals(20, map.getEnemies().size());
        assertEquals(624.0, map.getBottomBorder());
        assertNotNull(map.getMario());
        assertNotNull(map.getEndPoint());
    }

    @Test
    void missingMapIsRejected() {
        assertNull(MapLoader.getSingleton().createMap("maps/does-not-exist.png", 400));
    }
}
