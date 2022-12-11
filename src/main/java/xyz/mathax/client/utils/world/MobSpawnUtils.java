package xyz.mathax.client.utils.world;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;

import static xyz.mathax.client.MatHax.mc;

public class MobSpawnUtils {
    public static MobSpawn isValidMobSpawn(BlockPos blockPos, boolean newMobSpawnLightLevel) {
        int spawnLightLimit = newMobSpawnLightLevel ? 0 : 7;
        if (!(mc.world.getBlockState(blockPos).getBlock() instanceof AirBlock) || mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.BEDROCK) {
            return MobSpawn.Never;
        }

        if (!BlockUtils.topSurface(mc.world.getBlockState(blockPos.down()))) {
            if (mc.world.getBlockState(blockPos.down()).getCollisionShape(mc.world, blockPos.down()) != VoxelShapes.fullCube()) {
                return MobSpawn.Never;
            }

            if (mc.world.getBlockState(blockPos.down()).isTranslucent(mc.world, blockPos.down())) {
                return MobSpawn.Never;
            }
        }

        if (mc.world.getLightLevel(blockPos, 0) <= spawnLightLimit) {
            return MobSpawn.Potential;
        } else if (mc.world.getLightLevel(LightType.BLOCK, blockPos) <= spawnLightLimit) {
            return MobSpawn.Always;
        }

        return MobSpawn.Never;
    }
}
