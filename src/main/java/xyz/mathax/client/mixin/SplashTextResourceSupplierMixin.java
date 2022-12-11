package xyz.mathax.client.mixin;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.systems.config.Config;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    private boolean override = true;

    private final Random random = new Random();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<String> infoReturnable) {
        if (Config.get() == null || !Config.get().titleScreenCreditsAndSplashesSetting.get()) {
            return;
        }

        if (override) {
            List<String> splashes = MatHax.getSplashes();
            infoReturnable.setReturnValue(splashes.get(random.nextInt(splashes.size())));
        }

        override = !override;
    }
}
