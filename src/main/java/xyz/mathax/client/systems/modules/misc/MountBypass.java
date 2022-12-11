package xyz.mathax.client.systems.modules.misc;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.packets.PacketEvent;
import xyz.mathax.client.mixininterface.IPlayerInteractEntityC2SPacket;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class MountBypass extends Module {
    private boolean dontCancel;

    public MountBypass(Category category) {
        super(category, "Mount Bypass", "Allows you to bypass the IllegalStacks plugin and put chests on entities.");
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (dontCancel) {
            dontCancel = false;
            return;
        }

        if (event.packet instanceof IPlayerInteractEntityC2SPacket packet) {
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT && packet.getEntity() instanceof AbstractDonkeyEntity) {
                event.cancel();
            }
        }
    }
}