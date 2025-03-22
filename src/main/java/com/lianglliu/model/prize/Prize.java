package com.lianglliu.model.prize;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.model.hero.Mario;

import java.awt.*;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();

    void onTouch(Mario mario, GameEngine engine);

}
