package xyz.mathax.client.mixin;

import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.modules.movement.IgnoreBorder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {
    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    private void canCollide(Entity entity, Box box, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (Modules.get().isEnabled(IgnoreBorder.class)) {
            infoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "contains(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void contains(BlockPos pos, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (Modules.get().isEnabled(IgnoreBorder.class)) {
            infoReturnable.setReturnValue(true);
        }
    }
}