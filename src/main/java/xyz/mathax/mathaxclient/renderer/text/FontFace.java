package xyz.mathax.mathaxclient.renderer.text;

import xyz.mathax.mathaxclient.utils.text.FontUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class FontFace {
    public final FontInfo info;

    protected FontFace(FontInfo info) {
        this.info = info;
    }

    public abstract InputStream toStream();

    @Override
    public String toString() {
        return info.toString();
    }
}