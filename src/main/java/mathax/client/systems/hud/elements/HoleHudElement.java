package mathax.client.systems.hud.elements;

import mathax.client.mixin.WorldRendererAccessor;
import mathax.client.renderer.Renderer2D;
import mathax.client.settings.BlockListSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.HudElement;
import mathax.client.systems.hud.HudRenderer;
import mathax.client.utils.Utils;
import mathax.client.utils.render.RenderUtils;
import mathax.client.utils.render.color.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class HoleHudElement extends HudElement {
    private final Color BG_COLOR = new Color(255, 25, 25, 100);
    private final Color OL_COLOR = new Color(255, 25, 25, 255);

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

    public final Setting<List<Block>> safeSetting = generalSettings.add(new BlockListSetting.Builder()
        .name("Safe blocks")
        .description("Which blocks to consider safe.")
        .defaultValue(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK)
        .build()
    );

    public HoleHudElement(Hud hud) {
        super(hud, "Hole", "Displays information about the hole you are standing in.");
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * 3 * scaleSetting.get(), 16 * 3 * scaleSetting.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        Renderer2D.COLOR.begin();

        drawBlock(get(Facing.Left), x, y + 16 * scaleSetting.get()); // Left
        drawBlock(get(Facing.Front), x + 16 * scaleSetting.get(), y); // Front
        drawBlock(get(Facing.Right), x + 32 * scaleSetting.get(), y + 16 * scaleSetting.get()); // Right
        drawBlock(get(Facing.Back), x + 16 * scaleSetting.get(), y + 32 * scaleSetting.get()); // Back

        Renderer2D.COLOR.render(null);
    }

    private Direction get(Facing dir) {
        if (!Utils.canUpdate() || isInEditor()) {
            return Direction.DOWN;
        }

        return Direction.fromRotation(MathHelper.wrapDegrees(mc.player.getYaw() + dir.offset));
    }

    private void drawBlock(Direction dir, double x, double y) {
        Block block = dir == Direction.DOWN ? Blocks.OBSIDIAN : mc.world.getBlockState(mc.player.getBlockPos().offset(dir)).getBlock();
        if (!safeSetting.get().contains(block)) {
            return;
        }

        RenderUtils.drawItem(block.asItem().getDefaultStack(), (int) x, (int) y, scaleSetting.get(), false);

        if (dir == Direction.DOWN) {
            return;
        }

        ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos().values().forEach(info -> {
            if (info.getPos().equals(mc.player.getBlockPos().offset(dir))) {
                renderBreaking(x, y, info.getStage() / 9f);
            }
        });
    }

    private void renderBreaking(double x, double y, double percent) {
        Renderer2D.COLOR.quad(x, y, (16 * percent) * scaleSetting.get(), 16 * scaleSetting.get(), BG_COLOR);
        Renderer2D.COLOR.quad(x, y, 16 * scaleSetting.get(), 1 * scaleSetting.get(), OL_COLOR);
        Renderer2D.COLOR.quad(x, y + 15 * scaleSetting.get(), 16 * scaleSetting.get(), 1 * scaleSetting.get(), OL_COLOR);
        Renderer2D.COLOR.quad(x, y, 1 * scaleSetting.get(), 16 * scaleSetting.get(), OL_COLOR);
        Renderer2D.COLOR.quad(x + 15 * scaleSetting.get(), y, 1 * scaleSetting.get(), 16 * scaleSetting.get(), OL_COLOR);
    }

    private enum Facing {
        Left("Left", -90),
        Right("Right", 90),
        Front("Front", 0),
        Back("Back", 180);

        private final String name;

        private final int offset;

        Facing(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
