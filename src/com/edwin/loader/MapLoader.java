package com.edwin.loader;

import com.edwin.manager.Colors;
import com.edwin.manager.Images;
import com.edwin.model.EndFlag;
import com.edwin.model.Map;
import com.edwin.model.brick.GroundBrick;
import com.edwin.model.brick.OrdinaryBrick;
import com.edwin.model.brick.Pipe;
import com.edwin.model.brick.SurpriseBrick;
import com.edwin.model.enemy.KoopaTroopa;
import com.edwin.model.enemy.Roomba;
import com.edwin.model.hero.Mario;
import com.edwin.model.prize.Coin;
import com.edwin.model.prize.FireFlower;
import com.edwin.model.prize.OneUpMushroom;
import com.edwin.model.prize.Prize;
import com.edwin.model.prize.SuperMushroom;

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
