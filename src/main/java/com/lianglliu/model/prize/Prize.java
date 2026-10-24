package com.lianglliu.model.prize;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.hero.Mario;

import java.awt.Rectangle;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();

    void onTouch(Mario mario, GameContext context);
}
