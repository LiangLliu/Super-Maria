package com.lianglliu.manager;

import com.lianglliu.config.GameConfig;
import com.lianglliu.core.Camera;
import com.lianglliu.loader.MapLoader;
import com.lianglliu.model.GameContext;
import com.lianglliu.model.Map;
import com.lianglliu.model.brick.OrdinaryBrick;
import com.lianglliu.model.hero.Mario;

import java.awt.Graphics2D;

/**
 * Owns the current map and is the single entry point for everything that touches it.
 * Every public method is synchronized because the game loop runs on its own thread while
 * Swing repaints and input events read/drive the same state on the EDT.
 */
public class MapManager {

    private static final double LEVEL_TIME_SECONDS = 400;
    private static final int END_ZONE_WIDTH = 320;

    private final Camera camera;
    private final CollisionResolver collisions;
    private Map map;

    public MapManager(Camera camera) {
        this.camera = camera;
        this.collisions = new CollisionResolver(camera);
    }

    public synchronized void updateLocations() {
        if (map == null) {
            return;
        }
        map.updateLocations();
    }

    public synchronized void updateCamera() {
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

    public synchronized void checkCollisions(GameContext context) {
        if (map != null && collisions.resolve(map, context)) {
            resetCurrentMap(context);
        }
    }

    public synchronized void resetCurrentMap(GameContext context) {
        if (map == null) {
            return;
        }
        Mario mario = map.getMario();
        String path = map.getPath();

        mario.resetLocation();
        context.resetCamera();

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

    public synchronized void jump(GameContext context) {
        if (map != null) {
            map.getMario().jump(context);
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

    public synchronized void fire(GameContext context) {
        if (map == null) {
            return;
        }
        var fireball = map.getMario().fire();
        if (fireball != null) {
            map.addFireball(fireball);
            context.playFireball();
        }
    }

    public synchronized boolean isMarioSuper() {
        return map != null && map.getMario().isSuper();
    }

    public synchronized void addRevealedBrick(OrdinaryBrick brick) {
        map.addRevealedBrick(brick);
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

    /** The level is over once Mario has walked past the flag into the end zone. */
    public synchronized boolean endLevel() {
        return map != null && map.getMario().getX() >= map.getEndPoint().getX() + END_ZONE_WIDTH;
    }

    public synchronized void updateTime() {
        if (map != null) {
            map.updateTime(1);
        }
    }
}
