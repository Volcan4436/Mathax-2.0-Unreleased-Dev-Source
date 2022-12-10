package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.entity.player.BreakBlockEvent;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import net.minecraft.block.BlockState;

public class AntiGhostBlock extends Module {
    private BlockState lastState;

    public AntiGhostBlock(Category category) {
        super(category, "Anti Ghost Block", "Attempts to prevent ghost blocks arising from breaking blocks quickly. Especially useful with multiconnect.");
    }

    @EventHandler
    public void onBreakBlock(BreakBlockEvent event) {
        if (mc.isInSingleplayer()) {
            return;
        }

        event.setCancelled(true);

        // play the related sounds and particles for the user.
        BlockState blockState = mc.world.getBlockState(event.blockPos);
        blockState.getBlock().onBreak(mc.world, event.blockPos, blockState, mc.player); // this doesn't alter the state of the block in the world
    }
}