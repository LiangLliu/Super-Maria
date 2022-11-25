package com.edwin.loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {

    private final BufferedImage marioForms;
    private final BufferedImage brickAnimation;

    private ImageLoader() {
        marioForms = loadImage("/mario-forms.png");
        brickAnimation = loadImage("/brick-animation.png");
    }

    private static class Inner {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    public static ImageLoader getSingleton() {
        return Inner.INSTANCE;
    }

    public BufferedImage loadImage(String path) {

        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource("/com/edwin/assets" + path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage getSubImage(BufferedImage image, int col, int row, int w, int h) {
        if ((col == 1 || col == 4) && row == 3) { //koopa
            return image.getSubimage((col - 1) * 48, 128, w, h);
        }
        return image.getSubimage((col - 1) * 48, (row - 1) * 48, w, h);
    }

    public BufferedImage[] getLeftFrames(int marioForm) {
        return getBufferedImages(1, marioForm, 4, 7);
    }

    public BufferedImage[] getRightFrames(int marioForm) {
        return getBufferedImages(2, marioForm, 5, 8);
    }

    private BufferedImage[] getBufferedImages(int col, int marioForm, int col1, int col2) {
        var bufferedImages = new BufferedImage[5];
        int width = 52, height = 48;

        if (marioForm == 1) { //super mario
            col = col1;
            width = 48;
            height = 96;
        } else if (marioForm == 2) { //fire mario
            col = col2;
            width = 48;
            height = 96;
        }

        for (int i = 0; i < bufferedImages.length; i++) {
            bufferedImages[i] = marioForms.getSubimage((col - 1) * width, (i) * height, width, height);
        }
        return bufferedImages;
    }

    public BufferedImage[] getBrickFrames() {
        var frames = new BufferedImage[4];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = brickAnimation.getSubimage(i * 105, 0, 105, 105);
        }
        return frames;
    }
}
