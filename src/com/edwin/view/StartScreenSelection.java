package com.edwin.view;

public enum StartScreenSelection {
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
        int selection;

        if (lineNumber > -1 && lineNumber < 3) {
            selection = lineNumber - (toUp ? 1 : -1);
            if (selection == -1)
                selection = 2;
            else if (selection == 3)
                selection = 0;
            return getSelection(selection);
        }

        return null;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
