package com.lianglliu.loader;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontLoader {

    public static Font getMarioFont() {
        try {
            var inputStream = ResourceLoader.getResource("font/SuperMario256.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Verdana", Font.PLAIN, 12);
        }
    }
}
