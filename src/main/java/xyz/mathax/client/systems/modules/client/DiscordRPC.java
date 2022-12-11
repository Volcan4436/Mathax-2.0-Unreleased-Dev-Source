package xyz.mathax.client.systems.modules.client;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.gui.WidgetScreen;
import xyz.mathax.client.settings.BoolSetting;
import xyz.mathax.client.settings.IntSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.modules.misc.NameProtect;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.network.versions.Versions;
import xyz.mathax.client.utils.player.PlayerUtils;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DiscordRPC extends Module {
    public static final List<Pair<String, String>> customStates = new ArrayList<>();

    private static final RichPresence rpc = new RichPresence();

    private int ticks;

    private final SettingGroup generalSettings = settings.createGroup("General");
    private final SettingGroup detailSettings = settings.createGroup("Details");
    private final SettingGroup stateSettings = settings.createGroup("State");

    // General

    private final Setting<Integer> updateDelaySetting = generalSettings.add(new IntSetting.Builder()
            .name("Update delay")
            .description("How fast to update the status in ticks.")
            .defaultValue(100)
            .min(10)
            .sliderRange(10, 200)
            .build()
    );

    // Details

    public final Setting<Boolean> nameSetting = detailSettings.add(new BoolSetting.Builder()
            .name("Name")
            .description("Show your name in the status.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> healthSetting = detailSettings.add(new BoolSetting.Builder()
            .name("Health")
            .description("Show your health in the status.")
            .defaultValue(true)
            .build()
    );

    // State

    public final Setting<Boolean> worldNameSetting = stateSettings.add(new BoolSetting.Builder()
            .name("World name")
            .description("Show current world name or server IP address.")
            .defaultValue(true)
            .build()
    );

    public DiscordRPC(Category category) {
        super(category, "Discord RPC", "Shows MatHax as your Discord status.", true);

        registerCustomState("com.terraformersmc.modmenu.gui", "Browsing mods");
        registerCustomState("me.jellysquid.mods.sodium.client", "Changing options");
        registerCustomState("de.maxhenkel.voicechat.gui", "Changing voice chat options");
    }

    @Override
    public void onEnable() {
        DiscordIPC.start(878967665501306920L, null);

        rpc.setStart(System.currentTimeMillis() / 1000L);

        ticks = 0;

        update();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        update();
    }

    private void update() {
        if (ticks >= updateDelaySetting.get()) {
            rpc.setDetails(getVersions() + (nameSetting.get() ? " | " + getUsername() : "") + (healthSetting.get() && mc.world != null && mc.player != null ? " | " + getHealth() : ""));
            rpc.setLargeImage("mathax", "MatHax " + getVersions());
            rpc.setState(getState());

            DiscordIPC.setActivity(rpc);

            ticks = 0;
        } else {
            ticks++;
        }
    }

    private String getVersions() {
        return Versions.getStylized() + " - " + Versions.getMinecraft();
    }

    private String getUsername() {
        return Modules.get().get(NameProtect.class).getName();
    }

    private String getHealth() {
        String text = "";
        if (mc.world != null && mc.player != null) {
            if (mc.player.isDead()) {
                text = "Dead";
            } else if (mc.player.isCreative()) {
                text = "Creative Mode";
            } else if (mc.player.isSpectator()) {
                text = "Spectator Mode";
            } else {
                text = Math.round(PlayerUtils.getTotalHealth()) + " HP";
            }
        }

        return text;
    }

    private String getState() {
        String state = null;
        if (mc.world != null && mc.player != null) {
            state = "Playing" + (worldNameSetting.get() ? " on " + Utils.getWorldName() : "") + " (" + (mc.isInSingleplayer() ? "Singleplayer" : "Multiplayer") + ")";
        } else if (mc.getOverlay() instanceof SplashOverlay && mc.world == null && mc.player == null) {
            state = "Loading...";
        } else if (mc.currentScreen instanceof TitleScreen) {
            state = "Looking at title screen";
        } else if (mc.currentScreen instanceof SelectWorldScreen) {
            state = "Selecting world";
        } else if (mc.currentScreen instanceof CreateWorldScreen || mc.currentScreen instanceof EditGameRulesScreen) {
            state = "Creating world";
        } else if (mc.currentScreen instanceof EditWorldScreen) {
            state = "Editing world";
        } else if (mc.currentScreen instanceof LevelLoadingScreen) {
            state = "Loading world";
        } else if (mc.currentScreen instanceof MultiplayerScreen) {
            state = "Selecting server";
        } else if (mc.currentScreen instanceof AddServerScreen) {
            state = "Adding server";
        } else if (mc.currentScreen instanceof ConnectScreen || mc.currentScreen instanceof DirectConnectScreen) {
            state = "Connecting to server";
        } else if (mc.currentScreen instanceof WidgetScreen) {
            state = "Browsing MatHax's GUI";
        } else if (mc.currentScreen instanceof OptionsScreen || mc.currentScreen instanceof SkinOptionsScreen || mc.currentScreen instanceof SoundOptionsScreen || mc.currentScreen instanceof VideoOptionsScreen || mc.currentScreen instanceof ControlsOptionsScreen || mc.currentScreen instanceof LanguageOptionsScreen || mc.currentScreen instanceof ChatOptionsScreen || mc.currentScreen instanceof PackScreen || mc.currentScreen instanceof AccessibilityOptionsScreen) {
            state = "Changing options";
        } else if (mc.currentScreen instanceof CreditsScreen) {
            state = "Reading credits";
        } else if (mc.currentScreen instanceof RealmsScreen) {
            state = "Browsing Realms";
        } else {
            String className = mc.currentScreen.getClass().getName();
            boolean setState = false;
            for (var pair : customStates) {
                if (className.startsWith(pair.getLeft())) {
                    state = pair.getRight();
                    setState = true;
                    break;
                }
            }

            if (!setState) {
                state = "In main menu";
            }
        }

        return state;
    }

    @Override
    public void onDisable() {
        rpc.setState("Shutting down...");

        DiscordIPC.setActivity(rpc);

        DiscordIPC.stop();
    }

    public static void registerCustomState(String packageName, String state) {
        for (var pair : customStates) {
            if (pair.getLeft().equals(packageName)) {
                pair.setRight(state);
                return;
            }
        }

        customStates.add(new Pair<>(packageName, state));
    }

    public static void unregisterCustomState(String packageName) {
        customStates.removeIf(pair -> pair.getLeft().equals(packageName));
    }
}