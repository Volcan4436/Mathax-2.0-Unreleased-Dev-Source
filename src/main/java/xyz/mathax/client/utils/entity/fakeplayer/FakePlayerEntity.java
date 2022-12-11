package xyz.mathax.client.utils.entity.fakeplayer;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.mathax.client.MatHax;
import xyz.mathax.client.utils.render.PlayerHeadTexture;
import xyz.mathax.client.utils.render.PlayerHeadUtils;

public class FakePlayerEntity extends OtherClientPlayerEntity {
    public boolean doNotPush, hideWhenInsideCamera;

    private @Nullable PlayerHeadTexture headTexture;

    public FakePlayerEntity(PlayerEntity player, String name, float health, boolean copyInv) {
        super(MatHax.mc.world, player.getGameProfile());

        copyPositionAndRotation(player);

        prevYaw = getYaw();
        prevPitch = getPitch();
        headYaw = player.headYaw;
        prevHeadYaw = headYaw;
        bodyYaw = player.bodyYaw;
        prevBodyYaw = bodyYaw;

        Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(player.getAttributes());
        setPose(player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) {
            getInventory().clone(player.getInventory());
        }

        this.headTexture = PlayerHeadUtils.fetchHead(uuid);
    }

    public PlayerHeadTexture getHead() {
        return headTexture != null ? headTexture : PlayerHeadUtils.STEVE_HEAD;
    }

    public void spawn() {
        unsetRemoved();
        MatHax.mc.world.addEntity(getId(), this);
    }

    public void despawn() {
        MatHax.mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        if (playerListEntry == null) {
            playerListEntry = MatHax.mc.getNetworkHandler().getPlayerListEntry(MatHax.mc.player.getUuid());
        }

        return playerListEntry;
    }
}
