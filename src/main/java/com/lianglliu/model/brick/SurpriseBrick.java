package com.lianglliu.model.brick;

import com.lianglliu.model.GameContext;
import com.lianglliu.model.prize.Prize;

import java.awt.image.BufferedImage;

public class SurpriseBrick extends Brick {

    private final BufferedImage emptyStyle;
    private Prize prize;

    public SurpriseBrick(double x, double y, BufferedImage style, BufferedImage emptyStyle, Prize prize) {
        super(x, y, style);
        this.emptyStyle = emptyStyle;
        this.prize = prize;
    }

    @Override
    public Prize reveal(GameContext context) {
        if (prize != null) {
            prize.reveal();
        }

        setStyle(emptyStyle);

        var revealed = prize;
        prize = null;
        return revealed;
    }
}
