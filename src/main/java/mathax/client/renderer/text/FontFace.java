package mathax.client.renderer.text;

import mathax.client.utils.text.FontUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public record FontFace(FontInfo info, Path path) {
    public File getFile() {
        return path.toFile();
    }

    public InputStream asStream() {
        InputStream in = FontUtils.stream(path.toFile());
        if (in == null) {
            throw new RuntimeException("Font " + this + " couldn't be loaded");
        }

        return in;
    }

    @Override
    public String toString() {
        return info().family() + " " + info().type();
    }

    public boolean equals(FontFace fontFace) {
        if (fontFace == this) {
            return true;
        }

        if (fontFace == null) {
            return false;
        }

        return info.equals(fontFace.info);
    }

    public enum Type {
        Regular,
        Bold,
        Italic,
        Bold_Italic;

        public static Type fromString(String str) {
            return switch (str) {
                case "Bold" -> Bold;
                case "Italic" -> Italic;
                case "Bold Italic", "BoldItalic" -> Bold_Italic;
                default -> Regular;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case Bold -> "Bold";
                case Italic -> "Italic";
                case Bold_Italic -> "Bold Italic";
                default -> "Regular";
            };
        }
    }
}
