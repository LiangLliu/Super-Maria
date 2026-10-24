package com.lianglliu.model;

import com.lianglliu.config.GameConfig;
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

    /** Returned by {@link #floorAt(double)} when a column has no ground at all. */
    public static final int NO_FLOOR = -1;

    private final BufferedImage backgroundImage;
    private final int worldWidth;
    private final int worldHeight;
    private final int[] floorByColumn;
    private final int baseFloor;

    private double remainingTime;
    private Mario mario;
    private EndFlag endPoint;
    private String path;

    /**
     * @param floorByColumn floor of each 48 px world column, or {@link #NO_FLOOR}
     * @param baseFloor     the level's main floor, used where a column has no ground of its own
     */
    public Map(double remainingTime, BufferedImage backgroundImage, int worldWidth, int worldHeight,
               int[] floorByColumn, int baseFloor) {
        this.remainingTime = remainingTime;
        this.backgroundImage = backgroundImage;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.floorByColumn = floorByColumn;
        this.baseFloor = baseFloor;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getBaseFloor() {
        return baseFloor;
    }

    /** Floor under the given world x, or {@link #NO_FLOOR} for a pit. */
    public int floorAt(double x) {
        int column = (int) (x / GameConfig.PIXEL_MULTIPLIER);
        if (column < 0) {
            column = 0;
        } else if (column >= floorByColumn.length) {
            column = floorByColumn.length - 1;
        }
        return floorByColumn[column];
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

    public void addRevealedPrize(Prize prize) {
        revealedPrizes.add(prize);
    }

    public void addFireball(Fireball fireball) {
        fireballs.add(fireball);
    }

    public void setEndPoint(EndFlag endPoint) {
        this.endPoint = endPoint;
        int floor = floorAt(endPoint.getX());
        endPoint.setFloorY(floor == NO_FLOOR ? baseFloor : floor);
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
