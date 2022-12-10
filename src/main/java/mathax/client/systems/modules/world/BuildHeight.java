package mathax.client.systems.modules.world;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.packets.PacketEvent;
import mathax.client.mixin.BlockHitResultAccessor;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Direction;

public class BuildHeight extends Module {
    public BuildHeight(Category category) {
        super(category, "Build Height", "Allows you to interact with objects at the build limit.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerInteractBlockC2SPacket packet)) {
            return;
        }

        if (packet.getBlockHitResult().getPos().y >= 255 && packet.getBlockHitResult().getSide() == Direction.UP) {
            ((BlockHitResultAccessor) packet.getBlockHitResult()).setSide(Direction.DOWN);
        }
    }
}