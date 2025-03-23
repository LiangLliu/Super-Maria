package com.lianglliu.loader;

import com.lianglliu.manager.Colors;
import com.lianglliu.manager.Images;
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

public class MapLoader {

    private static final int PIXEL_MULTIPLIER = 48;

    public Map createMap(String mapPath, double timeLimit) {
        var mapImage = ImageLoader.getSingleton().loadImage(mapPath);

        if (mapImage == null) {
            System.out.println("Given path is invalid...");
            return null;
        }

        var createdMap = new Map(timeLimit, Images.backgroundImage);
        String[] paths = mapPath.split("/");
        createdMap.setPath(paths[paths.length - 1]);

        for (int x = 0; x < mapImage.getWidth(); x++) {
            for (int y = 0; y < mapImage.getHeight(); y++) {
                int currentPixel = mapImage.getRGB(x, y);
                int xLocation = x * PIXEL_MULTIPLIER;
                int yLocation = y * PIXEL_MULTIPLIER;

                processPixel(currentPixel, xLocation, yLocation, createdMap);
            }
        }

        System.out.println("Map is created..");
        return createdMap;
    }

    private void processPixel(int pixel, int x, int y, Map map) {
        if (pixel == Colors.ordinaryBrick) {
            map.addBrick(new OrdinaryBrick(x, y, Images.ordinaryBrick));
        } else if (pixel == Colors.surpriseBrick) {
            Prize prize = generateRandomPrize(x, y);
            map.addBrick(new SurpriseBrick(x, y, Images.surpriseBrick, prize));
        } else if (pixel == Colors.pipe) {
            map.addGroundBrick(new Pipe(x, y, Images.pipe));
        } else if (pixel == Colors.groundBrick) {
            map.addGroundBrick(new GroundBrick(x, y, Images.groundBrick));
        } else if (pixel == Colors.goomba) {
            var enemy = new Roomba(x, y, Images.goombaLeft);
            enemy.setRightImage(Images.goombaRight);
            map.addEnemy(enemy);
        } else if (pixel == Colors.koopa) {
            var enemy = new KoopaTroopa(x, y, Images.koopaLeft);
            enemy.setRightImage(Images.koopaRight);
            map.addEnemy(enemy);
        } else if (pixel == Colors.mario) {
            map.setMario(new Mario(x, y));
        } else if (pixel == Colors.end) {
            map.setEndPoint(new EndFlag(x + 24, y, Images.endFlag));
        }
    }

    private Prize generateRandomPrize(double x, double y) {
        int random = (int) (Math.random() * 12);
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
