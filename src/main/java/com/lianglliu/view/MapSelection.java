package com.lianglliu.view;

import com.lianglliu.core.IChangeSelector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class MapSelection implements IChangeSelector {

    private final ArrayList<String> maps = new ArrayList<>();
    private final MapSelectionItem[] mapSelectionItems;

    public MapSelection() {
        getMaps();
        this.mapSelectionItems = createItems(this.maps);
    }

    private void getMaps() {
        //TODO: read from file
        maps.add("Map 1.png");
        maps.add("Map 2.png");
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1280, 720);

        if (mapSelectionItems == null) {
            System.out.println(1);
            return;
        }

        var title = "Select a Map";
        int x_location = (1280 - g.getFontMetrics().stringWidth(title)) / 2;
        g.setColor(Color.YELLOW);
        g.drawString(title, x_location, 150);

        for (MapSelectionItem item : mapSelectionItems) {
            g.setColor(Color.WHITE);
            int width = g.getFontMetrics().stringWidth(item.getName().split("[.]")[0]);
            int height = g.getFontMetrics().getHeight();
            item.setDimension(new Dimension(width, height));
            item.setLocation(new Point((1280 - width) / 2, item.getLocation().y));
            g.drawString(item.getName().split("[.]")[0], item.getLocation().x, item.getLocation().y);
        }
    }

    private MapSelectionItem[] createItems(ArrayList<String> maps) {
        if (maps == null || maps.size() < 1) {
            return null;
        }

        int defaultGridSize = 100;
        var items = new MapSelectionItem[maps.size()];
        for (int i = 0; i < items.length; i++) {
            var location = new Point(0, (i + 1) * defaultGridSize + 200);
            items[i] = new MapSelectionItem(maps.get(i), location);
        }

        return items;
    }

    public String selectMap(Point mouseLocation) {
        for (var item : mapSelectionItems) {
            var dimension = item.getDimension();
            var location = item.getLocation();
            boolean inX = location.x <= mouseLocation.x && location.x + dimension.width >= mouseLocation.x;
            boolean inY = location.y >= mouseLocation.y && location.y - dimension.height <= mouseLocation.y;
            if (inX && inY) {
                return item.getName();
            }
        }
        return null;
    }

    public String selectMap(int index) {
        if (index < mapSelectionItems.length && index > -1)
            return mapSelectionItems[index].getName();
        return null;
    }

    @Override
    public int getLength() {
        return mapSelectionItems.length;
    }
}
