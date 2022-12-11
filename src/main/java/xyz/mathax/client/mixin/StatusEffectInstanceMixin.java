package xyz.mathax.client.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {
    /*@Shadow
    private int duration;

    @Inject(method = "updateDuration", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfoReturnable<Integer> infoReturnable) {
        if (!Utils.canUpdate()) {
            return;
        }

        if (Modules.get().get(PotionSaver.class).shouldFreeze(((StatusEffectInstance) (Object) this).getEffectType())) {
            infoReturnable.setReturnValue(duration);
        }
    }*/
}
