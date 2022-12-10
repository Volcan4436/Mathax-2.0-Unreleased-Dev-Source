package mathax.client.systems.modules.movement;

import mathax.client.settings.BoolSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;

public class TridentBoost extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Double> multiplierSetting = generalSettings.add(new DoubleSetting.Builder()
            .name("Boost")
            .description("How much your velocity is multiplied by when using riptide.")
            .defaultValue(2)
            .min(0.1)
            .sliderRange(1, 3)
            .build()
    );

    private final Setting<Boolean> allowOutOfWaterSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Out of water")
            .description("Whether riptide should work out of water")
            .defaultValue(true)
            .build()
    );

    public TridentBoost(Category category) {
        super(category, "Trident Boost", "Boosts you when using riptide with a trident.");
    }

    public double getMultiplier() {
        return isEnabled() ? multiplierSetting.get() : 1;
    }

    public boolean allowOutOfWater() {
        return isEnabled() ? allowOutOfWaterSetting.get() : false;
    }
}