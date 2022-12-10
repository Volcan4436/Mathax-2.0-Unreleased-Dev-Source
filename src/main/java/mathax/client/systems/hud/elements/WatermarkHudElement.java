package mathax.client.systems.hud.elements;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameJoinedEvent;
import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.renderer.GL;
import mathax.client.renderer.Renderer2D;
import mathax.client.settings.*;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.TripleTextHudElement;
import mathax.client.utils.misc.MatHaxIdentifier;
import mathax.client.utils.network.versions.Versions;
import mathax.client.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class WatermarkHudElement extends TripleTextHudElement {
    private static final Identifier MATHAX_LOGO = new MatHaxIdentifier("icons/1080.png");

    public static boolean didntCheckForUpdate = true;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Mode> modeSetting = generalSettings.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Watermark style to use.")
            .defaultValue(Mode.Both)
            .build()
    );

    private final Setting<Double> scaleSetting = generalSettings.add(new DoubleSetting.Builder()
            .name("Scale")
            .description("Scale of the icon.")
            .defaultValue(1)
            .min(1)
            .sliderRange(1, 5)
            .visible(() -> modeSetting.get() == Mode.Icon)
            .build()
    );

    private final Setting<Boolean> updateCheckerSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Update Checker")
            .description("Checks if a new version of MatHax is available.")
            .defaultValue(true)
            .build()
    );

    public WatermarkHudElement(Hud hud) {
        super(hud, "Watermark", "Displays a MatHax watermark.", true);
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        didntCheckForUpdate = true;
    }

    protected String getLeft() {
        return MatHax.NAME + " ";
    }

    protected String getCenter() {
        return Versions.getStylized();
    }

    protected String getRight() {
        if (updateCheckerSetting.get()) {
            if (didntCheckForUpdate) {
                didntCheckForUpdate = false;
                Versions.checkForUpdate();
            }

            if (Versions.isUpdateAvailable()) {
                return " [Outdated | Latest version: " + Versions.getStylized(true) + "]";
            }
        }

        return "";
    }

    @Override
    public void render(OverlayRenderer renderer) {
        double textWidth = renderer.textWidth(getLeft()) + renderer.textWidth(getCenter()) + renderer.textWidth(getRight());

        switch (modeSetting.get()) {
            case Text -> {
                box.setSize(textWidth, renderer.textHeight());

                double x = box.getX();
                double y = box.getY();

                renderer.text(getLeft(), x, y, hud.primaryColorSetting.get());
                renderer.text(getCenter(), x + renderer.textWidth(getLeft()), y, hud.secondaryColorSetting.get());
                renderer.text(getRight(), x + textWidth - renderer.textWidth(getRight()), y, hud.primaryColorSetting.get());
            }
            case Icon -> box.setSize(renderer.textHeight() * scaleSetting.get(),  renderer.textHeight() * scaleSetting.get());
            default -> {
                box.setSize(renderer.textHeight() + 2 + textWidth, renderer.textHeight());

                double x = box.getX();
                double y = box.getY();

                renderer.text(getLeft(), x + 2 + renderer.textHeight(), y + 2, hud.primaryColorSetting.get());
                renderer.text(getCenter(), x + 2 + renderer.textHeight() + renderer.textWidth(getLeft()), y + 2, hud.secondaryColorSetting.get());
                renderer.text(getRight(), x + 2 + renderer.textHeight() + textWidth - renderer.textWidth(getRight()), y + 2, hud.primaryColorSetting.get());
            }
        }

        GL.bindTexture(MATHAX_LOGO);
        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texturedQuad(box.getX(), box.getY(), box.width - (modeSetting.get() != Mode.Text ? textWidth : 0), box.height, Color.WHITE);
        Renderer2D.TEXTURE.render(null);
    }

    public enum Mode {
        Text("Text"),
        Icon("Icon"),
        Both("Both");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}