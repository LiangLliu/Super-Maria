package com.lianglliu.loader;

import com.lianglliu.model.Map;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        assertEquals(624, map.getBaseFloor(), "floor is derived from the ground pixels");
        assertNotNull(map.getMario());
        assertNotNull(map.getEndPoint());
    }

    @Test
    void map2Structure() {
        var map = load("Map 2.png");

        assertEquals(138, map.getBricks().size());
        assertEquals(20, map.getEnemies().size());
        assertEquals(624, map.getBaseFloor());
        assertNotNull(map.getMario());
        assertNotNull(map.getEndPoint());
    }

    /** The ground is baked into background.png, so every pit column has to be painted black. */
    @Test
    void pitsArePaintedAsBlackVoids() {
        var map = load("Map 1.png");
        int floor = map.getBaseFloor();
        assertEquals(Map.NO_FLOOR, map.floorAt(61 * 48 + 24.0));

        var frame = new BufferedImage(1268, 708, BufferedImage.TYPE_INT_RGB);
        var g2 = frame.createGraphics();
        g2.translate(-(61 * 48 - 500), 0);
        map.drawMap(g2);
        g2.dispose();

        int insidePit = frame.getRGB(524, floor + 20) & 0xFFFFFF;
        int nextToPit = frame.getRGB(476, floor + 20) & 0xFFFFFF;
        assertEquals(0, insidePit, "pit column is a black void");
        assertNotEquals(0, nextToPit, "the ground next to the pit stays");
    }

    @Test
    void missingMapIsRejected() {
        assertNull(MapLoader.getSingleton().createMap("maps/does-not-exist.png", 400));
    }
}
