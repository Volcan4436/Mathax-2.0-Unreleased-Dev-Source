package xyz.mathax.client.mixin;

import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.modules.render.Fullbright;
import xyz.mathax.client.systems.modules.render.Xray;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (Modules.get().get(Fullbright.class).getGamma() || Modules.get().isEnabled(Xray.class)) {
            args.set(2, 0xFFFFFFFF);
        }
    }

    /*@Inject(method = "getDarknessFactor(F)F", at = @At("HEAD"), cancellable = true)
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> infoReturnable) {
        if (Modules.get().get(NoRender.class).noDarkness()) {
            infoReturnable.setReturnValue(0.0f);
        }
    }*/
}
