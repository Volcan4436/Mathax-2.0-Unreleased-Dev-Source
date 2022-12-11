package xyz.mathax.client.systems.modules.movement;

import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;

public class IgnoreBorder extends Module {
    public IgnoreBorder(Category category) {
        super(category, "Ignore Border", "Disables world border restrictions.");
    }
}