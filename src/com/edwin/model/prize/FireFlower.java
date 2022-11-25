package com.edwin.model.prize;

import com.edwin.engine.GameEngine;
import com.edwin.model.hero.Mario;
import com.edwin.model.hero.MarioForm;
import com.edwin.view.Animation;
import com.edwin.loader.ImageLoader;

import java.awt.image.BufferedImage;

public class FireFlower extends BoostItem {

    public FireFlower(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(150);
    }

    @Override
    public void onTouch(Mario mario, GameEngine engine) {
        mario.acquirePoints(getPoint());

        var imageLoader = ImageLoader.getSingleton();

        if(!mario.getMarioForm().isFire()){
            var leftFrames = imageLoader.getLeftFrames(MarioForm.FIRE);
            var rightFrames = imageLoader.getRightFrames(MarioForm.FIRE);

            var animation = new Animation(leftFrames, rightFrames);
            var newForm = new MarioForm(animation, true, true);
            mario.setMarioForm(newForm);
            mario.setDimension(48, 96);

            engine.playFireFlower();
        }
    }

    @Override
    public void updateLocation(){}

}
