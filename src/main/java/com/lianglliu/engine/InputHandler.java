package com.lianglliu.engine;

import com.lianglliu.manager.ButtonAction;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.view.StartScreenSelection;

/**
 * Turns player actions into game commands. Runs on the EDT.
 */
public class InputHandler {

    private final GameEngine gameEngine;

    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void handleInput(ButtonAction input) {
        switch (gameEngine.getGameStatus()) {
            case START_SCREEN -> handleStartScreenInput(input);
            case MAP_SELECTION -> handleMapSelectionInput(input);
            case RUNNING -> handleGameplayInput(input);
            case PAUSED -> handlePausedInput(input);
            case GAME_OVER, MISSION_PASSED -> handleGameEndInput(input);
            case HELP_SCREEN, ABOUT_SCREEN -> {
                // only ESC leaves these screens, handled below
            }
        }

        if (input == ButtonAction.GO_TO_START_SCREEN) {
            gameEngine.setGameStatus(GameStatus.START_SCREEN);
        }
    }

    private void handleStartScreenInput(ButtonAction input) {
        StartScreenSelection selection = gameEngine.getStartScreenSelection();

        switch (input) {
            case SELECT -> {
                if (selection == StartScreenSelection.START_GAME) {
                    gameEngine.startGame();
                } else if (selection == StartScreenSelection.VIEW_ABOUT) {
                    gameEngine.setGameStatus(GameStatus.ABOUT_SCREEN);
                } else if (selection == StartScreenSelection.VIEW_HELP) {
                    gameEngine.setGameStatus(GameStatus.HELP_SCREEN);
                }
            }
            case GO_UP -> gameEngine.selectOption(true);
            case GO_DOWN -> gameEngine.selectOption(false);
        }
    }

    private void handleMapSelectionInput(ButtonAction input) {
        switch (input) {
            case SELECT -> gameEngine.selectMapViaKeyboard();
            case GO_UP -> gameEngine.changeSelectedMap(true);
            case GO_DOWN -> gameEngine.changeSelectedMap(false);
        }
    }

    private void handleGameplayInput(ButtonAction input) {
        var mapManager = gameEngine.getMapManager();

        switch (input) {
            case JUMP -> mapManager.jump(gameEngine);
            case M_RIGHT -> mapManager.move(true);
            case M_LEFT -> mapManager.move(false);
            case ACTION_COMPLETED -> mapManager.stopMoving();
            case FIRE -> mapManager.fire(gameEngine);
            case PAUSE_RESUME -> gameEngine.pauseGame();
        }
    }

    private void handlePausedInput(ButtonAction input) {
        if (input == ButtonAction.PAUSE_RESUME) {
            gameEngine.pauseGame();
        }
    }

    private void handleGameEndInput(ButtonAction input) {
        if (input == ButtonAction.GO_TO_START_SCREEN) {
            gameEngine.reset();
        }
    }
}
