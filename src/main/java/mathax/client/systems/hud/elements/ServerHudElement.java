package mathax.client.systems.hud.elements;

import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;
import mathax.client.utils.Utils;

public class ServerHudElement extends DoubleTextHudElement {
    public ServerHudElement(Hud hud) {
        super(hud, "Server", "Displays the server you're currently in.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (!Utils.canUpdate()) {
            return "None";
        }

        return Utils.getWorldName();
    }
}



