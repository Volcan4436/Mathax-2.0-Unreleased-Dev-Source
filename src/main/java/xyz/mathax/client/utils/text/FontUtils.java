package xyz.mathax.client.utils.text;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.renderer.text.Fonts;
import xyz.mathax.client.renderer.text.FontFace;
import xyz.mathax.client.renderer.text.FontFamily;
import xyz.mathax.client.renderer.text.FontInfo;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.files.StreamUtils;
import xyz.mathax.client.utils.misc.MatHaxIdentifier;
import net.minecraft.util.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static xyz.mathax.client.MatHax.mc;

public class FontUtils {
    public static FontInfo getFontInfo(File file) {
        InputStream stream = stream(file);
        if (stream == null) {
            return null;
        }

        byte[] bytes = Utils.readBytes(stream);
        if (bytes.length < 5) {
            return null;
        }

        if (bytes[0] != 0 || bytes[1] != 1 || bytes[2] != 0 || bytes[3] != 0 || bytes[4] != 0) {
            return null;
        }

        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(fontInfo, buffer)) {
            return null;
        }

        ByteBuffer nameBuffer = STBTruetype.stbtt_GetFontNameString(fontInfo, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 1);
        ByteBuffer typeBuffer = STBTruetype.stbtt_GetFontNameString(fontInfo, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 2);
        if (typeBuffer == null || nameBuffer == null) {
            return null;
        }

        return new FontInfo(StandardCharsets.UTF_16.decode(nameBuffer).toString(), FontFace.Type.fromString(StandardCharsets.UTF_16.decode(typeBuffer).toString()));
    }

    public static Set<String> getSearchPaths() {
        Set<String> paths = new HashSet<>();
        paths.add(System.getProperty("java.home") + "/lib/fonts");

        for (File directory : getUFontDirectories()) {
            if (directory.exists()) {
                paths.add(directory.getAbsolutePath());
            }
        }

        for (File directory : getSFontDirectories()) {
            if (directory.exists()) {
                paths.add(directory.getAbsolutePath());
            }
        }

        return paths;
    }

    public static List<File> getUFontDirectories() {
        return switch (Util.getOperatingSystem()) {
            case WINDOWS -> List.of(new File(System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Windows\\Fonts"));
            case OSX -> List.of(new File(System.getProperty("user.home") + "/Library/Fonts/"));
            default -> List.of(new File(System.getProperty("user.home") + "/.local/share/fonts"), new File(System.getProperty("user.home") + "/.fonts"));
        };
    }

    public static List<File> getSFontDirectories() {
        return switch (Util.getOperatingSystem()) {
            case WINDOWS -> List.of(new File(System.getenv("SystemRoot") + "\\Fonts"));
            case OSX -> List.of(new File("/System/Library/Fonts/"));
            default -> List.of(new File("/usr/share/fonts/"));
        };
    }

    public static File getDirectory(List<File> directories) {
        for (File directory : directories) {
            if (directory.exists()) {
                return directory;
            }
        }

        directories.get(0).mkdirs();
        return directories.get(0);
    }

    public static void collectFonts(List<FontFamily> fontList, File directory, Consumer<File> consumer) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles((file) -> (file.isFile() && file.getName().endsWith(".ttf") || file.isDirectory()));
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                collectFonts(fontList, file, consumer);
                continue;
            }

            FontInfo fontInfo = FontUtils.getFontInfo(file);
            if (fontInfo != null) {
                consumer.accept(file);

                FontFamily family = Fonts.getFamily(fontInfo.family());
                if (family == null) {
                    family = new FontFamily(fontInfo.family());
                    fontList.add(family);
                }

                if (family.add(file) == null) {
                    MatHax.LOG.warn("Failed to load font {} {}.", fontInfo.family(), fontInfo.type());
                }
            }
        }
    }

    public static void copyBuiltin(String name, File target) {
        try {
            File fontFile = new File(MatHax.VERSION_FOLDER + "/Temp", name + ".ttf");
            fontFile.createNewFile();
            InputStream stream = mc.getResourceManager().getResource(new MatHaxIdentifier("fonts/" + name + ".ttf")).get().getInputStream();
            StreamUtils.copy(stream, fontFile);
            Files.copy(fontFile.toPath(), new File(target, fontFile.getName()).toPath(), REPLACE_EXISTING);
            fontFile.delete();
        } catch (Exception exception) {
            MatHax.LOG.error("Failed to copy builtin font {} to {}.", name, target.getAbsolutePath());
            exception.printStackTrace();

            if (name.equals(Fonts.DEFAULT_FONT_FAMILY)) {
                throw new RuntimeException("Failed to load default font.");
            }
        }
    }

    public static InputStream stream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
