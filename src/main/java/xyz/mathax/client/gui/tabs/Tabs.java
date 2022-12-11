package xyz.mathax.client.gui.tabs;

import xyz.mathax.client.init.PreInit;
import xyz.mathax.client.gui.tabs.builtin.*;

import java.util.ArrayList;
import java.util.List;

public class Tabs {
    private static final List<Tab> tabs = new ArrayList<>();

    @PreInit
    public static void init() {
        add(new ModulesTab());
        add(new ConfigTab());
        add(new GuiTab());
        add(new HudTab());
        add(new FriendsTab());
        add(new EnemiesTab());
        add(new MacrosTab());
        add(new ProfilesTab());
        add(new FakePlayersTab());
        add(new BaritoneTab());
    }

    public static void add(Tab tab) {
        tabs.add(tab);
    }

    public static List<Tab> get() {
        return tabs;
    }
}
