package com.lianglliu.engine;

import com.lianglliu.manager.GameStatus;
import com.lianglliu.manager.StartScreenSelection;

/**
 * The only place that interprets a player intent for the current game state.
 * Runs on the EDT.
 */
public class InputHandler {

    private final GameEngine gameEngine;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void handleInput(Input input) {
        switch (gameEngine.status()) {
            case START_SCREEN -> handleStartScreen(input);
            case MAP_SELECTION -> handleMapSelection(input);
            case RUNNING -> handleGameplay(input);
            case PAUSED -> handlePaused(input);
            case HELP_SCREEN, ABOUT_SCREEN -> handleInfoScreen(input);
            case GAME_OVER, MISSION_PASSED -> handleGameEnd(input);
        }
    }

    private void handleStartScreen(Input input) {
        switch (input) {
            case UP -> gameEngine.selectOption(true);
            case DOWN -> gameEngine.selectOption(false);
            case CONFIRM -> {
                StartScreenSelection selection = gameEngine.getStartScreenSelection();
                if (selection == StartScreenSelection.START_GAME) {
                    gameEngine.startGame();
                } else if (selection == StartScreenSelection.VIEW_ABOUT) {
                    gameEngine.setGameStatus(GameStatus.ABOUT_SCREEN);
                } else {
                    gameEngine.setGameStatus(GameStatus.HELP_SCREEN);
                }
            }
        }
    }

    private void handleMapSelection(Input input) {
        switch (input) {
            case UP -> gameEngine.changeSelectedMap(true);
            case DOWN -> gameEngine.changeSelectedMap(false);
            case CONFIRM -> gameEngine.startSelectedMap();
            case CANCEL -> gameEngine.setGameStatus(GameStatus.START_SCREEN);
        }
    }

    private void handleGameplay(Input input) {
        var mapManager = gameEngine.getMapManager();

        switch (input) {
            case UP -> mapManager.jump(gameEngine);
            case LEFT -> mapManager.move(false);
            case RIGHT -> mapManager.move(true);
            case STOP -> mapManager.stopMoving();
            case ACTION -> mapManager.fire(gameEngine);
            case CANCEL -> gameEngine.pauseGame();
        }
    }

    private void handlePaused(Input input) {
        if (input == Input.CANCEL) {
            gameEngine.pauseGame();
        }
    }

    private void handleInfoScreen(Input input) {
        if (input == Input.CANCEL) {
            gameEngine.setGameStatus(GameStatus.START_SCREEN);
        }
    }

    private void handleGameEnd(Input input) {
        if (input == Input.CANCEL) {
            gameEngine.reset();
        }
    }
}
