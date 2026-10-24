package com.lianglliu.manager;

import com.lianglliu.config.GameConfig;
import com.lianglliu.core.Camera;
import com.lianglliu.model.GameContext;
import com.lianglliu.model.GameObject;
import com.lianglliu.model.Map;
import com.lianglliu.model.brick.Brick;
import com.lianglliu.model.enemy.Enemy;
import com.lianglliu.model.hero.Fireball;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.BoostItem;
import com.lianglliu.model.prize.Prize;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Per-frame world interactions: terrain, enemies, prizes, fireballs and the end flag.
 * Stateless - the caller owns the map and the lock.
 */
final class CollisionResolver {

    private final Camera camera;

    CollisionResolver(Camera camera) {
        this.camera = camera;
    }

    /**
     * @return true when Mario lost a life during this pass
     */
    boolean resolve(Map map, GameContext context) {
        Mario mario = map.getMario();

        checkBottom(map, mario, context);
        checkTop(map, mario, context);
        boolean marioDied = checkHorizontal(map, mario, context);
        checkEnemies(map);
        checkBoostItems(map);
        checkPrizeContact(map, mario, context);
        checkFireballs(map, mario);
        if (!marioDied) {
            checkEndFlag(map, mario);
        }
        return marioDied;
    }

    private void checkBottom(Map map, Mario mario, GameContext context) {
        var removalList = new ArrayList<GameObject>();
        Rectangle marioBottom = mario.getBottomBounds();

        if (!mario.isJumping()) {
            mario.setFalling(true);
        }

        for (Brick brick : map.getBricks()) {
            if (marioBottom.intersects(brick.getTopBounds())) {
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                mario.setFalling(false);
                mario.setVelY(0);
            }
        }

        for (Enemy enemy : map.getEnemies()) {
            if (marioBottom.intersects(enemy.getTopBounds())) {
                mario.acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
                removalList.add(enemy);
                context.playStomp();
            }
        }

        if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
            mario.setY(map.getBottomBorder() - mario.getDimension().height);
            mario.setFalling(false);
            mario.setVelY(0);
        }

        removeObjects(map, removalList);
    }

    private void checkTop(Map map, Mario mario, GameContext context) {
        Rectangle marioTop = mario.getTopBounds();
        for (Brick brick : map.getBricks()) {
            if (marioTop.intersects(brick.getBottomBounds())) {
                mario.setVelY(0);
                mario.setY(brick.getY() + brick.getDimension().height);
                var prize = brick.reveal(context);
                if (prize != null) {
                    map.addRevealedPrize(prize);
                }
            }
        }
    }

    private boolean checkHorizontal(Map map, Mario mario, GameContext context) {
        var removalList = new ArrayList<GameObject>();
        boolean marioDies = false;

        Rectangle marioHorBounds = mario.getToRight() ? mario.getRightBounds() : mario.getLeftBounds();

        for (Brick brick : map.getBricks()) {
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

        for (Enemy enemy : map.getEnemies()) {
            Rectangle enemyBounds = mario.getToRight() ? enemy.getLeftBounds() : enemy.getRightBounds();
            if (marioHorBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(context);
                removalList.add(enemy);
            }
        }
        removeObjects(map, removalList);

        if (mario.getX() <= camera.getX() && mario.getVelX() < 0) {
            mario.setVelX(0);
            mario.setX(camera.getX());
        }

        return marioDies;
    }

    private void checkEnemies(Map map) {
        for (Enemy enemy : map.getEnemies()) {
            boolean standsOnBrick = false;

            for (Brick brick : map.getBricks()) {
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

    private void checkBoostItems(Map map) {
        List<Brick> bricks = map.getBricks();

        for (var prize : map.getRevealedPrizes()) {
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

    private void handleBoostItemBrickCollision(BoostItem boostItem, List<Brick> bricks) {
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

    private void checkPrizeContact(Map map, Mario mario, GameContext context) {
        var removalList = new ArrayList<GameObject>();
        Rectangle marioBounds = mario.getBounds();

        for (var prize : map.getRevealedPrizes()) {
            if (prize.getBounds().intersects(marioBounds)) {
                prize.onTouch(mario, context);
                removalList.add((GameObject) prize);
            }
        }
        removeObjects(map, removalList);
    }

    private void checkFireballs(Map map, Mario mario) {
        var removalList = new ArrayList<GameObject>();

        for (var fireball : map.getFireballs()) {
            Rectangle fireballBounds = fireball.getBounds();

            for (var enemy : map.getEnemies()) {
                if (fireballBounds.intersects(enemy.getBounds())) {
                    mario.acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
                    removalList.add(enemy);
                    removalList.add(fireball);
                }
            }

            for (var brick : map.getBricks()) {
                if (fireballBounds.intersects(brick.getBounds())) {
                    removalList.add(fireball);
                }
            }
        }
        removeObjects(map, removalList);
    }

    /** Touching the flag awards its bonus once; walking past it is handled by the map manager. */
    private void checkEndFlag(Map map, Mario mario) {
        var endPoint = map.getEndPoint();

        if (mario.getX() >= endPoint.getX() && !endPoint.isTouched()) {
            endPoint.setTouched(true);
            int height = (int) (map.getBottomBorder() - mario.getY());
            mario.acquirePoints(Math.max(0, height) * GameConfig.Scoring.FLAG_HEIGHT_MULTIPLIER);
        }
    }

    private void removeObjects(Map map, List<GameObject> removalList) {
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
