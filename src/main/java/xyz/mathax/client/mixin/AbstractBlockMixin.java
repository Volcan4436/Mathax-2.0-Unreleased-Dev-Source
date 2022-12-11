package xyz.mathax.client.mixin;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.events.world.AmbientOcclusionEvent;
import xyz.mathax.client.events.world.CollisionShapeEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
    private void onGetAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> infoReturnable) {
        AmbientOcclusionEvent event = MatHax.EVENT_BUS.post(AmbientOcclusionEvent.get());
        if (event.lightLevel != -1) {
            infoReturnable.setReturnValue(event.lightLevel);
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> infoReturnable) {
        if (!(state.getFluidState().isEmpty())) {
            CollisionShapeEvent event = MatHax.EVENT_BUS.post(CollisionShapeEvent.get(state.getFluidState().getBlockState(), pos, CollisionShapeEvent.CollisionType.FLUID));
            if (event.shape != null) {
                infoReturnable.setReturnValue(event.shape);
            }
        } else {
            CollisionShapeEvent event = MatHax.EVENT_BUS.post(CollisionShapeEvent.get(state, pos, CollisionShapeEvent.CollisionType.BLOCK));
            if (event.shape != null) {
                infoReturnable.setReturnValue(event.shape);
            }
        }
    }
}
