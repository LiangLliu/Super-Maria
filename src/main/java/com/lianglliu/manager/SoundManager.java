package com.lianglliu.manager;

import com.lianglliu.async.AsyncExecutor;
import com.lianglliu.loader.ResourceLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SoundManager {

    private final Clip background;
    private final Map<SoundType, Clip> soundMaps;
    private long clipTime = 0;


    private enum SoundType {
        JUMP("jump"),
        COIN("coin"),
        FIREBALL("fireball"),
        GAME_OVER("gameOver"),
        STOMP("stomp"),
        ONE_UP("oneUp"),
        SUPER_MUSHROOM("superMushroom"),
        MARIO_DIES("marioDies"),
        ;
        private final String sound;

        SoundType(String sound) {
            this.sound = sound;
        }
    }


    public SoundManager() {
        background = getClip(loadAudio("background"));

        soundMaps = Arrays.stream(SoundType.values())
                .map(soundType -> new AbstractMap.SimpleEntry<>(
                        soundType,
                        AsyncExecutor.supplyAsync(() -> getClip(loadAudio(soundType.sound)))
                ))
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        AsyncExecutor.fetch(entry.getValue())
                ))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private AudioInputStream loadAudio(String url) {
        System.out.println("Loading " + url);
        try {
            var inputStream = ResourceLoader.getResource("audio/" + url + ".wav");
            var bufferedIn = new BufferedInputStream(inputStream);
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
        playSound(SoundType.JUMP);
    }

    public void playCoin() {
        playSound(SoundType.COIN);
    }

    public void playFireball() {
        playSound(SoundType.FIREBALL);
    }

    public void playGameOver() {
        playSound(SoundType.GAME_OVER);
    }

    public void playStomp() {
        playSound(SoundType.STOMP);
    }

    public void playOneUp() {
        playSound(SoundType.ONE_UP);
    }

    public void playSuperMushroom() {
        playSound(SoundType.SUPER_MUSHROOM);
    }

    public void playMarioDies() {
        playSound(SoundType.MARIO_DIES);
    }

    public void playFireFlower() {
    }

    private void playSound(SoundType sound) {
        AsyncExecutor.runAsync(() -> Optional.ofNullable(soundMaps.get(sound))
                .ifPresent(clip -> {
                    if (!clip.isActive()) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }));
    }
}
