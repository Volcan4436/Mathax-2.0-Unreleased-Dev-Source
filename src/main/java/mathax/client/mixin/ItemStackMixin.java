package mathax.client.mixin;

import mathax.client.MatHax;
import mathax.client.events.entity.player.FinishUsingItemEvent;
import mathax.client.events.entity.player.StoppedUsingItemEvent;
import mathax.client.events.game.ItemStackTooltipEvent;
import mathax.client.events.game.SectionVisibleEvent;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.BetterTooltips;
import mathax.client.utils.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static mathax.client.MatHax.mc;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"), cancellable = true)
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
        if (Utils.canUpdate()) {
            ItemStackTooltipEvent event = MatHax.EVENT_BUS.post(ItemStackTooltipEvent.get((ItemStack) (Object) this, info.getReturnValue()));
            info.setReturnValue(event.list);
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> infoReturnable) {
        if (user == mc.player) {
            MatHax.EVENT_BUS.post(FinishUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
        if (user == mc.player) {
            MatHax.EVENT_BUS.post(StoppedUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "isSectionVisible", at = @At("RETURN"), cancellable = true)
    private static void onSectionVisible(int flags, ItemStack.TooltipSection tooltipSection, CallbackInfoReturnable<Boolean> info) {
        SectionVisibleEvent event = MatHax.EVENT_BUS.post(SectionVisibleEvent.get(tooltipSection, info.getReturnValueZ()));
        info.setReturnValue(event.visible);
    }
}
