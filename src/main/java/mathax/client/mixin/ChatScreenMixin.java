package mathax.client.mixin;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mathax.client.MatHax;
import mathax.client.events.game.SendMessageEvent;
import mathax.client.systems.commands.Commands;
import mathax.client.systems.config.Config;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.NoRender;
import mathax.client.utils.render.color.Color;
import mathax.client.utils.text.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.encryption.Signer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget chatField;

    @Shadow
    public abstract boolean sendMessage(String chatText, boolean addToHistory);

    @Unique
    private boolean ignoreChatMessage;

    public ChatScreenMixin(Text title) {
        super(title);
    }

    /*@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V", shift = At.Shift.AFTER))
    private void onInit(CallbackInfo info) {
        if (Modules.get().get(BetterChat.class).isInfiniteChatBox()) {
            chatField.setMaxLength(Integer.MAX_VALUE);
        }
    }*/

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(Config.get().prefixSetting.get()) && !message.startsWith("/") && !message.startsWith(BaritoneAPI.getSettings().prefix.value)) {
            SendMessageEvent event = MatHax.EVENT_BUS.post(SendMessageEvent.get(message));
            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendMessage(event.message, addToHistory);
                ignoreChatMessage = false;
            }

            infoReturnable.setReturnValue(true);

            return;
        }

        if (message.startsWith(Config.get().prefixSetting.get())) {
            try {
                Commands.get().dispatch(message.substring(Config.get().prefixSetting.get().length()));
            } catch (CommandSyntaxException exception) {
                ChatUtils.error(exception.getMessage());
            }

            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            infoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (chatField.getText().startsWith(Config.get().prefixSetting.get())) {
            renderBoxOutline(matrixStack, Color.MATHAX);
        } else if (chatField.getText().startsWith(BaritoneAPI.getSettings().prefix.value)) {
            renderBoxOutline(matrixStack, Color.MAGENTA);
        }
    }

    private void renderBoxOutline(MatrixStack matrixStack, Color color) {
        //TODO: Renders in the middle of the screen for some fucking reason.
        /*Renderer2D.COLOR.begin();
        Renderer2D.COLOR.boxLines(chatField.x - 1, chatField.y - 1, chatField.getWidth() + 1, chatField.getHeight() + 1, color);
        Renderer2D.COLOR.render(matrixStack);*/
    }
}
