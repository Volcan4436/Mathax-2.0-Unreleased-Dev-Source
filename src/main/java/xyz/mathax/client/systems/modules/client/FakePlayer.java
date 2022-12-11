package xyz.mathax.client.systems.modules.client;

import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.gui.widgets.containers.WHorizontalList;
import xyz.mathax.client.gui.widgets.pressable.WButton;
import xyz.mathax.client.settings.*;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.utils.entity.fakeplayer.FakePlayerManager;

public class FakePlayer extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    public final Setting<String> nameSetting = generalSettings.add(new StringSetting.Builder()
            .name("Name")
            .description("The name of the fake player.")
            .defaultValue("Matejko06")
            .build()
    );

    public final Setting<Boolean> copyInventorySetting = generalSettings.add(new BoolSetting.Builder()
            .name("Copy inventory")
            .description("Copy your inventory to the fake player.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Integer> healthSetting = generalSettings.add(new IntSetting.Builder()
            .name("Health")
            .description("The fake player's default health.")
            .defaultValue(20)
            .min(1)
            .sliderRange(1, 36)
            .build()
    );

    public FakePlayer(Category category) {
        super(category, "Fake Player", "Spawns a client side fake player for testing usages. Doesn't need to be active.");
    }

    @Override
    public WWidget getWidget(Theme theme) {
        WHorizontalList horizontalList = theme.horizontalList();

        WButton spawn = horizontalList.add(theme.button("Spawn")).widget();
        spawn.action = () -> FakePlayerManager.add(nameSetting.get(), healthSetting.get(), copyInventorySetting.get());

        WButton clear = horizontalList.add(theme.button("Clear")).widget();
        clear.action = FakePlayerManager::clear;

        return horizontalList;
    }

    @Override
    public String getInfoString() {
        return String.valueOf(FakePlayerManager.count());
    }
}
