package xyz.mathax.client.renderer.text;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.init.PreInit;
import xyz.mathax.client.renderer.Shaders;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.text.FontUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Fonts {
    public static CustomTextRenderer RENDERER;

    public static final List<FontFamily> FONT_FAMILIES = new ArrayList<>();

    private static final String[] BUILTIN_FONTS = {
            "Comfortaa",
            "JetBrains Mono",
            "Pixelation",
            "Tw Cen MT"
    };

    public static String DEFAULT_FONT_FAMILY;

    public static FontFace DEFAULT_FONT;

    @PreInit(dependencies = Shaders.class)
    public static void refresh() {
        File target = FontUtils.getDirectory(FontUtils.getUFontDirectories());
        for (String builtinFont : BUILTIN_FONTS) {
            FontUtils.copyBuiltin(builtinFont, target);
        }

        FONT_FAMILIES.clear();

        MatHax.LOG.info("Searching for font families...");

        long start = Utils.getCurrentTimeMillis();

        for (String fontPath : FontUtils.getSearchPaths()) {
            FontUtils.collectFonts(FONT_FAMILIES, new File(fontPath), file -> {
                if (file.getAbsolutePath().endsWith(BUILTIN_FONTS[0] + ".ttf")) {
                    DEFAULT_FONT_FAMILY = FontUtils.getFontInfo(file).family();
                }
            });
        }

        FONT_FAMILIES.sort(Comparator.comparing(FontFamily::getName));

        MatHax.LOG.info("Found {} font families in {} milliseconds.", FONT_FAMILIES.size(), Utils.getCurrentTimeMillis() - start);

        DEFAULT_FONT = getFamily(DEFAULT_FONT_FAMILY).get(FontFace.Type.Regular);
    }

    public static void load(FontFace fontFace) {
        if (fontFace == null) {
            fontFace = DEFAULT_FONT;
        }

        if (RENDERER != null && RENDERER.fontFace.equals(fontFace)) {
            return;
        }

        MatHax.LOG.info("Loading font {} {}...", fontFace.info().family(), fontFace.info().type());

        long start = Utils.getCurrentTimeMillis();

        try {
            RENDERER = new CustomTextRenderer(fontFace);

            MatHax.LOG.info("Loaded font {} {} in {} milliseconds.", fontFace.info().family(), fontFace.info().type(),  Utils.getCurrentTimeMillis() - start);
        } catch (Exception exception) {
            if (fontFace.equals(DEFAULT_FONT)) {
                throw new RuntimeException("Failed to load default font: " + fontFace, exception);
            }

            MatHax.LOG.error("Failed to load font: " + fontFace, exception);
            load(Fonts.DEFAULT_FONT);
        }
    }

    public static FontFamily getFamily(String name) {
        for (FontFamily fontFamily : Fonts.FONT_FAMILIES) {
            if (fontFamily.getName().equalsIgnoreCase(name)) {
                return fontFamily;
            }
        }

        return null;
    }
}