package mathax.client.systems.hud.elements;

import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;
import mathax.client.utils.Utils;

public class SpeedHudElement extends DoubleTextHudElement {
    public SpeedHudElement(Hud hud) {
        super(hud, "Speed", "Displays your horizontal speed.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            return "0";
        }

        return String.format("%.1f", Utils.getPlayerSpeed());
    }
}
