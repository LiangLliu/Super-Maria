package com.edwin.manager;

import com.edwin.engine.GameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

public class InputManager implements KeyListener, MouseListener {

    private GameEngine engine;

    public InputManager(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        var status = engine.getGameStatus();

        var currentAction = switch (keyCode) {
            case VK_UP -> {
                if (status == GameStatus.START_SCREEN || status == GameStatus.MAP_SELECTION)
                    yield ButtonAction.GO_UP;
                else
                    yield ButtonAction.JUMP;
            }
            case VK_DOWN -> {
                if (status == GameStatus.START_SCREEN || status == GameStatus.MAP_SELECTION) {
                    yield ButtonAction.GO_DOWN;
                }
                yield ButtonAction.NO_ACTION;
            }

            case VK_RIGHT -> ButtonAction.M_RIGHT;
            case VK_LEFT -> ButtonAction.M_LEFT;
            case VK_ENTER -> ButtonAction.SELECT;
            case VK_ESCAPE -> (status == GameStatus.RUNNING || status == GameStatus.PAUSED)
                    ? ButtonAction.PAUSE_RESUME
                    : ButtonAction.GO_TO_START_SCREEN;
            case VK_SPACE -> ButtonAction.FIRE;
            default -> ButtonAction.NO_ACTION;
        };

        notifyInput(currentAction);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (engine.getGameStatus() == GameStatus.MAP_SELECTION) {
            engine.selectMapViaMouse();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == VK_RIGHT || event.getKeyCode() == VK_LEFT)
            notifyInput(ButtonAction.ACTION_COMPLETED);
    }

    private void notifyInput(ButtonAction action) {
        if (action != ButtonAction.NO_ACTION)
            engine.receiveInput(action);
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
