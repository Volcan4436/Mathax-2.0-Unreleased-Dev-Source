package mathax.client.systems.modules.render;

import mathax.client.settings.ColorSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.utils.render.color.Color;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.util.math.Vec3d;

public class Confetti extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<SettingColor> firstColor = generalSettings.add(new ColorSetting.Builder()
            .name("First color")
            .description("The first color.")
            .defaultValue(new SettingColor(Color.MATHAX))
            .build()
    );

    private final Setting<SettingColor> secondColor = generalSettings.add(new ColorSetting.Builder()
            .name("Second color")
            .description("The second color.")
            .defaultValue(new SettingColor(Color.MATHAX_BACKGROUND))
            .build()
    );

    public Confetti(Category category) {
        super(category, "Confetti", "Changes the color of the totem pop particles.");
    }

    public Vec3d getFirstColor() {
        return getDoubleVectorColor(firstColor);
    }

    public Vec3d getSecondColor() {
        return getDoubleVectorColor(secondColor);
    }

    public Vec3d getDoubleVectorColor(Setting<SettingColor> colorSetting) {
        return new Vec3d(colorSetting.get().r / 255.0, colorSetting.get().g / 255.0, colorSetting.get().b / 255.0);
    }
}