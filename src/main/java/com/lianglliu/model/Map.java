package com.lianglliu.model;

import com.lianglliu.model.brick.Brick;
import com.lianglliu.model.brick.OrdinaryBrick;
import com.lianglliu.model.enemy.Enemy;
import com.lianglliu.model.hero.Fireball;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.prize.BoostItem;
import com.lianglliu.model.prize.Coin;
import com.lianglliu.model.prize.Prize;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Map {

    private double remainingTime;
    private Mario mario;
    private final ArrayList<Brick> bricks = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Brick> groundBricks = new ArrayList<>();
    private final ArrayList<Prize> revealedPrizes = new ArrayList<>();
    private final ArrayList<Brick> revealedBricks = new ArrayList<>();
    private final ArrayList<Fireball> fireballs = new ArrayList<>();
    private EndFlag endPoint;
    private final BufferedImage backgroundImage;
    private double bottomBorder = 720 - 96;
    private String path;

    public Map(double remainingTime, BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.remainingTime = remainingTime;
    }

    public Mario getMario() {
        return mario;
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Fireball> getFireballs() {
        return fireballs;
    }

    public ArrayList<Prize> getRevealedPrizes() {
        return revealedPrizes;
    }

    public ArrayList<Brick> getAllBricks() {
        var allBricks = new ArrayList<Brick>();

        allBricks.addAll(bricks);
        allBricks.addAll(groundBricks);

        return allBricks;
    }

    public void addBrick(Brick brick) {
        this.bricks.add(brick);
    }

    public void addGroundBrick(Brick brick) {
        this.groundBricks.add(brick);
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public void drawMap(Graphics2D g2) {
        drawBackground(g2);
        drawPrizes(g2);
        drawBricks(g2);
        drawEnemies(g2);
        drawFireballs(g2);
        drawMario(g2);
        endPoint.draw(g2);
    }

    private void drawFireballs(Graphics2D g2) {
        for (var fireball : fireballs) {
            fireball.draw(g2);
        }
    }

    private void drawPrizes(Graphics2D g2) {
        for (var prize : revealedPrizes) {
            if (prize instanceof Coin coin) {
                coin.draw(g2);
            } else if (prize instanceof BoostItem boostItem) {
                boostItem.draw(g2);
            }
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);
    }

    private void drawBricks(Graphics2D g2) {
        for (var brick : bricks) {
            if (brick != null)
                brick.draw(g2);
        }

        for (var brick : groundBricks) {
            brick.draw(g2);
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for (var enemy : enemies) {
            if (enemy != null)
                enemy.draw(g2);
        }
    }

    private void drawMario(Graphics2D g2) {
        mario.draw(g2);
    }

    public void updateLocations() {
        mario.updateLocation();
        for (var enemy : enemies) {
            enemy.updateLocation();
        }

        for (var prizeIterator = revealedPrizes.iterator(); prizeIterator.hasNext(); ) {
            var prize = prizeIterator.next();
            if (prize instanceof Coin coin) {
                coin.updateLocation();
                if (coin.getRevealBoundary() > coin.getY()) {
                    prizeIterator.remove();
                }
            } else if (prize instanceof BoostItem boostItem) {
                boostItem.updateLocation();
            }
        }

        for (var fireball : fireballs) {
            fireball.updateLocation();
        }

        for (var brickIterator = revealedBricks.iterator(); brickIterator.hasNext(); ) {
            var brick = (OrdinaryBrick) brickIterator.next();
            brick.animate();
            if (brick.getFrames() < 0) {
                bricks.remove(brick);
                brickIterator.remove();
            }
        }

        endPoint.updateLocation();
    }

    public double getBottomBorder() {
        return bottomBorder;
    }

    public void addRevealedPrize(Prize prize) {
        revealedPrizes.add(prize);
    }

    public void addFireball(Fireball fireball) {
        fireballs.add(fireball);
    }

    public void setEndPoint(EndFlag endPoint) {
        this.endPoint = endPoint;
    }

    public EndFlag getEndPoint() {
        return endPoint;
    }

    public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        revealedBricks.add(ordinaryBrick);
    }

    public void removeFireball(Fireball object) {
        fireballs.remove(object);
    }

    public void removeEnemy(Enemy object) {
        enemies.remove(object);
    }

    public void removePrize(Prize object) {
        revealedPrizes.remove(object);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void updateTime(double passed) {
        remainingTime = remainingTime - passed;
    }

    public boolean isTimeOver() {
        return remainingTime <= 0;
    }

    public double getRemainingTime() {
        return remainingTime;
    }
}
