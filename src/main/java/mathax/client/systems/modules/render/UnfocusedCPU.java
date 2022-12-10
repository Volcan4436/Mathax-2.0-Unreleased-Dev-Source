package mathax.client.systems.modules.render;

import mathax.client.settings.IntSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;

public class UnfocusedCPU extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    public final Setting<Integer> fpsSetting = generalSettings.add(new IntSetting.Builder()
            .name("Target FPS")
            .description("Target FPS to set as the limit when the window is not focused.")
            .min(1)
            .defaultValue(1)
            .sliderRange(1, 30)
            .build()
    );

    public UnfocusedCPU(Category category) {
        super(category, "Unfocused CPU", "Saves performance by limiting FPS when your Minecraft window is not focused.");
    }
}
