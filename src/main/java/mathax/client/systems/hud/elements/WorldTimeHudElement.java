package mathax.client.systems.hud.elements;

import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;
import mathax.client.utils.Utils;

public class WorldTimeHudElement extends DoubleTextHudElement {
    WorldTimeHudElement(Hud hud) {
        super(hud, "World Time", "Shows current in-game world time.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        return Utils.getWorldTime();
    }
}
