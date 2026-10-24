package com.lianglliu.engine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.lianglliu.async.AsyncExecutor;
import com.lianglliu.config.GameConfig;
import com.lianglliu.core.Camera;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.manager.GameView;
import com.lianglliu.manager.MapSelection;
import com.lianglliu.manager.MapManager;
import com.lianglliu.manager.SoundManager;
import com.lianglliu.model.GameContext;
import com.lianglliu.model.brick.OrdinaryBrick;
import com.lianglliu.manager.StartScreenSelection;
import com.lianglliu.view.UIManager;

public class GameEngine implements Runnable, GameContext, GameView {

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
    private final MapSelection mapSelection = new MapSelection();
    private long lastTimeTick = System.currentTimeMillis();

    public GameEngine() {
        init();
    }

    private void init() {
        var inputManager = new InputManager(this);
        inputHandler = new InputHandler(this);

        var uiManagerCompletableFuture = AsyncExecutor.supplyAsync(() -> new UIManager(this, GameConfig.GAME_WIDTH, GameConfig.GAME_HEIGHT));
        var soundManagerCompletableFuture = AsyncExecutor.supplyAsync(SoundManager::new);
        var mapManagerCompletableFuture = AsyncExecutor.supplyAsync(() -> new MapManager(camera));

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

    @Override
    public void resetCamera() {
        camera.reset();
        soundManager.restartBackground();
    }

    public void selectMapAt(int mouseY) {
        if (gameStatus != GameStatus.MAP_SELECTION) {
            return;
        }
        int index = MapSelection.indexAt(mouseY);
        if (index < 0) {
            return;
        }
        mapSelection.select(index);
        createMap(mapSelection.selectedName());
    }

    public void startSelectedMap() {
        createMap(mapSelection.selectedName());
    }

    public void changeSelectedMap(boolean up) {
        mapSelection.cycle(up);
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
        mapManager.updateCamera();

        if (mapManager.isGameOver()) {
            setGameStatus(GameStatus.GAME_OVER);
            soundManager.pauseBackground();
            soundManager.playGameOver();
            return;
        }

        if (mapManager.endLevel()) {
            setGameStatus(GameStatus.MISSION_PASSED);
        }
    }

    public void receiveInput(Input input) {
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

    @Override
    public void shakeCamera() {
        camera.shakeCamera();
    }

    @Override
    public GameStatus status() {
        return gameStatus;
    }

    @Override
    public int startOptionRow() {
        return startScreenSelection.getLineNumber();
    }

    @Override
    public int selectedMap() {
        return mapSelection.selected();
    }

    @Override
    public java.util.List<String> mapNames() {
        return mapSelection.names();
    }

    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        repaintRequested = true;
    }

    @Override
    public int score() {
        return mapManager.getScore();
    }

    @Override
    public int lives() {
        return mapManager.getRemainingLives();
    }

    @Override
    public int coins() {
        return mapManager.getCoins();
    }

    @Override
    public void drawMap(Graphics2D g2) {
        mapManager.drawMap(g2);
    }

    @Override
    public Point cameraLocation() {
        return new Point((int) camera.getX(), (int) camera.getY());
    }

    @Override
    public void playJump() {
        soundManager.playJump();
    }

    @Override
    public void playCoin() {
        soundManager.playCoin();
    }

    @Override
    public void playStomp() {
        soundManager.playStomp();
    }

    @Override
    public void playFireball() {
        soundManager.playFireball();
    }

    @Override
    public void playOneUp() {
        soundManager.playOneUp();
    }

    @Override
    public void playSuperMushroom() {
        soundManager.playSuperMushroom();
    }

    @Override
    public void playMarioDies() {
        soundManager.playMarioDies();
    }

    @Override
    public boolean isMarioSuper() {
        return mapManager.isMarioSuper();
    }

    @Override
    public void revealBrick(OrdinaryBrick brick) {
        mapManager.addRevealedBrick(brick);
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    @Override
    public int remainingTime() {
        return mapManager.getRemainingTime();
    }
}
