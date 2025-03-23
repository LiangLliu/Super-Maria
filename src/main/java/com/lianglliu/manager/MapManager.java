package com.lianglliu.manager;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.loader.MapLoader;
import com.lianglliu.model.GameObject;
import com.lianglliu.model.Map;
import com.lianglliu.model.brick.Brick;
import com.lianglliu.model.brick.OrdinaryBrick;
import com.lianglliu.model.enemy.Enemy;
import com.lianglliu.model.hero.Fireball;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.BoostItem;
import com.lianglliu.model.prize.Coin;
import com.lianglliu.model.prize.Prize;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Optional;

public class MapManager {

    private Map map;

    public MapManager() {
    }

    public void updateLocations() {
        if (map == null) {
            return;
        }
        map.updateLocations();
    }

    public void resetCurrentMap(GameEngine engine) {
        Mario mario = getMario();
        mario.resetLocation();
        engine.resetCamera();
        createMap(map.getPath());
        map.setMario(mario);
    }

    public boolean createMap(String path) {
        map = MapLoader.getSingleton().createMap("maps/" + path, 400);
        return map != null;
    }

    public void acquirePoints(int point) {
        getMario().acquirePoints(point);
    }

    public Mario getMario() {
        return map.getMario();
    }

    public void fire(GameEngine engine) {
        var fireball = getMario().fire();
        Optional.ofNullable(fireball).ifPresent(it -> {
            map.addFireball(it);
            engine.playFireball();
        });
    }

    public boolean isGameOver() {
        return getMario().getRemainingLives() == 0 || map.isTimeOver();
    }

    public int getScore() {
        return getMario().getPoints();
    }

    public int getRemainingLives() {
        return getMario().getRemainingLives();
    }

    public int getCoins() {
        return getMario().getCoins();
    }

    public void drawMap(Graphics2D g2) {
        map.drawMap(g2);
    }

    public int passMission() {
        if (getMario().getX() >= map.getEndPoint().getX() && !map.getEndPoint().isTouched()) {
            map.getEndPoint().setTouched(true);
            int height = (int) getMario().getY();
            return height * 2;
        }
        return -1;
    }


    public boolean endLevel() {
        return getMario().getX() >= map.getEndPoint().getX() + 320;
    }

    public void checkCollisions(GameEngine engine) {
        if (map == null) {
            return;
        }
        checkBottomCollisions(engine);
        checkTopCollisions(engine);
        checkMarioHorizontalCollision(engine);
        checkEnemyCollisions();
        checkPrizeCollision();
        checkPrizeContact(engine);
        checkFireballContact();
    }

    private void checkBottomCollisions(GameEngine engine) {
        Mario mario = getMario();
        var bricks = map.getAllBricks();
        var enemies = map.getEnemies();
        var removalList = new ArrayList<GameObject>();
        Rectangle marioBottom = mario.getBottomBounds();

        if (!mario.isJumping()) {
            mario.setFalling(true);
        }

        for (Brick brick : bricks) {
            if (marioBottom.intersects(brick.getTopBounds())) {
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                mario.setFalling(false);
                mario.setVelY(0);
            }
        }

        for (Enemy enemy : enemies) {
            if (marioBottom.intersects(enemy.getTopBounds())) {
                mario.acquirePoints(100);
                removalList.add(enemy);
                engine.playStomp();
            }
        }

        if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
            mario.setY(map.getBottomBorder() - mario.getDimension().height);
            mario.setFalling(false);
            mario.setVelY(0);
        }

