package mathax.client.mixin;

import mathax.client.MatHax;
import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.systems.Systems;
import mathax.client.systems.config.Config;
import mathax.client.systems.proxies.Proxies;
import mathax.client.systems.proxies.Proxy;
import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.Themes;
import mathax.client.utils.network.Executor;
import mathax.client.utils.network.versions.Version;
import mathax.client.utils.network.versions.Versions;
import mathax.client.gui.prompts.OkPrompt;
import mathax.client.gui.prompts.YesNoPrompt;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private final OverlayRenderer RENDERER = new OverlayRenderer();

    private static boolean firstTimeOpen = true;

    public TitleScreenMixin(Text title) {
        super(title);
    }

    // Update checker

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
    private void onRenderTitleScreen(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (firstTimeOpen) {
            firstTimeOpen = false;

            Executor.execute(() -> {
                MatHax.LOG.info("Checking for update...");

                Version version = Versions.get();
                Version latestVersion = Versions.getLatest();
                if (latestVersion == null) {
                    MatHax.LOG.info("Could not check for update!");
                    return;
                }

                if (latestVersion.isHigherThan(version)) {
                    MatHax.LOG.info("There is a new version available, {}! You are using {}!", Versions.getStylized(true), Versions.getStylized());
                    YesNoPrompt.create()
                            .title("New Update")
                            .message("A new version of %s for %s is available.", MatHax.NAME, Versions.getMinecraft())
                            .message("Your version: %s", Versions.getStylized())
                            .message("Latest version: %s", Versions.getStylized(true))
                            .message("Do you want to update?")
                            .onYes(() -> Util.getOperatingSystem().open("https://mathaxclient.xyz/download/"))
                            .onNo(() -> OkPrompt.create()
                                    .title("Are you sure?")
                                    .message("Using old versions of %s is not recommended", MatHax.NAME)
                                    .message("and could report in issues.")
                                    .id("new-update-no")
                                    .onOk(this::close)
                                    .show())
                            .id("new-update")
                            .show();
                } else {
                    MatHax.LOG.info("You are using the latest version, {}!", Versions.getStylized());
                }
            });
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!Config.get().titleScreenCreditsAndSplashesSetting.get()) {
            return;
        }

        RENDERER.begin(Systems.get(Themes.class).getTheme().scale(0.5), 0, true);

        //TODO: Only vanilla text renderer works atm
        RENDERER.text("TEST", 2, 2, Color.WHITE);

        RENDERER.end();

        // OLD CODE
        double y = 2;
        double y2 = y + textRenderer.fontHeight + y;

        String space = " ";
        int spaceLength = textRenderer.getWidth(space);

        String loggedInAs = "Logged in as";
        int loggedInAsLength = textRenderer.getWidth(loggedInAs);
        String loggedName = /*Modules.get().get(NameProtect.class).getName(*/client.getSession().getUsername()/*)*/;
        /*int loggedNameLength = textRenderer.getWidth(loggedName);
        String loggedOpenDeveloper = "[";
        int loggedOpenDeveloperLength = textRenderer.getWidth(loggedOpenDeveloper);
        String loggedDeveloper = "Developer";
        int loggedDeveloperLength = textRenderer.getWidth(loggedDeveloper);
        String loggedCloseDeveloper = "]";*/

        Proxy proxy = Proxies.get().getEnabled();
        String proxyUsing = proxy != null ? "Using proxy" + " " : "Not using a proxy";
        int proxyUsingLength = textRenderer.getWidth(proxyUsing);
        String proxyDetails = proxy != null ? "(" + proxy.nameSetting.get() + ") " + proxy.addressSetting.get() + ":" + proxy.portSetting.get() : null;

        String watermarkName = "MatHax";
        int watermarkNameLength = textRenderer.getWidth(watermarkName);
        String watermarkVersion = Versions.getStylized();
        int watermarkVersionLength = textRenderer.getWidth(watermarkVersion);
        int watermarkFullLength = watermarkNameLength + spaceLength + watermarkVersionLength;

        String authorBy = "By";
        int authorByLength = textRenderer.getWidth(authorBy);
        String authorName = "Matejko06";
        int authorNameLength = textRenderer.getWidth(authorName);
        int authorFullLength = authorByLength + spaceLength + authorNameLength;

        drawStringWithShadow(matrixStack, textRenderer, loggedInAs, 2, (int) y, Color.fromRGBA(Color.LIGHT_GRAY));
        drawStringWithShadow(matrixStack, textRenderer, space, loggedInAsLength + 2, (int) y, Color.fromRGBA(Color.LIGHT_GRAY));
        drawStringWithShadow(matrixStack, textRenderer, loggedName, loggedInAsLength + spaceLength + 2, (int) y, Color.fromRGBA(Color.WHITE));

        /*if (Modules.get() != null /*&& !Modules.get().isEnabled(NameProtect.class)*//* && OnlinePlayers.isPlayerDeveloper(client.getSession().getUuid())) {
            drawStringWithShadow(matrixStack, textRenderer, space, loggedInAsLength + spaceLength + loggedNameLength + 2, (int) y, GRAY);
            drawStringWithShadow(matrixStack, textRenderer, loggedOpenDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + 2, (int) y, GRAY);
            drawStringWithShadow(matrixStack, textRenderer, loggedDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + 2, (int) y, MatHax.INSTANCE.MATHAX_COLOR_INT);
            drawStringWithShadow(matrixStack, textRenderer, loggedCloseDeveloper, loggedInAsLength + spaceLength + loggedNameLength + spaceLength + loggedOpenDeveloperLength + loggedDeveloperLength + 2, (int) y, GRAY);
        }*/

        int watermarkPreviousWidth = 0;
        drawStringWithShadow(matrixStack, textRenderer, watermarkName, width - watermarkFullLength - 2, (int) y, Color.fromRGBA(Color.MATHAX));
        watermarkPreviousWidth += watermarkNameLength;
        drawStringWithShadow(matrixStack, textRenderer, space, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, Color.fromRGBA(Color.WHITE));
        watermarkPreviousWidth += spaceLength;
        drawStringWithShadow(matrixStack, textRenderer, watermarkVersion, width - watermarkFullLength + watermarkPreviousWidth - 2, (int) y, Color.fromRGBA(Color.WHITE));

        int authorPreviousWidth = 0;
        drawStringWithShadow(matrixStack, textRenderer, authorBy, width - authorFullLength - 2, (int) y2, Color.fromRGBA(Color.LIGHT_GRAY));
        authorPreviousWidth += authorByLength;
        drawStringWithShadow(matrixStack, textRenderer, space, width - authorFullLength + authorPreviousWidth - 2, (int) y2, Color.fromRGBA(Color.LIGHT_GRAY));
        authorPreviousWidth += spaceLength;
        drawStringWithShadow(matrixStack, textRenderer, authorName, width - authorFullLength + authorPreviousWidth - 2, (int) y2, Color.fromRGBA(Color.WHITE));

        if (proxyDetails != null) {
            drawStringWithShadow(matrixStack, textRenderer, proxyUsing, 2, (int) y2, Color.fromRGBA(Color.LIGHT_GRAY));
            drawStringWithShadow(matrixStack, textRenderer, proxyDetails, 2 + proxyUsingLength, (int) y2, Color.fromRGBA(Color.WHITE));
        }
    }
}
