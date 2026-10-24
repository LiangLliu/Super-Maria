package com.lianglliu.engine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.lianglliu.async.AsyncExecutor;
import com.lianglliu.config.GameConfig;
import com.lianglliu.core.Camera;
import com.lianglliu.manager.ButtonAction;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.manager.InputManager;
import com.lianglliu.manager.MapManager;
import com.lianglliu.manager.SoundManager;
import com.lianglliu.view.StartScreenSelection;
import com.lianglliu.view.UIManager;

public class GameEngine implements Runnable {

    private final Camera camera = new Camera();
    private MapManager mapManager;
    private UIManager uiManager;
    private SoundManager soundManager;
    private InputHandler inputHandler;
    private volatile GameStatus gameStatus = GameStatus.START_SCREEN;
    private volatile boolean isRunning;
    private volatile boolean repaintRequested = true;
    private Thread thread;
    private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
    private int selectedMap = 0;
    private long lastTimeTick = System.currentTimeMillis();

    public GameEngine() {
        init();
    }

    private void init() {
        var inputManager = new InputManager(this);
        inputHandler = new InputHandler(this);

        var uiManagerCompletableFuture = AsyncExecutor.supplyAsync(() -> new UIManager(this, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT));
        var soundManagerCompletableFuture = AsyncExecutor.supplyAsync(SoundManager::new);
        var mapManagerCompletableFuture = AsyncExecutor.supplyAsync(MapManager::new);

        uiManager = AsyncExecutor.fetch(uiManagerCompletableFuture);
        soundManager = AsyncExecutor.fetch(soundManagerCompletableFuture);
        mapManager = AsyncExecutor.fetch(mapManagerCompletableFuture);

        var frame = new JFrame("Super Mario Bros.");
        frame.add(uiManager);
        frame.addKeyListener(inputManager);
        frame.addMouseListener(inputManager);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        start();
    }

    private synchronized void start() {
        if (isRunning)
            return;

        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void reset() {
        resetCamera();
        setGameStatus(GameStatus.START_SCREEN);
    }

    public void resetCamera() {
        camera.reset();
        soundManager.restartBackground();
    }

    public void selectMapViaMouse() {
        var path = uiManager.selectMapViaMouse(uiManager.getMousePosition());
        if (path != null) {
            createMap(path);
        }
    }

    public void selectMapViaKeyboard() {
        var path = uiManager.selectMapViaKeyboard(selectedMap);
        if (path != null) {
            createMap(path);
        }
    }

    public void changeSelectedMap(boolean up) {
        selectedMap = uiManager.changeSelectedMap(selectedMap, up);
        repaintRequested = true;
    }

    private void createMap(String path) {
        if (mapManager.createMap(path)) {
            setGameStatus(GameStatus.RUNNING);
            soundManager.restartBackground();
        } else {
            setGameStatus(GameStatus.START_SCREEN);
        }
    }

    @Override
    public void run() {
        double nanosPerFrame = GameConfig.NANOSECONDS_PER_SECOND / GameConfig.TARGET_FPS;
        long previous = System.nanoTime();
        long nextFrame = previous;
        double delta = 0;

        try {
            while (isRunning && !thread.isInterrupted()) {
                long now = System.nanoTime();
                delta = Math.min(delta + (now - previous) / nanosPerFrame, GameConfig.MAX_UPDATES_PER_FRAME);
                previous = now;

                while (delta >= 1) {
                    if (gameStatus == GameStatus.RUNNING) {
                        gameLoop();
                    }
                    delta--;
                }

                render();
                updateTimeLimit();

                nextFrame += (long) nanosPerFrame;
                long wait = nextFrame - System.nanoTime();
                if (wait > 0) {
                    LockSupport.parkNanos(wait);
                } else {
                    nextFrame = System.nanoTime();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }

    private void render() {
        if (repaintRequested) {
            repaintRequested = false;
            uiManager.repaint();
        }
    }

    private void updateTimeLimit() {
        long now = System.currentTimeMillis();
        if (gameStatus != GameStatus.RUNNING) {
            lastTimeTick = now;
            return;
        }
        if (now - lastTimeTick >= GameConfig.TIMER_UPDATE_INTERVAL_MS) {
            lastTimeTick = now;
            mapManager.updateTime();
        }
    }

    private void gameLoop() {
        repaintRequested = true;
        mapManager.updateLocations();
        mapManager.checkCollisions(this);
        mapManager.updateCamera(camera);

        if (mapManager.isGameOver()) {
            setGameStatus(GameStatus.GAME_OVER);
            soundManager.pauseBackground();
            soundManager.playGameOver();
            return;
        }

        mapManager.checkMission();
        if (mapManager.endLevel()) {
            setGameStatus(GameStatus.MISSION_PASSED);
        }
    }

    public void receiveInput(ButtonAction input) {
        inputHandler.handleInput(input);
    }

    public void selectOption(boolean selectUp) {
        startScreenSelection = startScreenSelection.select(selectUp);
        repaintRequested = true;
    }

    public void startGame() {
        if (gameStatus != GameStatus.GAME_OVER) {
            setGameStatus(GameStatus.MAP_SELECTION);
        }
    }

    public void pauseGame() {
        if (gameStatus == GameStatus.RUNNING) {
            setGameStatus(GameStatus.PAUSED);
            soundManager.pauseBackground();
        } else if (gameStatus == GameStatus.PAUSED) {
            setGameStatus(GameStatus.RUNNING);
            soundManager.resumeBackground();
        }
    }

    public void shakeCamera() {
        camera.shakeCamera();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        repaintRequested = true;
    }

    public int getScore() {
        return mapManager.getScore();
    }

    public int getRemainingLives() {
        return mapManager.getRemainingLives();
    }

    public int getCoins() {
        return mapManager.getCoins();
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void drawMap(Graphics2D g2) {
        mapManager.drawMap(g2);
    }

    public Point getCameraLocation() {
        return new Point((int) camera.getX(), (int) camera.getY());
    }

    public void playCoin() {
        soundManager.playCoin();
    }

    public void playOneUp() {
        soundManager.playOneUp();
    }

    public void playSuperMushroom() {
        soundManager.playSuperMushroom();
    }

    public void playMarioDies() {
        soundManager.playMarioDies();
    }

    public void playJump() {
        soundManager.playJump();
    }

    public void playFireball() {
        soundManager.playFireball();
    }

    public void playStomp() {
        soundManager.playStomp();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public int getRemainingTime() {
        return mapManager.getRemainingTime();
    }
}
