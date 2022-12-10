package mathax.client.systems.modules.world;

import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;

public class Timer extends Module {
    public static final double OFF = 1;
    private double override = 1;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Double> multiplierSetting = generalSettings.add(new DoubleSetting.Builder()
            .name("Multiplier")
            .description("The timer multiplier amount.")
            .defaultValue(1)
            .min(0.1)
            .sliderRange(0.1, 2.5)
            .build()
    );

    public Timer(Category category) {
        super(category, "Timer", "Changes the speed of everything in your game.");
    }

    public double getMultiplier() {
        return override != OFF ? override : (isEnabled() ? multiplierSetting.get() : OFF);
    }

    public void setOverride(double override) {
        this.override = override;
    }
}