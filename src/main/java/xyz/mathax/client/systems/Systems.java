package xyz.mathax.client.systems;

import xyz.mathax.client.MatHax;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.game.GameLeftEvent;
import xyz.mathax.client.systems.accounts.Accounts;
import xyz.mathax.client.systems.commands.Commands;
import xyz.mathax.client.systems.config.Config;
import xyz.mathax.client.systems.enemies.Enemies;
import xyz.mathax.client.systems.friends.Friends;
import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.systems.macros.Macros;
import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.profiles.Profiles;
import xyz.mathax.client.systems.proxies.Proxies;
import xyz.mathax.client.systems.themes.Themes;
import xyz.mathax.client.systems.waypoints.Waypoints;
import xyz.mathax.client.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Systems {
    private static final Map<Class<? extends System>, System<?>> systems = new HashMap<>();
    private static final List<Runnable> preLoadTasks = new ArrayList<>(1);

    public static void addPreLoadTask(Runnable task) {
        preLoadTasks.add(task);
    }

    public static void init() {
        System<?> config = add(new Config());
        config.init();
        config.load();

        add(new Themes());
        add(new Modules());
        add(new Commands());
        add(new Friends());
        add(new Enemies());
        add(new Macros());
        add(new Accounts());
        add(new Waypoints());
        add(new Profiles());
        add(new Proxies());
        add(new Hud());

        MatHax.EVENT_BUS.subscribe(Systems.class);
    }

    public static <T extends System<?>> T get(Class<T> klass) {
        return (T) systems.get(klass);
    }

    private static System<?> add(System<?> system) {
        systems.put(system.getClass(), system);
        MatHax.EVENT_BUS.subscribe(system);
        system.init();

        return system;
    }

    // Save

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        save();
    }

    public static void save(File folder) {
        long start = Utils.getCurrentTimeMillis();

        MatHax.LOG.info("Saving...");

        for (System<?> system : systems.values()) {
            system.save(folder);
        }

        MatHax.LOG.info("Saved in {} milliseconds.", Utils.getCurrentTimeMillis() - start);
    }

    public static void save() {
        save(null);
    }

    // Load

    public static void load(File folder) {
        long start = Utils.getCurrentTimeMillis();

        MatHax.LOG.info("Loading...");

        for (Runnable task : preLoadTasks) {
            task.run();
        }

        for (System<?> system : systems.values()) {
            system.load(folder);
        }

        MatHax.LOG.info("Loaded in {} milliseconds.", Utils.getCurrentTimeMillis() - start);
    }

    public static void load() {
        load(null);
    }
}