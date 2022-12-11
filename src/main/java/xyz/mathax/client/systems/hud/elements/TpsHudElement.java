package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.systems.hud.DoubleTextHudElement;
import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.utils.world.TickRate;

public class TpsHudElement extends DoubleTextHudElement {
    public TpsHudElement(Hud hud) {
        super(hud, "TPS", "Displays the server's TPS.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        return String.format("%.1f", TickRate.INSTANCE.getTickRate());
    }
}
