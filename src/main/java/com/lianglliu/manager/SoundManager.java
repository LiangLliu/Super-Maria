package com.lianglliu.manager;

import com.lianglliu.async.AsyncExecutor;
import com.lianglliu.loader.ResourceLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Optional;

public class SoundManager {

    private Clip background;
    private long clipTime = 0;

    public SoundManager() {
        background = getClip(loadAudio("background"));
    }

    private AudioInputStream loadAudio(String url) {
        System.out.println("Loading " + url);
        try {
            String filePath = ResourceLoader.getResourceFilePath("audio/" + url + ".wav");

            var targetStream = new FileInputStream(Objects.requireNonNull(filePath));

            var bufferedIn = new BufferedInputStream(targetStream);
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
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("jump")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playCoin() {

        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("coin")))
                    .ifPresent(DataLine::start);
        });

    }

    public void playFireball() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("fireball")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playGameOver() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("gameOver")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playStomp() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("stomp")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playOneUp() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("oneUp")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playSuperMushroom() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("superMushroom")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playMarioDies() {
        AsyncExecutor.runAsync(() -> {
            Optional.ofNullable(getClip(loadAudio("marioDies")))
                    .ifPresent(DataLine::start);
        });
    }

    public void playFireFlower() {
    }
}
