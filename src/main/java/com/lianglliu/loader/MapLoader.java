package com.lianglliu.loader;

import com.lianglliu.config.GameConfig;
import com.lianglliu.model.EndFlag;
import com.lianglliu.model.Map;
import com.lianglliu.model.brick.GroundBrick;
import com.lianglliu.model.brick.OrdinaryBrick;
import com.lianglliu.model.brick.Pipe;
import com.lianglliu.model.brick.SurpriseBrick;
import com.lianglliu.model.enemy.KoopaTroopa;
import com.lianglliu.model.enemy.Roomba;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.Coin;
import com.lianglliu.model.prize.FireFlower;
import com.lianglliu.model.prize.OneUpMushroom;
import com.lianglliu.model.prize.Prize;
import com.lianglliu.model.prize.SuperMushroom;

import java.awt.image.BufferedImage;
import java.util.HashSet;

public class MapLoader {

    private static final int PIXEL_MULTIPLIER = GameConfig.PIXEL_MULTIPLIER;
    private static final int PRIZE_RANDOM_BOUND = 12;

    public Map createMap(String mapPath, double timeLimit) {
        BufferedImage mapImage;
        try {
            mapImage = ImageLoader.getSingleton().loadImage(mapPath);
        } catch (RuntimeException e) {
            System.err.println("Cannot load map " + mapPath + ": " + e.getMessage());
            return null;
        }

        int width = mapImage.getWidth();
        int height = mapImage.getHeight();
        var createdMap = new Map(timeLimit, Images.backgroundImage,
                width * PIXEL_MULTIPLIER, height * PIXEL_MULTIPLIER);
        String[] paths = mapPath.split("/");
        createdMap.setPath(paths[paths.length - 1]);

        int floorY = Integer.MAX_VALUE;
        var unknownColors = new HashSet<Integer>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = mapImage.getRGB(x, y);
                int xLocation = x * PIXEL_MULTIPLIER;
                int yLocation = y * PIXEL_MULTIPLIER;

                if (pixel == Colors.GROUND) {
                    // The floor is the highest ground row; its art is already painted by background.png.
                    floorY = Math.min(floorY, yLocation);
                } else if (pixel != Colors.EMPTY && pixel != Colors.BORDER
                        && !processPixel(pixel, xLocation, yLocation, createdMap)) {
                    unknownColors.add(pixel);
                }
            }
        }

        if (floorY != Integer.MAX_VALUE) {
            createdMap.setBottomBorder(floorY);
        }
        if (!unknownColors.isEmpty()) {
            System.err.println(mapPath + ": unknown map colors "
                    + unknownColors.stream().map(color -> String.format("0x%08X", color)).toList());
        }

        return createdMap;
    }

    private boolean processPixel(int pixel, int x, int y, Map map) {
        if (pixel == Colors.ORDINARY_BRICK) {
            map.addBrick(new OrdinaryBrick(x, y, Images.ordinaryBrick, Images.brickShatterFrames));
        } else if (pixel == Colors.SURPRISE_BRICK) {
            map.addBrick(new SurpriseBrick(x, y, Images.surpriseBrick, Images.surpriseBrickStyle, generateRandomPrize(x, y)));
        } else if (pixel == Colors.PIPE) {
            map.addBrick(new Pipe(x, y, Images.pipe));
        } else if (pixel == Colors.GROUND_BRICK) {
            map.addBrick(new GroundBrick(x, y, Images.groundBrick));
        } else if (pixel == Colors.GOOMBA) {
            var enemy = new Roomba(x, y, Images.goombaLeft);
            enemy.setRightImage(Images.goombaRight);
            map.addEnemy(enemy);
        } else if (pixel == Colors.KOOPA) {
            var enemy = new KoopaTroopa(x, y, Images.koopaLeft);
            enemy.setRightImage(Images.koopaRight);
            map.addEnemy(enemy);
        } else if (pixel == Colors.MARIO) {
            map.setMario(new Mario(x, y, Images.marioSprites));
        } else if (pixel == Colors.END) {
            map.setEndPoint(new EndFlag(x + 24, y, Images.endFlag));
        } else {
            return false;
        }
        return true;
    }

    private Prize generateRandomPrize(double x, double y) {
        int random = (int) (Math.random() * PRIZE_RANDOM_BOUND);
        if (random == 0) {
            return new SuperMushroom(x, y, Images.superMushroom);
        } else if (random == 1) {
            return new FireFlower(x, y, Images.fireFlower);
        } else if (random == 2) {
            return new OneUpMushroom(x, y, Images.oneUpMushroom);
        } else {
            return new Coin(x, y, Images.coin, 50);
        }
    }

    private MapLoader() {
    }

    private static class Inner {
        private static final MapLoader INSTANCE = new MapLoader();
    }

    public static MapLoader getSingleton() {
        return Inner.INSTANCE;
    }
}
