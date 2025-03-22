package com.lianglliu.model.brick;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.manager.Images;
import com.lianglliu.model.prize.Prize;

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
