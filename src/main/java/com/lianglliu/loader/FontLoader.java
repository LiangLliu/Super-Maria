package com.lianglliu.loader;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class FontLoader {

    public static Font getMarioFont() {
        try {
            String filePath = ResourceLoader.getResourceFilePath("font/SuperMario256.ttf");
            var inputStream = new FileInputStream(Objects.requireNonNull(filePath));
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Verdana", Font.PLAIN, 12);
        }
    }
}
