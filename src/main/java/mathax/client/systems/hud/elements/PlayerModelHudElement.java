package mathax.client.systems.hud.elements;

import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.renderer.Renderer2D;
import mathax.client.settings.*;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.HudElement;
import mathax.client.utils.misc.FakeClientPlayer;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerModelHudElement extends HudElement {
    private final SettingGroup generalSettings = settings.createGroup("General");
    private final SettingGroup headRotationSettings = settings.createGroup("Head Rotation");
    private final SettingGroup backgroundSettings = settings.createGroup("Background");

    // General

    private final Setting<Double> scaleSetting = generalSettings.add(new DoubleSetting.Builder()
        .name("Scale")
        .description("The scale.")
        .defaultValue(2)
        .min(1)
        .sliderRange(1, 5)
        .build()
    );

    // Head Rotation

    private final Setting<Boolean> copyYawSetting = headRotationSettings.add(new BoolSetting.Builder()
        .name("Copy yaw")
        .description("Make the player model's yaw equal to yours.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> customYawSetting = headRotationSettings.add(new IntSetting.Builder()
            .name("Custom yaw")
            .description("Custom yaw for when copy yaw is off.")
            .defaultValue(0)
            .range(-180, 180)
            .sliderRange(-180, 180)
            .visible(() -> !copyYawSetting.get())
            .build()
    );

    private final Setting<Boolean> copyPitchSetting = headRotationSettings.add(new BoolSetting.Builder()
        .name("Copy pitch")
        .description("Make the player model's pitch equal to yours.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> customPitchSetting = headRotationSettings.add(new IntSetting.Builder()
        .name("Custom pitch")
        .description("Custom pitch for when copy pitch is off.")
        .defaultValue(0)
        .range(-90, 90)
        .sliderRange(-90, 90)
        .visible(() -> !copyPitchSetting.get())
        .build()
    );

    // Background

    private final Setting<Boolean> backgroundSetting = backgroundSettings.add(new BoolSetting.Builder()
        .name("Background")
        .description("Displays a background behind the player model.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> backgroundColorSetting = backgroundSettings.add(new ColorSetting.Builder()
        .name("Background color")
        .description("Color of background.")
        .defaultValue(new SettingColor(0, 0, 0, 64))
        .build()
    );

    public PlayerModelHudElement(Hud hud) {
        super(hud, "Player Model", "Displays a model of your player.");
    }

    @Override
    public void update(OverlayRenderer renderer) {
        box.setSize(50 * scaleSetting.get(), 75 * scaleSetting.get());
    }

    @Override
    public void render(OverlayRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (backgroundSetting.get()) {
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(x, y, box.width, box.height, backgroundColorSetting.get());
            Renderer2D.COLOR.render(null);
        }

        PlayerEntity player = mc.player;
        if (isInEditor()) {
            player = FakeClientPlayer.getPlayer();
        }

        float yaw = copyYawSetting.get() ? MathHelper.wrapDegrees(player.prevYaw + (player.getYaw() - player.prevYaw) * mc.getTickDelta()) : (float) customYawSetting.get();
        float pitch = copyPitchSetting.get() ? player.getPitch() : (float) customPitchSetting.get();

        InventoryScreen.drawEntity((int) (x + (25 * scaleSetting.get())), (int) (y + (66 * scaleSetting.get())), (int) (30 * scaleSetting.get()), -yaw, -pitch, player);
    }
}
