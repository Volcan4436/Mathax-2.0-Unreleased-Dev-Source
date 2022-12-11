package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.mixin.MinecraftClientAccessor;
import xyz.mathax.client.systems.hud.DoubleTextHudElement;
import xyz.mathax.client.systems.hud.Hud;

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
