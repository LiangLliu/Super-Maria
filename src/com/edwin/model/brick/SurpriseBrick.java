package com.edwin.model.brick;

import com.edwin.engine.GameEngine;
import com.edwin.manager.Images;
import com.edwin.model.prize.Prize;

import java.awt.image.BufferedImage;

public class SurpriseBrick extends Brick{

    private Prize prize;

    public SurpriseBrick(double x, double y, BufferedImage style, Prize prize) {
        super(x, y, style);
        setBreakable(false);
        setEmpty(false);
        this.prize = prize;
    }

    @Override
    public Prize reveal(GameEngine engine){

        if(prize != null){
            prize.reveal();
        }

        setEmpty(true);
        setStyle(Images.surpriseBrickStyle);

        var toReturn = this.prize;
        this.prize = null;
        return toReturn;
    }

    @Override
    public Prize getPrize(){
        return prize;
    }
}
