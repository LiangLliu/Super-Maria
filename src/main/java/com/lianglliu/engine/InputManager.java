package com.lianglliu.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

/**
 * Translates AWT events into raw {@link Input} intents. It never looks at the
 * game state - that decision belongs to {@link InputHandler}.
 */
public class InputManager extends MouseAdapter implements KeyListener {

    private final GameEngine engine;

    public InputManager(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        var input = switch (event.getKeyCode()) {
            case VK_UP, VK_W -> Input.UP;
            case VK_DOWN, VK_S -> Input.DOWN;
            case VK_LEFT, VK_A -> Input.LEFT;
            case VK_RIGHT, VK_D -> Input.RIGHT;
            case VK_ENTER -> Input.CONFIRM;
            case VK_ESCAPE -> Input.CANCEL;
            case VK_SPACE -> Input.ACTION;
            default -> null;
        };

        if (input != null) {
            engine.receiveInput(input);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == VK_RIGHT || event.getKeyCode() == VK_LEFT) {
            engine.receiveInput(Input.STOP);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        engine.selectMapAt(event.getY());
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}
