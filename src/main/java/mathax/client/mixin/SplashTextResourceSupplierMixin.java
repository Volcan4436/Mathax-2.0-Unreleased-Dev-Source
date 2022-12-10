package mathax.client.mixin;

import mathax.client.systems.config.Config;
import mathax.client.utils.network.versions.Versions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    private boolean override = true;

    private final Random random = new Random();

    private final List<String> mathaxSplashes = getMatHaxSplashes();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<String> infoReturnable) {
        if (Config.get() == null || !Config.get().titleScreenCreditsAndSplashesSetting.get()) {
            return;
        }

        if (override) {
            infoReturnable.setReturnValue(mathaxSplashes.get(random.nextInt(mathaxSplashes.size())));
        }

        override = !override;
    }

    private static List<String> getMatHaxSplashes() {
        return Arrays.asList(
                // SPLASHES
                Formatting.RED + "MatHax on top!",
                Formatting.GRAY + "Matejko06" + Formatting.RED + " based god",
                Formatting.RED + "MatHaxClient.xyz",
                Formatting.RED + "MatHaxClient.xyz/Discord",
                Formatting.RED + Versions.getStylized(),
                Formatting.RED + Versions.getMinecraft(),

                // MEME SPLASHES
                Formatting.YELLOW + "cope",
                Formatting.YELLOW + "I <3 nns",
                Formatting.YELLOW + "haha 69",
                Formatting.YELLOW + "420 XDDDDDD",
                Formatting.YELLOW + "ayy",
                Formatting.YELLOW + "too ez",
                Formatting.YELLOW + "owned",
                Formatting.YELLOW + "your mom :joy:",
                Formatting.YELLOW + "BOOM BOOM BOOM!",
                Formatting.YELLOW + "I <3 forks",
                Formatting.YELLOW + "based",
                Formatting.YELLOW + "Pog",
                Formatting.YELLOW + "Big Rat on top!",
                Formatting.YELLOW + "bigrat.monster",

                // PERSONALIZED
                Formatting.YELLOW + "You're cool, " + Formatting.GRAY + MinecraftClient.getInstance().getSession().getUsername(),
                Formatting.YELLOW + "Owning with " + Formatting.GRAY + MinecraftClient.getInstance().getSession().getUsername(),
                Formatting.YELLOW + "Who is " + Formatting.GRAY + MinecraftClient.getInstance().getSession().getUsername() + Formatting.YELLOW + "?"
        );
    }

}
