package com.lianglliu.engine;

import com.lianglliu.async.AsyncExecutor;
import com.lianglliu.core.Camera;
import com.lianglliu.manager.ButtonAction;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.manager.InputManager;
import com.lianglliu.manager.MapManager;
import com.lianglliu.manager.SoundManager;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.view.StartScreenSelection;
import com.lianglliu.view.UIManager;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Graphics2D;
import java.awt.Point;

public class GameEngine implements Runnable {

    private final static int WIDTH = 1268, HEIGHT = 708;

    private MapManager mapManager;
    private UIManager uiManager;
    private SoundManager soundManager;
    private GameStatus gameStatus;
    private boolean isRunning;
    private Camera camera;
    private Thread thread;
    private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
    private int selectedMap = 0;

    public GameEngine() {
        init();
    }

    private void init() {
        var inputManager = new InputManager(this);
        gameStatus = GameStatus.START_SCREEN;

        camera = new Camera();

        var uiManagerCompletableFuture = AsyncExecutor.supplyAsync(() -> new UIManager(this, WIDTH, HEIGHT));
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

    private void reset() {
        resetCamera();
        setGameStatus(GameStatus.START_SCREEN);
    }

    public void resetCamera() {
        camera = new Camera();
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
    }

    private void createMap(String path) {
        boolean loaded = mapManager.createMap(path);
        if (loaded) {
            setGameStatus(GameStatus.RUNNING);
            soundManager.restartBackground();
        } else {
            setGameStatus(GameStatus.START_SCREEN);
        }

    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while (isRunning && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                if (gameStatus == GameStatus.RUNNING) {
                    gameLoop();
                }
                delta--;
            }
            render();

            if (gameStatus != GameStatus.RUNNING) {
                timer = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                mapManager.updateTime();
            }
        }
    }

    private void render() {
        uiManager.repaint();
    }

    private void gameLoop() {
        updateLocations();
        checkCollisions();
        updateCamera();

        if (isGameOver()) {
            setGameStatus(GameStatus.GAME_OVER);
        }

        int missionPassed = passMission();
        if (missionPassed > -1) {
            mapManager.acquirePoints(missionPassed);
            //setGameStatus(GameStatus.MISSION_PASSED);
        } else if (mapManager.endLevel())
            setGameStatus(GameStatus.MISSION_PASSED);
    }

    private void updateCamera() {
        var mario = mapManager.getMario();
        double marioVelocityX = mario.getVelX();
        double shiftAmount = 0;

        if (marioVelocityX > 0 && mario.getX() - 600 > camera.getX()) {
            shiftAmount = marioVelocityX;
        }

        camera.moveCam(shiftAmount, 0);
    }

    private void updateLocations() {
        mapManager.updateLocations();
    }

    private void checkCollisions() {
        mapManager.checkCollisions(this);
    }

    public void receiveInput(ButtonAction input) {
        switch (gameStatus) {
            case START_SCREEN -> {
                if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.START_GAME) {
                    startGame();
                } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_ABOUT) {
                    setGameStatus(GameStatus.ABOUT_SCREEN);
                } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_HELP) {
                    setGameStatus(GameStatus.HELP_SCREEN);
                } else if (input == ButtonAction.GO_UP) {
                    selectOption(true);
                } else if (input == ButtonAction.GO_DOWN) {
                    selectOption(false);
                }
            }

            case MAP_SELECTION -> {
                if (input == ButtonAction.SELECT) {
                    selectMapViaKeyboard();
                } else if (input == ButtonAction.GO_UP) {
                    changeSelectedMap(true);
                } else if (input == ButtonAction.GO_DOWN) {
                    changeSelectedMap(false);
                }
            }
            case RUNNING -> {
                Mario mario = mapManager.getMario();
                if (input == ButtonAction.JUMP) {
                    mario.jump(this);
                } else if (input == ButtonAction.M_RIGHT) {
                    mario.move(true, camera);
                } else if (input == ButtonAction.M_LEFT) {
                    mario.move(false, camera);
                } else if (input == ButtonAction.ACTION_COMPLETED) {
                    mario.setVelX(0);
                } else if (input == ButtonAction.FIRE) {
                    mapManager.fire(this);
                } else if (input == ButtonAction.PAUSE_RESUME) {
                    pauseGame();
                }
            }

            case PAUSED -> {
                if (input == ButtonAction.PAUSE_RESUME) {
                    pauseGame();
                }
            }

            case GAME_OVER, MISSION_PASSED -> {
                if (input == ButtonAction.GO_TO_START_SCREEN) {
                    reset();
                }
            }
        }


        if (input == ButtonAction.GO_TO_START_SCREEN) {
            setGameStatus(GameStatus.START_SCREEN);
        }
    }

    private void selectOption(boolean selectUp) {
        startScreenSelection = startScreenSelection.select(selectUp);
    }

    private void startGame() {
        if (gameStatus != GameStatus.GAME_OVER) {
            setGameStatus(GameStatus.MAP_SELECTION);
        }
    }

    private void pauseGame() {
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

    private boolean isGameOver() {
        if (gameStatus == GameStatus.RUNNING)
            return mapManager.isGameOver();
        return false;
    }


    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
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

    private int passMission() {
        return mapManager.passMission();
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

    public void playFireFlower() {
        soundManager.playFireFlower();
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
