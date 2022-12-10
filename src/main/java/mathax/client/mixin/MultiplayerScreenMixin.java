package mathax.client.mixin;

import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.Themes;
import mathax.client.utils.network.LastServerInfo;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        addDrawableChild(new ButtonWidget(this.width - 75 - 3, 3, 75, 20, Text.literal("Accounts"), button -> {
            client.setScreen(Themes.getTheme().accountsScreen());
        }));

        addDrawableChild(new ButtonWidget(this.width - 75 - 3 - 75 - 2, 3, 75, 20, Text.literal("Proxies"), button -> {
            client.setScreen(Themes.getTheme().proxiesScreen());
        }));

        if (LastServerInfo.getLastServer() != null) {
            addDrawableChild(new ButtonWidget(width / 2 - 154, 10, 100, 20, Text.literal("Last Server"), button -> {
                LastServerInfo.joinLastServer((MultiplayerScreen) (Object) this);
            }));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo info) {
        Theme theme = Themes.getTheme();
        boolean shadow = theme.fontShadow();
        theme.textRenderer().setScale(0.5);
        double loggedWidth = theme.textRenderer().render("Logged in as ", 2, 2, Color.LIGHT_GRAY, shadow);
        theme.textRenderer().render(client.getSession().getProfile().getName(), loggedWidth, 2, Color.WHITE, shadow);
    }

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/network/ServerInfo;)V")
    private void onConnect(ServerInfo serverInfo, CallbackInfo info) {
        LastServerInfo.setLastServer(serverInfo);
    }
}