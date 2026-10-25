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

    /** Both levels ship three 2-tile pits, and the renderer cuts them out of the baked ground. */
    @Test
    void shippedLevelPitsRenderAsBlackVoids() {
        assertPits("Map 1.png", 61, 85, 183);
        assertPits("Map 2.png", 69, 86, 152);
    }

    private static void assertPits(String name, int... firstColumns) {
        var map = load(name);
        for (int column : firstColumns) {
            assertEquals(Map.NO_FLOOR, map.floorAt(column * 48 + 24.0), name + " col " + column);
            assertEquals(Map.NO_FLOOR, map.floorAt((column + 1) * 48 + 24.0), name + " col " + (column + 1));
            assertEquals(map.getBaseFloor(), map.floorAt((column - 1) * 48 + 24.0), name + " col " + (column - 1));
            assertEquals(map.getBaseFloor(), map.floorAt((column + 2) * 48 + 24.0), name + " col " + (column + 2));
        }

        int pitColumn = firstColumns[0];
        int floor = map.getBaseFloor();
        var frame = new BufferedImage(1268, 708, BufferedImage.TYPE_INT_RGB);
        var g2 = frame.createGraphics();
        g2.translate(-(pitColumn * 48 - 500), 0);
        map.drawMap(g2);
        g2.dispose();

        assertEquals(0, frame.getRGB(524, floor + 20) & 0xFFFFFF, name + ": the pit is a black void");
        assertNotEquals(0, frame.getRGB(476, floor + 20) & 0xFFFFFF, name + ": the ground next to it stays");
    }

    @Test
    void missingMapIsRejected() {
        assertNull(MapLoader.getSingleton().createMap("maps/does-not-exist.png", 400));
    }
}
