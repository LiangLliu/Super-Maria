package com.lianglliu.engine;

import com.lianglliu.config.GameConfig;
import com.lianglliu.model.GameObject;
import com.lianglliu.model.Map;
import com.lianglliu.model.brick.Brick;
import com.lianglliu.model.enemy.Enemy;
import com.lianglliu.model.hero.Fireball;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.BoostItem;
import com.lianglliu.model.prize.Coin;
import com.lianglliu.model.prize.Prize;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 碰撞检测系统 - 专门处理游戏中的各种碰撞逻辑
 */
public class CollisionSystem {
    
    private final Map gameMap;
    private final GameEngine gameEngine;
    
    public CollisionSystem(Map gameMap, GameEngine gameEngine) {
        this.gameMap = gameMap;
        this.gameEngine = gameEngine;
    }
    
    public void checkAllCollisions() {
        if (gameMap == null) return;
        
        checkMarioBottomCollisions();
        checkMarioTopCollisions();
        checkMarioHorizontalCollisions();
        checkEnemyCollisions();
        checkPrizeCollisions();
        checkPrizeContacts();
        checkFireballContacts();
    }
    
    private void checkMarioBottomCollisions() {
        Mario mario = gameMap.getMario();
        List<GameObject> removalList = new ArrayList<>();
        Rectangle marioBottom = mario.getBottomBounds();
        
        if (!mario.isJumping()) {
            mario.setFalling(true);
        }
        
        // 与砖块的底部碰撞
        for (Brick brick : gameMap.getAllBricks()) {
            if (marioBottom.intersects(brick.getTopBounds())) {
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                mario.setFalling(false);
                mario.setVelY(0);
            }
        }
        
        // 踩敌人
        for (Enemy enemy : gameMap.getEnemies()) {
            if (marioBottom.intersects(enemy.getTopBounds())) {
                mario.acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
                removalList.add(enemy);
                gameEngine.playStomp();
            }
        }
        
        // 地面边界检查
        if (mario.getY() + mario.getDimension().height >= gameMap.getBottomBorder()) {
            mario.setY(gameMap.getBottomBorder() - mario.getDimension().height);
            mario.setFalling(false);
            mario.setVelY(0);
        }
        
        removeGameObjects(removalList);
    }
    
    private void checkMarioTopCollisions() {
        Mario mario = gameMap.getMario();
        Rectangle marioTop = mario.getTopBounds();
        
        for (Brick brick : gameMap.getAllBricks()) {
            if (marioTop.intersects(brick.getBottomBounds())) {
                mario.setVelY(0);
                mario.setY(brick.getY() + brick.getDimension().height);
                
                Prize prize = brick.reveal(gameEngine);
                if (prize != null) {
                    gameMap.addRevealedPrize(prize);
                }
            }
        }
    }
    
    private void checkMarioHorizontalCollisions() {
        Mario mario = gameMap.getMario();
        List<GameObject> removalList = new ArrayList<>();
        boolean marioDies = false;
        
        Rectangle marioHorBounds = mario.getToRight() ? 
            mario.getRightBounds() : mario.getLeftBounds();
        
        // 与砖块的水平碰撞
        for (Brick brick : gameMap.getAllBricks()) {
            Rectangle brickBounds = mario.getToRight() ? 
                brick.getLeftBounds() : brick.getRightBounds();
                
            if (marioHorBounds.intersects(brickBounds)) {
                mario.setVelX(0);
                if (mario.getToRight()) {
                    mario.setX(brick.getX() - mario.getDimension().width);
                } else {
                    mario.setX(brick.getX() + brick.getDimension().width);
                }
            }
        }
        
        // 与敌人的水平碰撞
        for (Enemy enemy : gameMap.getEnemies()) {
            Rectangle enemyBounds = mario.getToRight() ? 
                enemy.getLeftBounds() : enemy.getRightBounds();
                
            if (marioHorBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(gameEngine);
                removalList.add(enemy);
            }
        }
        
        removeGameObjects(removalList);
        
        // 摄像机边界限制
        if (mario.getX() <= gameEngine.getCameraLocation().getX() && mario.getVelX() < 0) {
            mario.setVelX(0);
            mario.setX(gameEngine.getCameraLocation().getX());
        }
        
        if (marioDies) {
            gameEngine.getMapManager().resetCurrentMap(gameEngine);
        }
    }
    
