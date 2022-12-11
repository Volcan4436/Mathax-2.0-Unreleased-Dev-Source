package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.systems.hud.DoubleTextHudElement;
import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.systems.themes.Themes;
import xyz.mathax.client.utils.Utils;
import net.minecraft.util.StringHelper;

public class ServerBrandHudElement extends DoubleTextHudElement {
    public ServerBrandHudElement(Hud hud) {
        super(hud, "Server Brand", "Displays the brand of the server you're currently in.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (!Utils.canUpdate() || mc.player.getServerBrand() == null) {
            return "None";
        }

        String brand = mc.player.getServerBrand();
        if (Systems.get(Themes.class).getTheme().customFont()) {
            brand = StringHelper.stripTextFormat(brand);
        }

        if (mc.isInSingleplayer() && brand.equals("fabric")) {
            brand = "Fabric";
        }

        return brand;
    }
}