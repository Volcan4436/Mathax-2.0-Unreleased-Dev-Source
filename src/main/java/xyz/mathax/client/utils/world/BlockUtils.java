package xyz.mathax.client.utils.world;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.eventbus.EventPriority;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.init.PreInit;
import xyz.mathax.client.mixininterface.IVec3d;
import xyz.mathax.client.utils.player.FindItemResult;
import xyz.mathax.client.utils.player.InvUtils;
import xyz.mathax.client.utils.player.Rotations;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class BlockUtils {
    private static final Vec3d hitPos = new Vec3d(0, 0, 0);

    public static boolean breaking;
    private static boolean breakingThisTick;

    @PreInit
    public static void init() {
        MatHax.EVENT_BUS.subscribe(BlockUtils.class);
    }

    // Placing

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, int rotationPriority) {
        return place(blockPos, findItemResult, rotationPriority, true);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean rotate, int rotationPriority) {
        return place(blockPos, findItemResult, rotate, rotationPriority, true);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean rotate, int rotationPriority, boolean checkEntities) {
        return place(blockPos, findItemResult, rotate, rotationPriority, true, checkEntities);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, int rotationPriority, boolean checkEntities) {
        return place(blockPos, findItemResult, true, rotationPriority, true, checkEntities);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean rotate, int rotationPriority, boolean swingHand, boolean checkEntities) {
        return place(blockPos, findItemResult, rotate, rotationPriority, swingHand, checkEntities, true);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean rotate, int rotationPriority, boolean swingHand, boolean checkEntities, boolean swapBack) {
        if (findItemResult.isOffhand()) {
            return place(blockPos, Hand.OFF_HAND, MatHax.mc.player.getInventory().selectedSlot, rotate, rotationPriority, swingHand, checkEntities, swapBack);
        } else if (findItemResult.isHotbar()) {
            return place(blockPos, Hand.MAIN_HAND, findItemResult.slot(), rotate, rotationPriority, swingHand, checkEntities, swapBack);
        }

        return false;
    }

    public static boolean place(BlockPos blockPos, Hand hand, int slot, boolean rotate, int rotationPriority, boolean swingHand, boolean checkEntities, boolean swapBack) {
        if (slot < 0 || slot > 8) {
            return false;
        }

        if (!canPlace(blockPos, checkEntities)) {
            return false;
        }

        ((IVec3d) hitPos).set(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        BlockPos neighbour;
        Direction side = getPlaceSide(blockPos);

        if (side == null) {
            side = Direction.UP;
            neighbour = blockPos;
        } else {
            neighbour = blockPos.offset(side.getOpposite());
            hitPos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5);
        }

        Direction s = side;

        if (rotate) {
            Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos), rotationPriority, () -> {
                InvUtils.swap(slot, swapBack);

                place(new BlockHitResult(hitPos, s, neighbour, false), hand, swingHand);

                if (swapBack) {
                    InvUtils.swapBack();
                }
            });
        } else {
            InvUtils.swap(slot, swapBack);

            place(new BlockHitResult(hitPos, s, neighbour, false), hand, swingHand);

            if (swapBack) {
                InvUtils.swapBack();
            }
        }


        return true;
    }

    private static void place(BlockHitResult blockHitResult, Hand hand, boolean swing) {
        boolean wasSneaking = MatHax.mc.player.input.sneaking;
        MatHax.mc.player.input.sneaking = false;

        ActionResult result = MatHax.mc.interactionManager.interactBlock(MatHax.mc.player, hand, blockHitResult);

        if (result.shouldSwingHand()) {
            if (swing) {
                MatHax.mc.player.swingHand(hand);
            } else {
                MatHax.mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
            }
        }

        MatHax.mc.player.input.sneaking = wasSneaking;
    }

    public static boolean canPlace(BlockPos blockPos, boolean checkEntities) {
        if (blockPos == null) {
            return false;
        }

        // Check y level
        if (!World.isValid(blockPos)) {
            return false;
        }

        // Check if current block is replaceable
        if (!MatHax.mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }

        // Check if intersects entities
        return !checkEntities || MatHax.mc.world.canPlace(MatHax.mc.world.getBlockState(blockPos), blockPos, ShapeContext.absent());
    }

    public static boolean canPlace(BlockPos blockPos) {
        return canPlace(blockPos, true);
    }

    public static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = MatHax.mc.world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || isClickable(state.getBlock())) {
                continue;
            }

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) {
                continue;
            }

            return side2;
        }

        return null;
    }

    // Breaking

    @EventHandler(priority = EventPriority.HIGHEST + 100)
    private static void onTickPre(TickEvent.Pre event) {
        breakingThisTick = false;
    }

    @EventHandler(priority = EventPriority.LOWEST - 100)
    private static void onTickPost(TickEvent.Post event) {
        if (!breakingThisTick && breaking) {
            breaking = false;

            if (MatHax.mc.interactionManager != null) {
                MatHax.mc.interactionManager.cancelBlockBreaking();
            }
        }
    }

    /** Needs to be used in {@link TickEvent.Pre} */
    public static boolean breakBlock(BlockPos blockPos, boolean swing) {
        if (!canBreak(blockPos, MatHax.mc.world.getBlockState(blockPos))) {
            return false;
        }

        // Creating new instance of block pos because minecraft assigns the parameter to a field and we don't want it to change when it has been stored in a field somewhere
        BlockPos pos = blockPos instanceof BlockPos.Mutable ? new BlockPos(blockPos) : blockPos;

        if (MatHax.mc.interactionManager.isBreakingBlock()) {
            MatHax.mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
        } else {
            MatHax.mc.interactionManager.attackBlock(pos, Direction.UP);
        }

        if (swing) {
            MatHax.mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            MatHax.mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }

        breaking = true;
        breakingThisTick = true;

        return true;
    }

    public static boolean canBreak(BlockPos blockPos, BlockState state) {
        if (!MatHax.mc.player.isCreative() && state.getHardness(MatHax.mc.world, blockPos) < 0) {
            return false;
        }

        return state.getOutlineShape(MatHax.mc.world, blockPos) != VoxelShapes.empty();
    }

    public static boolean canBreak(BlockPos blockPos) {
        return canBreak(blockPos, MatHax.mc.world.getBlockState(blockPos));
    }

    public static boolean canInstaBreak(BlockPos blockPos, BlockState state) {
        return MatHax.mc.player.isCreative() || state.calcBlockBreakingDelta(MatHax.mc.player, MatHax.mc.world, blockPos) >= 1;
    }

    public static boolean canInstaBreak(BlockPos blockPos) {
        return canInstaBreak(blockPos, MatHax.mc.world.getBlockState(blockPos));
    }

    // Other

    public static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock || block instanceof AnvilBlock || block instanceof ButtonBlock || block instanceof AbstractPressurePlateBlock || block instanceof BlockWithEntity || block instanceof BedBlock || block instanceof FenceGateBlock || block instanceof DoorBlock || block instanceof NoteBlock || block instanceof TrapdoorBlock;
    }

    public static boolean topSurface(BlockState blockState) {
        if (blockState.getBlock() instanceof SlabBlock && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
            return true;
        } else {
            return blockState.getBlock() instanceof StairsBlock && blockState.get(StairsBlock.HALF) == BlockHalf.TOP;
        }
    }

    private static final ThreadLocal<BlockPos.Mutable> EXPOSED_POS = ThreadLocal.withInitial(BlockPos.Mutable::new);

    public static boolean isExposed(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!MatHax.mc.world.getBlockState(EXPOSED_POS.get().set(blockPos, direction)).isOpaque()) {
                return true;
            }
        }

        return false;
    }

    public static BlockPos roundBlockPos(Vec3d vec) {
        return new BlockPos(vec.x, Math.round(vec.y), vec.z);
    }
}
