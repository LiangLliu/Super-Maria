package com.lianglliu.manager;

import com.lianglliu.loader.ResourceLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;

public class SoundManager {

    private Clip background;
    private long clipTime = 0;

    public SoundManager() {
        background = getClip(loadAudio("background"));
    }

    private AudioInputStream loadAudio(String url) {
        try {
            var filePath = ResourceLoader.getResourceFilePath("audio/" + url + ".wav");
            assert filePath != null;
            var audioSrc = getClass().getResourceAsStream(filePath);
            assert audioSrc != null;
            var bufferedIn = new BufferedInputStream(audioSrc);
            return AudioSystem.getAudioInputStream(bufferedIn);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private Clip getClip(AudioInputStream stream) {

        try {
            var clip = AudioSystem.getClip();
            clip.open(stream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void resumeBackground() {
        background.setMicrosecondPosition(clipTime);
        background.start();
    }

    public void pauseBackground() {
        clipTime = background.getMicrosecondPosition();
        background.stop();
    }

    public void restartBackground() {
        clipTime = 0;
        resumeBackground();
    }

    public void playJump() {
        var clip = getClip(loadAudio("jump"));
        clip.start();
    }

    public void playCoin() {
        var clip = getClip(loadAudio("coin"));
        clip.start();

    }

    public void playFireball() {
        var clip = getClip(loadAudio("fireball"));
        clip.start();

    }

    public void playGameOver() {
        var clip = getClip(loadAudio("gameOver"));
        clip.start();
    }

    public void playStomp() {
        var clip = getClip(loadAudio("stomp"));
        clip.start();
    }

    public void playOneUp() {
        var clip = getClip(loadAudio("oneUp"));
        clip.start();
    }

    public void playSuperMushroom() {

        var clip = getClip(loadAudio("superMushroom"));
        clip.start();
    }

    public void playMarioDies() {

        var clip = getClip(loadAudio("marioDies"));
        clip.start();
    }

    public void playFireFlower() {

    }
}
