package com.lianglliu.view;

import com.lianglliu.config.GameConfig;
import com.lianglliu.core.IChangeSelector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MapSelection implements IChangeSelector {

    private static final List<String> MAPS = List.of("Map 1.png", "Map 2.png");
    private static final int TITLE_Y = 150;
    private static final int ROW_HIT_HEIGHT = 60;
    private static final int ROW_HIT_BELOW = 20;

    private record Item(String name, String label, int baselineY) {
    }

    private final List<Item> items;

    public MapSelection() {
        var created = new ArrayList<Item>(MAPS.size());
        for (int i = 0; i < MAPS.size(); i++) {
            String name = MAPS.get(i);
            created.add(new Item(name, name.substring(0, name.lastIndexOf('.')),
                    GameConfig.UI.MAP_SELECTION_Y_OFFSET + i * GameConfig.UI.MAP_SELECTION_Y_SPACING));
        }
        items = List.copyOf(created);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT);

        var title = "Select a Map";
        g.setColor(Color.YELLOW);
        g.drawString(title, (GameConfig.GAME_WIDTH - g.getFontMetrics().stringWidth(title)) / 2, TITLE_Y);

        g.setColor(Color.WHITE);
        for (var item : items) {
            int x = (GameConfig.GAME_WIDTH - g.getFontMetrics().stringWidth(item.label())) / 2;
            g.drawString(item.label(), x, item.baselineY());
        }
    }

    public String selectMap(Point mouseLocation) {
        if (mouseLocation == null) {
            return null;
        }
        for (var item : items) {
            if (mouseLocation.y <= item.baselineY() + ROW_HIT_BELOW
                    && mouseLocation.y >= item.baselineY() - ROW_HIT_HEIGHT) {
                return item.name();
            }
        }
        return null;
    }

    public String selectMap(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index).name();
    }

    @Override
    public int getLength() {
        return items.size();
    }
}
