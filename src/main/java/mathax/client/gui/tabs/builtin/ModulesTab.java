package mathax.client.gui.tabs.builtin;

import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.Themes;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
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
        return Themes.getTheme().isModulesScreen(screen);
    }
}
