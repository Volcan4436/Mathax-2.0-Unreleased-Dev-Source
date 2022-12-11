package xyz.mathax.client.systems.modules.misc;

import xyz.mathax.client.settings.IntSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;

public class AutoReconnect extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    public final Setting<Integer> timeSetting = generalSettings.add(new IntSetting.Builder()
            .name("Delay")
            .description("The amount of seconds to wait before reconnecting to the server in ticks.")
            .defaultValue(100)
            .min(0)
            .sliderRange(0, 300)
            .build()
    );

    public AutoReconnect(Category category) {
        super(category, "Auto Reconnect", "Automatically reconnects when disconnected from a server.");
    }
}