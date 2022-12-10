package mathax.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public class PlayerArmorSlotMixin {
    /*@Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    private void onGetMaxItemCount(CallbackInfoReturnable<Integer> infoReturnable) {
        if (Modules.get().get(InventoryTweaks.class).armorStorage()) {
            infoReturnable.setReturnValue(64);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void onCanInsert(ItemStack stack, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (Modules.get().get(InventoryTweaks.class).armorStorage()) {
            infoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "canTakeItems", at = @At("HEAD"), cancellable = true)
    private void onCanTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (Modules.get().get(InventoryTweaks.class).armorStorage()) {
            infoReturnable.setReturnValue(true);
        }
    }*/
}
