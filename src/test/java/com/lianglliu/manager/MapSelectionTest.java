package com.lianglliu.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapSelectionTest {

    @Test
    void cyclingWrapsAroundTheList() {
        var selection = new MapSelection();

        assertEquals(1, selection.cycle(false));
        assertEquals(0, selection.cycle(false));
        assertEquals(1, selection.cycle(true));
    }

    @Test
    void selectingAnOutOfRangeIndexKeepsTheCurrentOne() {
        var selection = new MapSelection();

        assertEquals(0, selection.select(5));
        assertEquals(1, selection.select(1));
        assertEquals("Map 2.png", selection.selectedName());
    }

    @Test
    void mouseRowsAreHitTested() {
        assertEquals(0, MapSelection.indexAt(300));
        assertEquals(1, MapSelection.indexAt(360));
        assertEquals(1, MapSelection.indexAt(410));
        assertEquals(-1, MapSelection.indexAt(100));
        assertEquals(-1, MapSelection.indexAt(700));
    }
}
