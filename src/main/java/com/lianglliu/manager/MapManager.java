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
        if (map == null)
            return;

        map.updateLocations();
    }

    public void resetCurrentMap(GameEngine engine) {
        var mario = getMario();
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
        map.getMario().acquirePoints(point);
    }

    public Mario getMario() {
        return map.getMario();
    }

    public void fire(GameEngine engine) {
        var fireball = getMario().fire();

        Optional.ofNullable(fireball)
                .ifPresent(it -> {
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
        } else
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
        ArrayList<Brick> bricks = map.getAllBricks();
        ArrayList<Enemy> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBottomBounds = mario.getBottomBounds();

        if (!mario.isJumping()) {
            mario.setFalling(true);
        }

        for (Brick brick : bricks) {
            Rectangle brickTopBounds = brick.getTopBounds();
            if (marioBottomBounds.intersects(brickTopBounds)) {
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                mario.setFalling(false);
                mario.setVelY(0);
            }
        }

        for (Enemy enemy : enemies) {
            Rectangle enemyTopBounds = enemy.getTopBounds();
            if (marioBottomBounds.intersects(enemyTopBounds)) {
                mario.acquirePoints(100);
                toBeRemoved.add(enemy);
                engine.playStomp();
            }
        }

        if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
            mario.setY(map.getBottomBorder() - mario.getDimension().height);
            mario.setFalling(false);
            mario.setVelY(0);
        }

        removeObjects(toBeRemoved);
    }

    private void checkTopCollisions(GameEngine engine) {
        var mario = getMario();
        var bricks = map.getAllBricks();

        var marioTopBounds = mario.getTopBounds();
        for (var brick : bricks) {
            var brickBottomBounds = brick.getBottomBounds();
            if (marioTopBounds.intersects(brickBottomBounds)) {
                mario.setVelY(0);
                mario.setY(brick.getY() + brick.getDimension().height);
                var prize = brick.reveal(engine);
                if (prize != null)
                    map.addRevealedPrize(prize);
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine) {
        var mario = getMario();
        var bricks = map.getAllBricks();
        var enemies = map.getEnemies();
        var toBeRemoved = new ArrayList<GameObject>();

        boolean marioDies = false;
        boolean toRight = mario.getToRight();

        var marioBounds = toRight ? mario.getRightBounds() : mario.getLeftBounds();

        for (var brick : bricks) {
            var brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
            if (marioBounds.intersects(brickBounds)) {
                mario.setVelX(0);
                if (toRight)
                    mario.setX(brick.getX() - mario.getDimension().width);
                else
                    mario.setX(brick.getX() + brick.getDimension().width);
            }
        }

        for (var enemy : enemies) {
            var enemyBounds = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
            if (marioBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(engine);
                toBeRemoved.add(enemy);
            }
        }
        removeObjects(toBeRemoved);


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

        for (var enemy : enemies) {
            boolean standsOnBrick = false;

            for (var brick : bricks) {
                var enemyBounds = enemy.getLeftBounds();
                var brickBounds = brick.getRightBounds();

                var enemyBottomBounds = enemy.getBottomBounds();
                var brickTopBounds = brick.getTopBounds();

                if (enemy.getVelX() > 0) {
                    enemyBounds = enemy.getRightBounds();
                    brickBounds = brick.getLeftBounds();
                }

                if (enemyBounds.intersects(brickBounds)) {
                    enemy.setVelX(-enemy.getVelX());
                }

                if (enemyBottomBounds.intersects(brickTopBounds)) {
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
                var prizeBottomBounds = boostItem.getBottomBounds();
                var prizeRightBounds = boostItem.getRightBounds();
                var prizeLeftBounds = boostItem.getLeftBounds();
                boostItem.setFalling(true);

                for (var brick : bricks) {
                    Rectangle brickBounds;

                    if (boostItem.isFalling()) {
                        brickBounds = brick.getTopBounds();

                        if (brickBounds.intersects(prizeBottomBounds)) {
                            boostItem.setFalling(false);
                            boostItem.setVelY(0);
                            boostItem.setY(brick.getY() - boostItem.getDimension().height + 1);
                            if (boostItem.getVelX() == 0)
                                boostItem.setVelX(2);
                        }
                    }

                    if (boostItem.getVelX() > 0) {
                        brickBounds = brick.getLeftBounds();

                        if (brickBounds.intersects(prizeRightBounds)) {
                            boostItem.setVelX(-boostItem.getVelX());
                        }
                    } else if (boostItem.getVelX() < 0) {
                        brickBounds = brick.getRightBounds();

                        if (brickBounds.intersects(prizeLeftBounds)) {
                            boostItem.setVelX(-boostItem.getVelX());
                        }
                    }
                }

                if (boostItem.getY() + boostItem.getDimension().height > map.getBottomBorder()) {
                    boostItem.setFalling(false);
                    boostItem.setVelY(0);
                    boostItem.setY(map.getBottomBorder() - boostItem.getDimension().height);
                    if (boostItem.getVelX() == 0)
                        boostItem.setVelX(2);
                }

            }
        }
    }

    private void checkPrizeContact(GameEngine engine) {
        var prizes = map.getRevealedPrizes();
        var toBeRemoved = new ArrayList<GameObject>();

        var marioBounds = getMario().getBounds();
        for (var prize : prizes) {
            var prizeBounds = prize.getBounds();
            if (prizeBounds.intersects(marioBounds)) {
                prize.onTouch(getMario(), engine);
                toBeRemoved.add((GameObject) prize);
            } else if (prize instanceof Coin coin) {
                coin.onTouch(getMario(), engine);
            }
        }

        removeObjects(toBeRemoved);
    }

    private void checkFireballContact() {
        var fireballs = map.getFireballs();
        var enemies = map.getEnemies();
        var bricks = map.getAllBricks();
        var toBeRemoved = new ArrayList<GameObject>();

        for (var fireball : fireballs) {
            var fireballBounds = fireball.getBounds();

            for (var enemy : enemies) {
                var enemyBounds = enemy.getBounds();
                if (fireballBounds.intersects(enemyBounds)) {
                    acquirePoints(100);
                    toBeRemoved.add(enemy);
                    toBeRemoved.add(fireball);
                }
            }

            for (var brick : bricks) {
                var brickBounds = brick.getBounds();
                if (fireballBounds.intersects(brickBounds)) {
                    toBeRemoved.add(fireball);
                }
            }
        }

        removeObjects(toBeRemoved);
    }

    private void removeObjects(ArrayList<GameObject> list) {
        if (list == null) {
            return;
        }

        for (var object : list) {
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
