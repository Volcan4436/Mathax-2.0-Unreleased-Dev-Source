package mathax.client.systems.themes;

import mathax.client.MatHax;
import mathax.client.renderer.text.Fonts;
import mathax.client.systems.System;
import mathax.client.systems.Systems;

import java.io.File;

public class Themes extends System<Themes> {
    private static final Theme theme = new Theme();

    public Themes() {
        super("Themes", MatHax.VERSION_FOLDER);
    }

    public static Themes get() {
        return Systems.get(Themes.class);
    }

    @Override
    public void init() {
        load();
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public void save(File folder) {
        File file;
        if (folder != null) {
            file = new File(folder, getFile().getName());
        } else {
            file = getFile();
        }

        theme.save(file);
    }

    @Override
    public void load(File folder) {
        File file;
        if (folder != null) {
            file = new File(folder, getFile().getName());
        } else {
            file = getFile();
        }

        theme.load(file);

        Fonts.load(theme.font());
    }
}