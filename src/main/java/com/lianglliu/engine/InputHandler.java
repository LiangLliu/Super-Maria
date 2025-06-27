package com.lianglliu.engine;

import com.lianglliu.manager.ButtonAction;
import com.lianglliu.manager.GameStatus;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.view.StartScreenSelection;

/**
 * 输入处理器 - 从GameEngine中分离出来的输入逻辑
 */
public class InputHandler {
    
    private final GameEngine gameEngine;
    
    public InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    public void handleInput(ButtonAction input) {
        GameStatus gameStatus = gameEngine.getGameStatus();
        
        switch (gameStatus) {
            case START_SCREEN -> handleStartScreenInput(input);
            case MAP_SELECTION -> handleMapSelectionInput(input);
            case RUNNING -> handleGameplayInput(input);
            case PAUSED -> handlePausedInput(input);
            case GAME_OVER, MISSION_PASSED -> handleGameEndInput(input);
        }
        
        // 全局ESC键处理
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
        Mario mario = gameEngine.getMapManager().getMario();
        
        switch (input) {
            case JUMP -> mario.jump(gameEngine);
            case M_RIGHT -> mario.move(true, gameEngine.getCamera());
            case M_LEFT -> mario.move(false, gameEngine.getCamera());
            case ACTION_COMPLETED -> mario.setVelX(0);
            case FIRE -> gameEngine.getMapManager().fire(gameEngine);
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