    private void checkEnemyCollisions() {
        var bricks = gameMap.getAllBricks();
        var enemies = gameMap.getEnemies();
        
        for (Enemy enemy : enemies) {
            boolean standsOnBrick = false;
            
            // 敌人与砖块的碰撞
            for (Brick brick : bricks) {
                // 水平碰撞
                Rectangle enemyHor = enemy.getVelX() > 0 ? 
                    enemy.getRightBounds() : enemy.getLeftBounds();
                Rectangle brickHor = enemy.getVelX() > 0 ? 
                    brick.getLeftBounds() : brick.getRightBounds();
                    
                if (enemyHor.intersects(brickHor)) {
                    enemy.setVelX(-enemy.getVelX());
                }
                
                // 垂直碰撞
                if (enemy.getBottomBounds().intersects(brick.getTopBounds())) {
                    enemy.setFalling(false);
                    enemy.setVelY(0);
                    enemy.setY(brick.getY() - enemy.getDimension().height);
                    standsOnBrick = true;
                }
            }
            
            // 地面碰撞
            if (enemy.getY() + enemy.getDimension().height > gameMap.getBottomBorder()) {
                enemy.setFalling(false);
                enemy.setVelY(0);
                enemy.setY(gameMap.getBottomBorder() - enemy.getDimension().height);
            }
            
            if (!standsOnBrick && enemy.getY() < gameMap.getBottomBorder()) {
                enemy.setFalling(true);
            }
        }
    }
    
    private void checkPrizeCollisions() {
        var prizes = gameMap.getRevealedPrizes();
        var bricks = gameMap.getAllBricks();
        
        for (var prize : prizes) {
            if (prize instanceof BoostItem boostItem) {
                boostItem.setFalling(true);
                handleBoostItemBrickCollision(boostItem, bricks);
                
                // 地面碰撞
                if (boostItem.getY() + boostItem.getDimension().height > gameMap.getBottomBorder()) {
                    boostItem.setFalling(false);
                    boostItem.setVelY(0);
                    boostItem.setY(gameMap.getBottomBorder() - boostItem.getDimension().height);
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
    
    private void checkPrizeContacts() {
        var prizes = gameMap.getRevealedPrizes();
        var removalList = new ArrayList<GameObject>();
        Rectangle marioBounds = gameMap.getMario().getBounds();
        
        for (var prize : prizes) {
            Rectangle prizeBounds = prize.getBounds();
            if (prizeBounds.intersects(marioBounds)) {
                prize.onTouch(gameMap.getMario(), gameEngine);
                removalList.add((GameObject) prize);
            } else if (prize instanceof Coin coin) {
                coin.onTouch(gameMap.getMario(), gameEngine);
            }
        }
        removeGameObjects(removalList);
    }
    
    private void checkFireballContacts() {
        var fireballs = gameMap.getFireballs();
        var enemies = gameMap.getEnemies();
        var bricks = gameMap.getAllBricks();
        var removalList = new ArrayList<GameObject>();
        
        for (var fireball : fireballs) {
            Rectangle fireballBounds = fireball.getBounds();
            
            // 火球与敌人碰撞
            for (var enemy : enemies) {
                if (fireballBounds.intersects(enemy.getBounds())) {
                    gameMap.getMario().acquirePoints(GameConfig.Scoring.ENEMY_STOMP_POINTS);
                    removalList.add(enemy);
                    removalList.add(fireball);
                }
            }
            
            // 火球与砖块碰撞
            for (var brick : bricks) {
                if (fireballBounds.intersects(brick.getBounds())) {
                    removalList.add(fireball);
                }
            }
        }
        removeGameObjects(removalList);
    }
    
    private void removeGameObjects(List<GameObject> removalList) {
        if (removalList.isEmpty()) return;
        
        for (var object : removalList) {
            if (object instanceof Fireball fireball) {
                gameMap.removeFireball(fireball);
            } else if (object instanceof Enemy enemy) {
                gameMap.removeEnemy(enemy);
            } else if (object instanceof Coin || object instanceof BoostItem) {
                gameMap.removePrize((Prize) object);
            }
        }
    }
} 