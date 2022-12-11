package mathax.client.mixin;

import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.systems.Systems;
import mathax.client.systems.proxies.Proxies;
import mathax.client.systems.proxies.Proxy;
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
    private final OverlayRenderer RENDERER = new OverlayRenderer();

    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Theme theme = Systems.get(Themes.class).getTheme();

        addDrawableChild(new ButtonWidget.Builder(Text.literal("Accounts"), button -> client.setScreen(theme.accountsScreen())).position(this.width - 75 - 3, 3).size(75, 20).build());

        addDrawableChild(new ButtonWidget.Builder(Text.literal("Proxies"), button -> client.setScreen(theme.proxiesScreen())).position(this.width - 75 - 3 - 75 - 2, 3).size(75, 20).build());

        if (LastServerInfo.getLastServer() != null) {
            addDrawableChild(new ButtonWidget.Builder(Text.literal("Accounts"), button -> client.setScreen(theme.accountsScreen())).position(this.width / 2 - 154, 10).size(100, 20).build());
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo info) {
        float x = 2;
        float y = 2;

        String space = " ";
        int spaceLength = textRenderer.getWidth(space);

        String loggedInAs = "Logged in as";
        int loggedInAsLength = textRenderer.getWidth(loggedInAs);
        String loggedName = /*Modules.get().get(NameProtect.class).getName(*/client.getSession().getUsername()/*)*/;

        drawStringWithShadow(matrixStack, textRenderer, loggedInAs, 2, (int) y, Color.fromRGBA(Color.GRAY));
        drawStringWithShadow(matrixStack, textRenderer, space, loggedInAsLength + 2, (int) y, Color.fromRGBA(Color.GRAY));
        drawStringWithShadow(matrixStack, textRenderer, loggedName, loggedInAsLength + spaceLength + 2, (int) y, Color.fromRGBA(Color.WHITE));

        y += textRenderer.fontHeight + 2;

        Proxy proxy = Proxies.get().getEnabled();
        String proxyLeft = proxy != null ? "Using proxy " : "Not using a proxy";
        drawStringWithShadow(matrixStack, textRenderer, proxyLeft, (int)x, (int) y, Color.fromRGBA(Color.GRAY));

        String proxyRight = proxy != null ? (proxy.nameSetting.get() != null && !proxy.nameSetting.get().isEmpty() ? "(" + proxy.nameSetting.get() + ") " : "") + proxy.addressSetting.get() + ":" + proxy.portSetting.get() : null;
        if (proxyRight != null) {
            drawStringWithShadow(matrixStack, textRenderer, proxyRight, (int)x + textRenderer.getWidth(proxyLeft), (int) y, Color.fromRGBA(Color.WHITE));
        }

        // NEW CODE

        /*RENDERER.begin(Systems.get(Themes.class).getTheme().scale(0.5), 0, true);

        RENDERER.text("TEST", 2, 2, Color.WHITE);

        RENDERER.end();*/
    }

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/network/ServerInfo;)V")
    private void onConnect(ServerInfo serverInfo, CallbackInfo info) {
        LastServerInfo.setLastServer(serverInfo);
    }
}