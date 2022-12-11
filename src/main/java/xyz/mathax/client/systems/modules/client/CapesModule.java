package xyz.mathax.client.systems.modules.client;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.gui.widgets.containers.WHorizontalList;
import xyz.mathax.client.gui.widgets.pressable.WButton;
import xyz.mathax.client.settings.BoolSetting;
import xyz.mathax.client.settings.IntSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.utils.network.Capes;

public class CapesModule extends Module {
    private int timer = 0;

    private final SettingGroup autoReloadSettings = settings.createGroup("Auto Reload");

    // General

    private final Setting<Boolean> autoReloadSetting = autoReloadSettings.add(new BoolSetting.Builder()
            .name("Enabled")
            .description("Automatically reload capes.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> autoReloadDelaySetting = autoReloadSettings.add(new IntSetting.Builder()
            .name("Delay")
            .description("Delay between reloads in ticks.")
            .defaultValue(12000)
            .min(0)
            .sliderRange(6000, 36000)
            .build()
    );

    // Buttons

    @Override
    public WWidget getWidget(Theme theme) {
        WHorizontalList horizontalList = theme.horizontalList();

        WButton reload = horizontalList.add(theme.button("Reload")).widget();
        reload.action = () -> {
            if (isEnabled()) {
                Capes.refresh();
            }
        };
        horizontalList.add(theme.label("Reloads capes."));

        return horizontalList;
    }

    public CapesModule(Category category) {
        super(category, "Capes", "Shows MatHax capes on users which have them.", true);
    }

    @Override
    public void onEnable() {
        timer = 0;

        Capes.refresh();
    }

    @Override
    public void onDisable() {
        Capes.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!autoReloadSetting.get()) {
            return;
        }

        if (timer >= autoReloadDelaySetting.get()) {
            timer = 0;
            Capes.refresh();
        }

        timer++;
    }
}