package com.edwin.model.prize;

import com.edwin.manager.GameEngine;
import com.edwin.model.hero.Mario;

import java.awt.*;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();

    void onTouch(Mario mario, GameEngine engine);

}
