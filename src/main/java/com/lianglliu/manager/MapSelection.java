package com.lianglliu.manager;

import com.lianglliu.config.GameConfig;
import com.lianglliu.core.IChangeSelector;

import java.util.List;

/**
 * Which map is selected. Pure state - drawing and hit testing live in the view,
 * except for the row geometry shared through {@link #indexAt(int)}.
 */
public class MapSelection implements IChangeSelector {

    private static final List<String> MAPS = List.of("Map 1.png", "Map 2.png");
    private static final int ROW_HIT_HEIGHT = 60;
    private static final int ROW_HIT_BELOW = 20;

    private int selected;

    public List<String> names() {
        return MAPS;
    }

    public int selected() {
        return selected;
    }

    public String selectedName() {
        return MAPS.get(selected);
    }

    public int cycle(boolean up) {
        selected = changeSelect(selected, up);
        return selected;
    }

    public int select(int index) {
        if (index >= 0 && index < MAPS.size()) {
            selected = index;
        }
        return selected;
    }

    /**
     * @return the row index under the given mouse y, or -1 when no row is hit
     */
    public static int indexAt(int mouseY) {
        for (int i = 0; i < MAPS.size(); i++) {
            int baseline = GameConfig.UI.MAP_SELECTION_Y_OFFSET + i * GameConfig.UI.MAP_SELECTION_Y_SPACING;
            if (mouseY <= baseline + ROW_HIT_BELOW && mouseY >= baseline - ROW_HIT_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getLength() {
        return MAPS.size();
    }
}
