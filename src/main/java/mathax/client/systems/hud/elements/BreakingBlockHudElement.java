package mathax.client.systems.hud.elements;

import mathax.client.mixin.ClientPlayerInteractionManagerAccessor;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;

public class BreakingBlockHudElement extends DoubleTextHudElement {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Boolean> hideSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Hide")
            .description("Hide while not breaking any block.")
            .defaultValue(true)
            .build()
    );

    public BreakingBlockHudElement(Hud hud) {
        super(hud, "Breaking Progress", "Displays percentage of the block you are breaking.");
    }

    @Override
    protected String getLeft() {
        return "Breaking Progress: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            visible = true;
            return "0%";
        }

        float breakingProgress = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress();
        if (hideSetting.get()) {
            visible = breakingProgress > 0;
        }

        return String.format("%.0f%%", breakingProgress * 100);
    }
}