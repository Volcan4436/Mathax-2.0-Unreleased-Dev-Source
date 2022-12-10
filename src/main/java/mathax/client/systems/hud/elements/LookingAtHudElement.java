package mathax.client.systems.hud.elements;

import mathax.client.settings.BoolSetting;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;
import mathax.client.utils.misc.Names;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class LookingAtHudElement extends DoubleTextHudElement {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Mode> modeSetting = generalSettings.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("What to target.")
            .defaultValue(Mode.Both)
            .build()
    );

    private final Setting<Boolean> positionSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Position")
            .description("Display crosshair target's position.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> accuratePositionSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Accurate position")
            .description("Display accurate position.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> waterLoggedSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Waterlogged status")
            .description("Display if a block is waterlogged or not")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> uuidSetting = generalSettings.add(new BoolSetting.Builder()
            .name("UUID")
            .description("Display the uuid of the target.")
            .defaultValue(false)
            .build()
    );

    public LookingAtHudElement(Hud hud) {
        super(hud, "Looking At", "Displays what entity or block you are looking at.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return positionSetting.get() ? "Obsidian [0, 0, 0]" : "Obsidian";

        if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK && modeSetting.get() != Mode.Entities) {
            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            String result = Names.get(mc.world.getBlockState(pos).getBlock());

            if (positionSetting.get()) {
                result += String.format(" (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
            }

            if (waterLoggedSetting.get() && mc.world.getFluidState(pos).isIn(FluidTags.WATER)) {
                result += " (water logged)";
            }

            return result;
        } else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY && modeSetting.get() != Mode.Blocks) {
            Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();
            String result;

            if (target instanceof PlayerEntity) {
                result = ((PlayerEntity) target).getGameProfile().getName();
            } else {
                result = target.getName().getString();
            }

            if (positionSetting.get()) {
                result += String.format(" (%d, %d, %d)", target.getBlockX(), target.getBlockY(), target.getBlockZ());
            }

            if (waterLoggedSetting.get() && target.isTouchingWater()) {
                result += " (in water)";
            }

            if (uuidSetting.get()) {
                result += String.format(" (%s)", target.getUuidAsString());
            }

            return result;
        }

        return "Nothing";
    }

    public enum Mode {
        Entities("Entities"),
        Blocks("Blocks"),
        Both("Both");

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
