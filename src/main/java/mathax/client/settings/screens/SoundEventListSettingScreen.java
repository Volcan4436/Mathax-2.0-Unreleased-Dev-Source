package mathax.client.settings.screens;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.settings.Setting;
import mathax.client.utils.misc.Names;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public class SoundEventListSettingScreen extends LeftRightListSettingScreen<SoundEvent> {
    public SoundEventListSettingScreen(Theme theme, Setting<List<SoundEvent>> setting) {
        super(theme, "Select Sounds", setting, setting.get(), Registries.SOUND_EVENT);
    }

    @Override
    protected WWidget getValueWidget(SoundEvent value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(SoundEvent value) {
        return Names.getSoundName(value.getId());
    }
}
