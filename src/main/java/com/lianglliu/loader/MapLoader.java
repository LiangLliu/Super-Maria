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

    public Map createMap(String mapPath, double timeLimit) {
        var mapImage = ImageLoader.getSingleton().loadImage(mapPath);

        if (mapImage == null) {
            System.out.println("Given path is invalid...");
            return null;
        }

        var createdMap = new Map(timeLimit, Images.backgroundImage);
        String[] paths = mapPath.split("/");
        createdMap.setPath(paths[paths.length - 1]);

        int pixelMultiplier = 48;

        for (int x = 0; x < mapImage.getWidth(); x++) {
            for (int y = 0; y < mapImage.getHeight(); y++) {

                int currentPixel = mapImage.getRGB(x, y);
                int xLocation = x * pixelMultiplier;
                int yLocation = y * pixelMultiplier;

                if (currentPixel == Colors.ordinaryBrick) {
                    var brick = new OrdinaryBrick(xLocation, yLocation, Images.ordinaryBrick);
                    createdMap.addBrick(brick);
                } else if (currentPixel == Colors.surpriseBrick) {
                    var prize = generateRandomPrize(xLocation, yLocation);
                    var brick = new SurpriseBrick(xLocation, yLocation, Images.surpriseBrick, prize);
                    createdMap.addBrick(brick);
                } else if (currentPixel == Colors.pipe) {
                    var brick = new Pipe(xLocation, yLocation, Images.pipe);
                    createdMap.addGroundBrick(brick);
                } else if (currentPixel == Colors.groundBrick) {
                    var brick = new GroundBrick(xLocation, yLocation, Images.groundBrick);
                    createdMap.addGroundBrick(brick);
                } else if (currentPixel == Colors.goomba) {
                    var enemy = new Roomba(xLocation, yLocation, Images.goombaLeft);
                    enemy.setRightImage(Images.goombaRight);
                    createdMap.addEnemy(enemy);
                } else if (currentPixel == Colors.koopa) {
                    var enemy = new KoopaTroopa(xLocation, yLocation, Images.koopaLeft);
                    enemy.setRightImage(Images.koopaRight);
                    createdMap.addEnemy(enemy);
                } else if (currentPixel == Colors.mario) {
                    var marioObject = new Mario(xLocation, yLocation);
                    createdMap.setMario(marioObject);
                } else if (currentPixel == Colors.end) {
                    var endPoint = new EndFlag(xLocation + 24, yLocation, Images.endFlag);
                    createdMap.setEndPoint(endPoint);
                }
            }
        }

        System.out.println("Map is created..");
        return createdMap;
    }

    private Prize generateRandomPrize(double x, double y) {
        int random = (int) (Math.random() * 12);

        return switch (random) {
            case 0 -> new SuperMushroom(x, y, Images.superMushroom);
            case 1 -> new FireFlower(x, y, Images.fireFlower);
            case 2 -> new OneUpMushroom(x, y, Images.oneUpMushroom);
            default -> new Coin(x, y, Images.coin, 50);
        };
    }

    private MapLoader() {
    }

    private static class Inner {
        private static final MapLoader INSTANCE = new MapLoader();
    }

    public static MapLoader getSingleton() {
        return MapLoader.Inner.INSTANCE;
    }
}
