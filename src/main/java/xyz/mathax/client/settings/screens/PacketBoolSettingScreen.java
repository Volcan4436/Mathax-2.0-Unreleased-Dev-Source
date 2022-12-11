package xyz.mathax.client.settings.screens;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.settings.PacketListSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.utils.network.PacketUtils;
import net.minecraft.network.Packet;

import java.util.Set;
import java.util.function.Predicate;

public class PacketBoolSettingScreen extends LeftRightListSettingScreen<Class<? extends Packet<?>>> {
    public PacketBoolSettingScreen(Theme theme, Setting<Set<Class<? extends Packet<?>>>> setting) {
        super(theme, "Select Packets", setting, setting.get(), PacketUtils.REGISTRY);
    }

    @Override
    protected boolean includeValue(Class<? extends Packet<?>> value) {
        Predicate<Class<? extends Packet<?>>> filter = ((PacketListSetting) setting).filter;
        if (filter == null) {
            return true;
        }

        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Class<? extends Packet<?>> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Class<? extends Packet<?>> value) {
        return PacketUtils.getName(value);
    }
}
