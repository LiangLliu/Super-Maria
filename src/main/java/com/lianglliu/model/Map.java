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
import java.util.Collections;
import java.util.List;

public class Map {

    private final ArrayList<Brick> bricks = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Prize> revealedPrizes = new ArrayList<>();
    private final ArrayList<OrdinaryBrick> revealedBricks = new ArrayList<>();
    private final ArrayList<Fireball> fireballs = new ArrayList<>();

    private final List<Brick> bricksView = Collections.unmodifiableList(bricks);
    private final List<Enemy> enemiesView = Collections.unmodifiableList(enemies);
    private final List<Prize> prizesView = Collections.unmodifiableList(revealedPrizes);
    private final List<Fireball> fireballsView = Collections.unmodifiableList(fireballs);

    private final BufferedImage backgroundImage;
    private final int worldWidth;

    private double remainingTime;
    private Mario mario;
    private EndFlag endPoint;
    private double bottomBorder;
    private String path;

    public Map(double remainingTime, BufferedImage backgroundImage, int worldWidth, int worldHeight) {
        this.remainingTime = remainingTime;
        this.backgroundImage = backgroundImage;
        this.worldWidth = worldWidth;
        this.bottomBorder = worldHeight; // replaced by the floor found in the map image
    }

    public Mario getMario() {
        return mario;
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }

    public List<Enemy> getEnemies() {
        return enemiesView;
    }

    public List<Fireball> getFireballs() {
        return fireballsView;
    }

    public List<Prize> getRevealedPrizes() {
        return prizesView;
    }

    /** Read-only list of every solid tile. */
    public List<Brick> getBricks() {
        return bricksView;
    }

    public void addBrick(Brick brick) {
        bricks.add(brick);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void drawMap(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);

        for (var prize : revealedPrizes) {
            if (prize instanceof Coin coin) {
                coin.draw(g2);
            } else if (prize instanceof BoostItem boostItem) {
                boostItem.draw(g2);
            }
        }

        for (var brick : bricks) {
            brick.draw(g2);
        }

        for (var enemy : enemies) {
            enemy.draw(g2);
        }

        for (var fireball : fireballs) {
            fireball.draw(g2);
        }

        mario.draw(g2);
        endPoint.draw(g2);
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

        for (var fireballIterator = fireballs.iterator(); fireballIterator.hasNext(); ) {
            var fireball = fireballIterator.next();
            fireball.updateLocation();
            if (fireball.getX() + fireball.getDimension().width < 0 || fireball.getX() > worldWidth) {
                fireballIterator.remove();
            }
        }

        for (var brickIterator = revealedBricks.iterator(); brickIterator.hasNext(); ) {
            var brick = brickIterator.next();
            brick.animate();
            if (brick.isFinished()) {
                bricks.remove(brick);
                brickIterator.remove();
            }
        }

        endPoint.updateLocation();
    }

    public double getBottomBorder() {
        return bottomBorder;
    }

    public void setBottomBorder(double bottomBorder) {
        this.bottomBorder = bottomBorder;
        if (endPoint != null) {
            endPoint.setFloorY((int) bottomBorder);
        }
    }

    public void addRevealedPrize(Prize prize) {
        revealedPrizes.add(prize);
    }

    public void addFireball(Fireball fireball) {
        fireballs.add(fireball);
    }

    public void setEndPoint(EndFlag endPoint) {
        this.endPoint = endPoint;
        endPoint.setFloorY((int) bottomBorder);
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
