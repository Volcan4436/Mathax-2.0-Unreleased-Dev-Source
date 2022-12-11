package xyz.mathax.client.utils.gui;

import xyz.mathax.client.gui.DefaultSettingsWidgetFactory;
import xyz.mathax.client.gui.widgets.containers.WTable;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class SettingsWidgetFactory {
    private static final Map<Class<?>, Function<Theme, DefaultSettingsWidgetFactory.Factory>> customFactories = new HashMap<>();

    protected final Map<Class<?>, Factory> factories = new HashMap<>();

    protected final Theme theme;

    public SettingsWidgetFactory(Theme theme) {
        this.theme = theme;
    }

    public static void registerCustomFactory(Class<?> settingClass, Function<Theme, Factory> factoryFunction) {
        customFactories.put(settingClass, factoryFunction);
    }

    public static void unregisterCustomFactory(Class<?> settingClass) {
        customFactories.remove(settingClass);
    }

    public abstract WWidget create(Theme theme, Settings settings, String filter);

    protected Factory getFactory(Class<?> settingClass) {
        if (customFactories.containsKey(settingClass)) return customFactories.get(settingClass).apply(theme);
        return factories.get(settingClass);
    }

    @FunctionalInterface
    public interface Factory {
        void create(WTable table, Setting<?> setting);
    }
}