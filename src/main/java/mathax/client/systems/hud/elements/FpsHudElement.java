package mathax.client.systems.hud.elements;

import mathax.client.mixin.MinecraftClientAccessor;
import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;

public class FpsHudElement extends DoubleTextHudElement {
    public FpsHudElement(Hud hud) {
        super(hud, "FPS", "Displays your FPS.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        return Integer.toString(MinecraftClientAccessor.getFps());
    }
}
