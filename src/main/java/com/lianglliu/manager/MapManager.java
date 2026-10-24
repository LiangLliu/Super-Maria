package com.lianglliu.manager;

import com.lianglliu.config.GameConfig;
import com.lianglliu.core.Camera;
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

/**
 * Owns the current map and is the single entry point for everything that touches it.
 * Every public method is synchronized because the game loop runs on its own thread while
 * Swing repaints and input events read/drive the same state on the EDT.
 */
public class MapManager {

    private static final double LEVEL_TIME_SECONDS = 400;

    private Map map;

    public MapManager() {
    }

    public synchronized void updateLocations() {
        if (map == null) {
            return;
        }
        map.updateLocations();
    }

    public synchronized void updateCamera(Camera camera) {
        if (map == null) {
            return;
        }
        Mario mario = map.getMario();
        double shiftAmount = 0;

        if (mario.getVelX() > 0 && mario.getX() - GameConfig.CAMERA_FOLLOW_OFFSET > camera.getX()) {
            shiftAmount = mario.getVelX();
        }

        camera.moveCam(shiftAmount, 0);
    }

    public synchronized void resetCurrentMap(GameEngine engine) {
        if (map == null) {
            return;
        }
        Mario mario = map.getMario();
        String path = map.getPath();

        mario.resetLocation();
        engine.resetCamera();

        if (createMap(path)) {
            map.setMario(mario);
        }
    }

    public synchronized boolean createMap(String path) {
        var created = MapLoader.getSingleton().createMap("maps/" + path, LEVEL_TIME_SECONDS);
        if (created == null) {
            return false;
        }
        map = created;
        return true;
    }

    public synchronized void jump(GameEngine engine) {
        if (map != null) {
            map.getMario().jump(engine);
        }
    }

    public synchronized void move(boolean toRight) {
        if (map != null) {
            map.getMario().move(toRight);
        }
    }

    public synchronized void stopMoving() {
        if (map != null) {
            map.getMario().setVelX(0);
        }
    }

    public synchronized void fire(GameEngine engine) {
        if (map == null) {
            return;
        }
        var fireball = map.getMario().fire();
        if (fireball != null) {
            map.addFireball(fireball);
            engine.playFireball();
        }
    }

    public synchronized boolean isMarioSuper() {
        return map != null && map.getMario().isSuper();
    }

    public synchronized boolean isGameOver() {
        return map != null && (map.getMario().getRemainingLives() == 0 || map.isTimeOver());
    }

    public synchronized int getScore() {
        return map == null ? 0 : map.getMario().getPoints();
    }

    public synchronized int getRemainingLives() {
        return map == null ? 0 : map.getMario().getRemainingLives();
    }

    public synchronized int getCoins() {
        return map == null ? 0 : map.getMario().getCoins();
    }

    public synchronized int getRemainingTime() {
        return map == null ? 0 : (int) map.getRemainingTime();
    }

    public synchronized void drawMap(Graphics2D g2) {
        if (map != null) {
            map.drawMap(g2);
        }
    }

    /** Awards the flag bonus once, then {@link #endLevel()} takes over. */
    public synchronized void checkMission() {
        if (map == null) {
            return;
        }
        var mario = map.getMario();
        var endPoint = map.getEndPoint();

        if (mario.getX() >= endPoint.getX() && !endPoint.isTouched()) {
            endPoint.setTouched(true);
            int height = (int) (map.getBottomBorder() - mario.getY());
            mario.acquirePoints(Math.max(0, height) * GameConfig.Scoring.FLAG_HEIGHT_MULTIPLIER);
        }
    }

    public synchronized boolean endLevel() {
        return map != null && map.getMario().getX() >= map.getEndPoint().getX() + 320;
    }

    public synchronized void checkCollisions(GameEngine engine) {
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

    public synchronized void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        map.addRevealedBrick(ordinaryBrick);
    }

    public synchronized void updateTime() {
        if (map != null) {
            map.updateTime(1);
        }
    }

    private Mario mario() {
        return map.getMario();
    }

    private void checkBottomCollisions(GameEngine engine) {
        Mario mario = mario();
        var bricks = map.getBricks();
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
                mario.acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
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
        Mario mario = mario();
        Rectangle marioTop = mario.getTopBounds();
        for (Brick brick : map.getBricks()) {
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
        Mario mario = mario();
        var bricks = map.getBricks();
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
        var bricks = map.getBricks();
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
        var bricks = map.getBricks();

        for (var prize : prizes) {
            if (prize instanceof BoostItem boostItem) {

                boostItem.setFalling(true);
                handleBoostItemBrickCollision(boostItem, bricks);

                if (boostItem.getY() + boostItem.getDimension().height > map.getBottomBorder()) {
                    boostItem.setFalling(false);
                    boostItem.setVelY(0);
                    boostItem.setY(map.getBottomBorder() - boostItem.getDimension().height);
                    if (boostItem.getVelX() == 0) {
                        boostItem.setVelX(GameConfig.BOOST_ITEM_SPEED);
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
                    boostItem.setVelX(GameConfig.BOOST_ITEM_SPEED);
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
        Rectangle marioBounds = mario().getBounds();

        for (var prize : prizes) {
            if (prize.getBounds().intersects(marioBounds)) {
                prize.onTouch(mario(), engine);
                removalList.add((GameObject) prize);
            }
        }
        removeObjects(removalList);
    }

    private void checkFireballContact() {
        var fireballs = map.getFireballs();
        var enemies = map.getEnemies();
        var bricks = map.getBricks();
        var removalList = new ArrayList<GameObject>();

        for (var fireball : fireballs) {
            Rectangle fireballBounds = fireball.getBounds();

            for (var enemy : enemies) {
                if (fireballBounds.intersects(enemy.getBounds())) {
                    mario().acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
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
        for (var object : removalList) {
            if (object instanceof Fireball fireball) {
                map.removeFireball(fireball);
            } else if (object instanceof Enemy enemy) {
                map.removeEnemy(enemy);
            } else if (object instanceof Prize prize) {
                map.removePrize(prize);
            }
        }
    }
}
