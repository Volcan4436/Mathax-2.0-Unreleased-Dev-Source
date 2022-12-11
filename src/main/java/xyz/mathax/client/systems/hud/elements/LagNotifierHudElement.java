package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.systems.hud.TripleTextHudElement;
import xyz.mathax.client.utils.render.color.Color;
import xyz.mathax.client.utils.world.TickRate;

public class LagNotifierHudElement extends TripleTextHudElement {
    public LagNotifierHudElement(Hud hud) {
        super(hud, "Lag Notifier", "Displays if the server is lagging in ticks.");
    }

    @Override
    protected String getLeft() {
        return "Server not responding for ";
    }

    @Override
    protected String getCenter() {
        if (isInEditor()) {
            centerColor = Color.RED;
            visible = true;
            return "83";
        }

        float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick() * 20;
        if (timeSinceLastTick > 200) {
            centerColor = Color.RED;
        } else if (timeSinceLastTick > 60) {
            centerColor = Color.ORANGE;
        } else {
            centerColor = Color.YELLOW;
        }

        visible = timeSinceLastTick >= 20f;

        return String.format("%d", (int) timeSinceLastTick);
    }

    @Override
    protected String getRight() {
        return " ticks";
    }
}
