package xyz.mathax.client.systems.modules.misc;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.settings.EnumSetting;
import xyz.mathax.client.settings.IntSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.utils.Utils;

public class AutoClicker extends Module {
    private int rightClickTimer, leftClickTimer;

    private final SettingGroup leftSettings = settings.createGroup("Left");
    private final SettingGroup rightSettings = settings.createGroup("Right");

    // Left

    private final Setting<Mode> leftClickModeSetting = leftSettings.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("The method of clicking for clicks.")
            .defaultValue(Mode.Press)
            .build()
    );

    private final Setting<Integer> leftClickDelaySetting = leftSettings.add(new IntSetting.Builder()
            .name("Delay")
            .description("The amount of delay between clicks in ticks.")
            .defaultValue(2)
            .min(0)
            .sliderRange(0, 60)
            .visible(() -> leftClickModeSetting.get() != Mode.Disabled)
            .build()
    );

    // Right

    private final Setting<Mode> rightClickModeSetting = rightSettings.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("The method of clicking for clicks.")
            .defaultValue(Mode.Press)
            .build()
    );

    private final Setting<Integer> rightClickDelaySetting = rightSettings.add(new IntSetting.Builder()
            .name("Delay")
            .description("The amount of delay between clicks in ticks.")
            .defaultValue(2)
            .min(0)
            .sliderRange(0, 60)
            .visible(() -> rightClickModeSetting.get() != Mode.Disabled)
            .build()
    );

    public AutoClicker(Category category) {
        super(category, "Auto Clicker", "Automatically clicks.");
    }

    @Override
    public void onEnable() {
        rightClickTimer = 0;
        leftClickTimer = 0;

        mc.options.attackKey.setPressed(false);
        mc.options.useKey.setPressed(false);
    }

    @Override
    public void onDisable() {
        mc.options.attackKey.setPressed(false);
        mc.options.useKey.setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (leftClickModeSetting.get()) {
            case Disabled -> {}
            case Hold -> mc.options.attackKey.setPressed(true);
            case Press -> {
                leftClickTimer++;
                if (leftClickTimer > leftClickDelaySetting.get()) {
                    Utils.leftClick();
                    leftClickTimer = 0;
                }
            }
        }

        switch (rightClickModeSetting.get()) {
            case Disabled -> {}
            case Hold -> mc.options.useKey.setPressed(true);
            case Press -> {
                rightClickTimer++;
                if (rightClickTimer > rightClickDelaySetting.get()) {
                    Utils.rightClick();
                    rightClickTimer = 0;
                }
            }
        }
    }

    public enum Mode {
        Disabled("Disabled"),
        Hold("Hold"),
        Press("Press");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}