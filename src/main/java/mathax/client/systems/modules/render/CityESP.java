package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.Render3DEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.renderer.ShapeMode;
import mathax.client.settings.ColorSetting;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.utils.entity.EntityUtils;
import mathax.client.utils.entity.SortPriority;
import mathax.client.utils.entity.TargetUtils;
import mathax.client.utils.render.color.Color;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CityESP extends Module {
    private BlockPos target;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<ShapeMode> shapeModeSetting = generalSettings.add(new EnumSetting.Builder<ShapeMode>()
            .name("Shape mode")
            .description("Determines how the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColorSetting = generalSettings.add(new ColorSetting.Builder()
            .name("Side color")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(Color.MATHAX, 75))
            .build()
    );

    private final Setting<SettingColor> lineColorSetting = generalSettings.add(new ColorSetting.Builder()
            .name("Line color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(Color.MATHAX))
            .build()
    );

    public CityESP(Category category) {
        super(category, "City ESP", "Displays blocks that can be broken in order to city another player.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        PlayerEntity targetEntity = TargetUtils.getPlayerTarget(mc.interactionManager.getReachDistance() + 2, SortPriority.Lowest_Distance);
        if (TargetUtils.isBadTarget(targetEntity, mc.interactionManager.getReachDistance() + 2)) {
            target = null;
        } else {
            target = EntityUtils.getCityBlock(targetEntity);
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (target == null) {
            return;
        }

        event.renderer.box(target, sideColorSetting.get(), lineColorSetting.get(), shapeModeSetting.get(), 0);
    }
}
