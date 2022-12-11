package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.systems.hud.DoubleTextHudElement;
import xyz.mathax.client.systems.hud.Hud;

public class DurabilityHudElement extends DoubleTextHudElement {
    public DurabilityHudElement(Hud hud) {
        super(hud, "Durability", "Displays durability of the item you are holding.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            return "69";
        }

        if (!mc.player.getMainHandStack().isEmpty() && mc.player.getMainHandStack().isDamageable()) {
            return String.valueOf(mc.player.getMainHandStack().getMaxDamage() - mc.player.getMainHandStack().getDamage());
        }

        return "Infinite";
    }
}