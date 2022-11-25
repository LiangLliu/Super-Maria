package com.edwin.loader;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontLoader {

    public static Font getMarioFont() {
        try {
            var ins = FontLoader.class.getResourceAsStream("/com/edwin/assets/font/mario-font.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, ins);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Verdana", Font.PLAIN, 12);
        }
    }
}
