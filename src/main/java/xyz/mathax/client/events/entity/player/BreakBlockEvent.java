package xyz.mathax.client.events.entity.player;

import xyz.mathax.client.events.Cancellable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakBlockEvent extends Cancellable {
    private static final BreakBlockEvent INSTANCE = new BreakBlockEvent();

    public BlockPos blockPos;

    public BlockState getBlockState(World world) {
        return world.getBlockState(blockPos);
    }

    public static BreakBlockEvent get(BlockPos blockPos) {
        INSTANCE.blockPos = blockPos;
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
