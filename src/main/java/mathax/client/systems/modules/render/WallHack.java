package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.ChunkOcclusionEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import net.minecraft.block.Block;

import java.util.List;

public class WallHack extends Module {
    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    public final Setting<Integer> opacitySetting = generalSettings.add(new IntSetting.Builder()
        .name("Opacity")
        .description("The opacity for rendered blocks.")
        .defaultValue(0)
        .range(0, 255)
        .sliderRange(0, 255)
        .onChanged(onChanged -> {
            if (isEnabled()) {
                mc.worldRenderer.reload();
            }
        })
        .build()
    );

    public final Setting<List<Block>> blocksSetting = generalSettings.add(new BlockListSetting.Builder()
        .name("Blocks")
        .description("What blocks should be targeted for Wall Hack.")
        .defaultValue()
        .onChanged(onChanged -> {
            if (isEnabled()) {
                mc.worldRenderer.reload();
            }
        })
        .build()
    );

    public final Setting<Boolean> occludeChunksSetting = generalSettings.add(new BoolSetting.Builder()
        .name("Occlude chunks")
        .description("Whether caves should occlude underground (may look wonky when on).")
        .defaultValue(false)
        .build()
    );

    public WallHack(Category category) {
        super(category, "Wall Hack", "Makes blocks translucent.");
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
    }

    @EventHandler
    private void onChunkOcclusion(ChunkOcclusionEvent event) {
        if (!occludeChunksSetting.get()) {
            event.cancel();
        }
    }
}
