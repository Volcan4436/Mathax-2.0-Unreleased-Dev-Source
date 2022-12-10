package mathax.client.systems.hud.elements;

import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.HudElement;
import mathax.client.utils.player.InvUtils;
import mathax.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TotemHudElement extends HudElement {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Double> scaleSetting = generalSettings.add(new DoubleSetting.Builder()
        .name("Scale")
        .description("The scale.")
        .defaultValue(2)
        .min(1)
        .sliderRange(1, 5)
        .build()
    );

    public TotemHudElement(Hud hud) {
        super(hud, "Totems", "Displays the amount of totems in your inventory.");
    }

    @Override
    public void update(OverlayRenderer renderer) {
        box.setSize(16 * scaleSetting.get(), 16 * scaleSetting.get());
    }


    @Override
    public void render(OverlayRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) {
            RenderUtils.drawItem(Items.TOTEM_OF_UNDYING.getDefaultStack(), (int) x, (int) y, scaleSetting.get(), true);
        } else if (InvUtils.find(Items.TOTEM_OF_UNDYING).count() > 0) {
            RenderUtils.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING, InvUtils.find(Items.TOTEM_OF_UNDYING).count()), (int) x, (int) y, scaleSetting.get(), true);
        }
    }
}
