package xyz.mathax.client.renderer.text;

import xyz.mathax.client.utils.text.FontUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontFamily {
    private final List<FontFace> fonts = new ArrayList<>();

    private final String name;

    public FontFamily(String name) {
        this.name = name;
    }

    public FontFace add(File fontFile) {
        FontInfo info = FontUtils.getFontInfo(fontFile);
        if (info == null) {
            return null;
        }

        for (FontFace font : fonts) {
            if (font.info().equals(info)) {
                return null;
            }
        }

        FontFace font = new FontFace(info, fontFile.toPath());
        fonts.add(font);

        return font;
    }

    public FontFace get(FontFace.Type type) {
        if (type == null) {
            return null;
        }

        for (FontFace font : fonts) {
            if (font.info().type().equals(type)) {
                return font;
            }
        }

        return null;
    }

    public boolean has(FontFace.Type type) {
        return get(type) != null;
    }

    public boolean contains(FontFace fontFace) {
        if (fontFace == null) {
            return false;
        }

        return fonts.contains(fontFace);
    }

    public String getName() {
        return name;
    }
}