package xyz.mathax.client.settings.screens;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.systems.modules.Modules;

import java.util.List;

public class ModuleListSettingScreen extends LeftRightListSettingScreen<Module> {
    public ModuleListSettingScreen(Theme theme, Setting<List<Module>> setting) {
        super(theme, "Select Modules", setting, setting.get(), Modules.REGISTRY);
    }

    @Override
    protected WWidget getValueWidget(Module value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Module value) {
        return value.name;
    }
}