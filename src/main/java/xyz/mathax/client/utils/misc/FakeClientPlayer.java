package xyz.mathax.client.utils.misc;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.game.GameJoinedEvent;
import xyz.mathax.client.init.PreInit;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.world.Difficulty;

public class FakeClientPlayer {
    private static ClientWorld world;
    private static PlayerEntity player;
    private static PlayerListEntry playerListEntry;

    private static String lastId;
    private static boolean needsNewEntry;

    @PreInit
    public static void init() {
        MatHax.EVENT_BUS.subscribe(FakeClientPlayer.class);
    }

    @EventHandler
    private static void onGameJoined(GameJoinedEvent event) {}

    public static PlayerEntity getPlayer() {
        String id = MatHax.mc.getSession().getUuid();

        if (player == null || (!id.equals(lastId))) {
            if (world == null) {
                world = new ClientWorld(new ClientPlayNetworkHandler(MatHax.mc, null, new ClientConnection(NetworkSide.CLIENTBOUND), MatHax.mc.getCurrentServerEntry(), MatHax.mc.getSession().getProfile(), null), new ClientWorld.Properties(Difficulty.NORMAL, false, false), world.getRegistryKey(), world.getDimensionEntry(), 1, 1, MatHax.mc::getProfiler, null, false, 0);
            }

            player = new OtherClientPlayerEntity(world, MatHax.mc.getSession().getProfile());

            lastId = id;
            needsNewEntry = true;
        }

        return player;
    }

    public static PlayerListEntry getPlayerListEntry() {
        if (playerListEntry == null || needsNewEntry) {
            playerListEntry = new PlayerListEntry(MatHax.mc.getSession().getProfile(), false);
            needsNewEntry = false;
        }

        return playerListEntry;
    }
}