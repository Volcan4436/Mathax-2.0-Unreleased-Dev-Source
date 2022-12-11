package xyz.mathax.client.settings.screens;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.utils.misc.Names;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

import java.util.List;

public class ParticleTypeListSettingScreen extends LeftRightListSettingScreen<ParticleType<?>> {
    public ParticleTypeListSettingScreen(Theme theme, Setting<List<ParticleType<?>>> setting) {
        super(theme, "Select Particles", setting, setting.get(), Registries.PARTICLE_TYPE);
    }

    @Override
    protected WWidget getValueWidget(ParticleType<?> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(ParticleType<?> value) {
        return Names.get(value);
    }

    @Override
    protected boolean skipValue(ParticleType<?> value) {
        return !(value instanceof ParticleEffect);
    }
}
