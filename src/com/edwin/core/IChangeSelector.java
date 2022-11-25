package com.edwin.core;

public interface IChangeSelector {

    int getLength();

    default int changeSelect(int currentIndex, boolean up) {
        return up
                ? selectUp(currentIndex)
                : selectDown(currentIndex);
    }

    default int selectUp(int currentIndex) {
        return (currentIndex <= 0)
                ? getLength() - 1
                : currentIndex - 1;
    }

    default int selectDown(int currentIndex) {
        return (currentIndex >= getLength() - 1)
                ? 0
                : currentIndex + 1;
    }
}
