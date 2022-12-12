package xyz.mathax.mathaxclient.mixin;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TextVisitFactory.class)
public abstract class TextVisitFactoryMixin {
    /*@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", index = 0)
    private static String adjustText(String text) {
        if (Modules.get() != null) {
            return Modules.get().get(NameProtect.class).replaceName(text);
        } else {
            return text;
        }
    }*/
}
