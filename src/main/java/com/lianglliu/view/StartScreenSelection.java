package com.lianglliu.view;

import com.lianglliu.core.IChangeSelector;

public enum StartScreenSelection implements IChangeSelector {
    START_GAME(0),
    VIEW_HELP(1),
    VIEW_ABOUT(2);

    private final int lineNumber;

    StartScreenSelection(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public StartScreenSelection getSelection(int number) {
        return switch (number) {
            case 0 -> START_GAME;
            case 1 -> VIEW_HELP;
            case 2 -> VIEW_ABOUT;
            default -> null;
        };
    }

    public StartScreenSelection select(boolean toUp) {
        int index = changeSelect(lineNumber, toUp);
        return getSelection(index);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int getLength() {
        return StartScreenSelection.values().length;
    }
}
