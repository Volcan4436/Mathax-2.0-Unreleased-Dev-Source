package xyz.mathax.client.systems.macros;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.eventbus.EventPriority;
import xyz.mathax.client.events.mathax.KeyEvent;
import xyz.mathax.client.events.mathax.MouseButtonEvent;
import xyz.mathax.client.systems.System;
import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.utils.input.KeyAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Macros extends System<Macros> implements Iterable<Macro> {
    public static final File MACROS_FOLDER = new File(MatHax.VERSION_FOLDER, "Macros");

    private List<Macro> macros = new ArrayList<>();

    public Macros() {
        super("Macros", null);
    }

    public static Macros get() {
        return Systems.get(Macros.class);
    }

    public void add(Macro macro) {
        macros.add(macro);
        MatHax.EVENT_BUS.subscribe(macro);
        save();
    }

    public List<Macro> getAll() {
        return macros;
    }

    public void remove(Macro macro) {
        if (macros.remove(macro)) {
            MatHax.EVENT_BUS.unsubscribe(macro);
            save();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Release) {
            return;
        }

        for (Macro macro : macros) {
            if (macro.onAction(true, event.key)) {
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Release) {
            return;
        }

        for (Macro macro : macros) {
            if (macro.onAction(false, event.button)) {
                return;
            }
        }
    }

    public boolean isEmpty() {
        return macros.isEmpty();
    }

    @Override
    public Iterator<Macro> iterator() {
        return macros.iterator();
    }

    @Override
    public void save(File folder) {
        if (folder == null) {
            folder = MACROS_FOLDER;
        }

        folder = new File(folder, "Macros");
        folder.mkdirs();

        for (Macro macro : macros) {
            macro.save(folder);
        }
    }

    @Override
    public void load(File folder) {
        if (folder == null) {
            folder = MACROS_FOLDER;
        }

        folder = new File(folder, "Macros");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".json")) {
                    return;
                }

                Macro macro = new Macro();
                macro.load(file);
                macros.add(macro);
            }
        }
    }
}