        removeObjects(removalList);
    }

    private void checkTopCollisions(GameEngine engine) {
        Mario mario = getMario();
        Rectangle marioTop = mario.getTopBounds();
        for (Brick brick : map.getAllBricks()) {
            if (marioTop.intersects(brick.getBottomBounds())) {
                mario.setVelY(0);
                mario.setY(brick.getY() + brick.getDimension().height);
                var prize = brick.reveal(engine);
                if (prize != null) {
                    map.addRevealedPrize(prize);
                }
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine) {
        Mario mario = getMario();
        var bricks = map.getAllBricks();
        var enemies = map.getEnemies();
        var removalList = new ArrayList<GameObject>();
        boolean marioDies = false;

        Rectangle marioHorBounds = mario.getToRight() ? mario.getRightBounds() : mario.getLeftBounds();

        for (Brick brick : bricks) {
            Rectangle brickBounds = mario.getToRight() ? brick.getLeftBounds() : brick.getRightBounds();
            if (marioHorBounds.intersects(brickBounds)) {
                mario.setVelX(0);
                if (mario.getToRight()) {
                    mario.setX(brick.getX() - mario.getDimension().width);
                } else {
                    mario.setX(brick.getX() + brick.getDimension().width);
                }
            }
        }

        for (Enemy enemy : enemies) {
            Rectangle enemyBounds = mario.getToRight() ? enemy.getLeftBounds() : enemy.getRightBounds();
            if (marioHorBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(engine);
                removalList.add(enemy);
            }
        }
        removeObjects(removalList);

        if (mario.getX() <= engine.getCameraLocation().getX() && mario.getVelX() < 0) {
            mario.setVelX(0);
            mario.setX(engine.getCameraLocation().getX());
        }

        if (marioDies) {
            resetCurrentMap(engine);
        }
    }

    private void checkEnemyCollisions() {
        var bricks = map.getAllBricks();
        var enemies = map.getEnemies();

        for (Enemy enemy : enemies) {
            boolean standsOnBrick = false;

            for (Brick brick : bricks) {
                Rectangle enemyHor = enemy.getVelX() > 0 ? enemy.getRightBounds() : enemy.getLeftBounds();
                Rectangle brickHor = enemy.getVelX() > 0 ? brick.getLeftBounds() : brick.getRightBounds();
                if (enemyHor.intersects(brickHor)) {
                    enemy.setVelX(-enemy.getVelX());
                }


                if (enemy.getBottomBounds().intersects(brick.getTopBounds())) {
                    enemy.setFalling(false);
                    enemy.setVelY(0);
                    enemy.setY(brick.getY() - enemy.getDimension().height);
                    standsOnBrick = true;
                }
            }

            if (enemy.getY() + enemy.getDimension().height > map.getBottomBorder()) {
                enemy.setFalling(false);
                enemy.setVelY(0);
                enemy.setY(map.getBottomBorder() - enemy.getDimension().height);
            }
            if (!standsOnBrick && enemy.getY() < map.getBottomBorder()) {
                enemy.setFalling(true);
            }
        }
    }

    private void checkPrizeCollision() {
        var prizes = map.getRevealedPrizes();
        var bricks = map.getAllBricks();

        for (var prize : prizes) {
            if (prize instanceof BoostItem boostItem) {

                boostItem.setFalling(true);
                handleBoostItemBrickCollision(boostItem, bricks);

                if (boostItem.getY() + boostItem.getDimension().height > map.getBottomBorder()) {
                    boostItem.setFalling(false);
                    boostItem.setVelY(0);
                    boostItem.setY(map.getBottomBorder() - boostItem.getDimension().height);
                    if (boostItem.getVelX() == 0) {
                        boostItem.setVelX(2);
                    }
                }
            }
        }
    }

    private void handleBoostItemBrickCollision(BoostItem boostItem, ArrayList<Brick> bricks) {
        Rectangle prizeBottom = boostItem.getBottomBounds();
        Rectangle prizeRight = boostItem.getRightBounds();
        Rectangle prizeLeft = boostItem.getLeftBounds();

        for (Brick brick : bricks) {

            if (boostItem.isFalling() && prizeBottom.intersects(brick.getTopBounds())) {
                boostItem.setFalling(false);
                boostItem.setVelY(0);
                boostItem.setY(brick.getY() - boostItem.getDimension().height + 1);
                if (boostItem.getVelX() == 0) {
                    boostItem.setVelX(2);
                }
            }

            if (boostItem.getVelX() > 0 && prizeRight.intersects(brick.getLeftBounds())) {
                boostItem.setVelX(-boostItem.getVelX());
            } else if (boostItem.getVelX() < 0 && prizeLeft.intersects(brick.getRightBounds())) {
                boostItem.setVelX(-boostItem.getVelX());
            }
        }
    }

    private void checkPrizeContact(GameEngine engine) {
        var prizes = map.getRevealedPrizes();
        var removalList = new ArrayList<GameObject>();
        Rectangle marioBounds = getMario().getBounds();

        for (var prize : prizes) {
            Rectangle prizeBounds = prize.getBounds();
            if (prizeBounds.intersects(marioBounds)) {
                prize.onTouch(getMario(), engine);
                removalList.add((GameObject) prize);
            } else if (prize instanceof Coin coin) {
                coin.onTouch(getMario(), engine);
            }
        }
        removeObjects(removalList);
    }

    private void checkFireballContact() {
        var fireballs = map.getFireballs();
        var enemies = map.getEnemies();
        var bricks = map.getAllBricks();
        var removalList = new ArrayList<GameObject>();

        for (var fireball : fireballs) {
            Rectangle fireballBounds = fireball.getBounds();

            for (var enemy : enemies) {
                if (fireballBounds.intersects(enemy.getBounds())) {
                    acquirePoints(100);
                    removalList.add(enemy);
                    removalList.add(fireball);
                }
            }

            for (var brick : bricks) {
                if (fireballBounds.intersects(brick.getBounds())) {
                    removalList.add(fireball);
                }
            }
        }
        removeObjects(removalList);
    }

    private void removeObjects(ArrayList<GameObject> removalList) {
        if (removalList == null || removalList.isEmpty()) {
            return;
        }
        for (var object : removalList) {
            if (object instanceof Fireball fireball) {
                map.removeFireball(fireball);
            } else if (object instanceof Enemy enemy) {
                map.removeEnemy(enemy);
            } else if (object instanceof Coin || object instanceof BoostItem) {
                map.removePrize((Prize) object);
            }
        }
    }

    public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        map.addRevealedBrick(ordinaryBrick);
    }

    public void updateTime() {
        if (map != null) {
            map.updateTime(1);
        }
    }

    public int getRemainingTime() {
        return (int) map.getRemainingTime();
    }
}
