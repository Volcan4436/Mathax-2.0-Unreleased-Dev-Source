package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.mixin.ClientPlayerInteractionManagerAccessor;
import xyz.mathax.client.settings.BoolSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.hud.DoubleTextHudElement;
import xyz.mathax.client.systems.hud.Hud;

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