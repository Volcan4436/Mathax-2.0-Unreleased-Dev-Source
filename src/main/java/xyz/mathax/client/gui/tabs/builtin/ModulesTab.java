package xyz.mathax.client.gui.tabs.builtin;

import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.systems.themes.Themes;
import xyz.mathax.client.gui.tabs.Tab;
import xyz.mathax.client.gui.tabs.TabScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModulesTab extends Tab {
    public ModulesTab() {
        super("Modules");
    }

    @Override
    public TabScreen createScreen(Theme theme) {
        return theme.modulesScreen();
    }

    @Override
    public boolean isScreen(Screen screen) {
        return Systems.get(Themes.class).getTheme().isModulesScreen(screen);
    }
}
