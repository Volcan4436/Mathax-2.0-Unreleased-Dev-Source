package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.ParticleTypeListSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;

import java.util.List;

public class Trail extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<List<ParticleType<?>>> particlesSetting = generalSettings.add(new ParticleTypeListSetting.Builder()
            .name("Particles")
            .description("Particles to draw.")
            .defaultValue(
                    ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    ParticleTypes.CAMPFIRE_COSY_SMOKE
            )
            .build()
    );

    private final Setting<Boolean> pauseSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Pause when stationary")
            .description("Whether or not to add particles when you are not moving.")
            .defaultValue(true)
            .build()
    );

    public Trail(Category category) {
        super(category, "Trail", "Renders a customizable trail behind you.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (pauseSetting.get() && mc.player.getVelocity().x == 0 && mc.player.getVelocity().y == 0 && mc.player.getVelocity().z == 0) {
            return;
        }

        for (ParticleType<?> particleType : particlesSetting.get()) {
            mc.world.addParticle((ParticleEffect) particleType, mc.player.getX(), mc.player.getY(), mc.player.getZ(), 0, 0, 0);
        }
    }
}