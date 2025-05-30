package com.lianglliu.model.prize;

import com.lianglliu.engine.GameEngine;
import com.lianglliu.model.hero.Mario;
import com.lianglliu.model.hero.MarioForm;
import com.lianglliu.view.Animation;
import com.lianglliu.loader.ImageLoader;

import java.awt.image.BufferedImage;

public class SuperMushroom extends BoostItem{

    public SuperMushroom(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(125);
    }

    @Override
    public void onTouch(Mario mario, GameEngine engine) {
        mario.acquirePoints(getPoint());

        var imageLoader = ImageLoader.getSingleton();

        if(!mario.getMarioForm().isSuper()){
            var leftFrames = imageLoader.getLeftFrames(MarioForm.SUPER);
            var rightFrames = imageLoader.getRightFrames(MarioForm.SUPER);

            var animation = new Animation(leftFrames, rightFrames);
            var newForm = new MarioForm(animation, true, false);
            mario.setMarioForm(newForm);
            mario.setDimension(48, 96);

            engine.playSuperMushroom();
        }
    }
}
