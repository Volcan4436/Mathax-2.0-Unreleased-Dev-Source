package xyz.mathax.client;

import xyz.mathax.client.addons.AddonManager;
import xyz.mathax.client.addons.MatHaxAddon;
import xyz.mathax.client.eventbus.EventBus;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.eventbus.EventPriority;
import xyz.mathax.client.eventbus.IEventBus;
import xyz.mathax.client.events.game.OpenScreenEvent;
import xyz.mathax.client.events.mathax.KeyEvent;
import xyz.mathax.client.events.mathax.MouseButtonEvent;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.init.PostInit;
import xyz.mathax.client.init.PreInit;
import xyz.mathax.client.systems.modules.client.DiscordRPC;
import xyz.mathax.client.systems.modules.render.Zoom;
import xyz.mathax.client.systems.themes.Themes;
import xyz.mathax.client.gui.WidgetScreen;
import xyz.mathax.client.gui.tabs.Tabs;
import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.systems.config.Config;
import xyz.mathax.client.systems.modules.Categories;
import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.init.Init;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.input.KeyAction;
import xyz.mathax.client.utils.input.KeyBinds;
import xyz.mathax.client.utils.misc.MatHaxIdentifier;
import xyz.mathax.client.utils.network.OnlinePlayers;
import xyz.mathax.client.utils.network.versions.Versions;
import xyz.mathax.client.utils.window.Icon;
import xyz.mathax.client.utils.window.Title;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class MatHax implements ClientModInitializer {
    public static MinecraftClient mc;
    public static MatHax INSTANCE;
    public static MatHaxAddon ADDON;

    public static final String NAME = "MatHax";
    public static final String ID = NAME.toLowerCase(Locale.ROOT);
    public static final ModMetadata META = FabricLoader.getInstance().getModContainer(ID).get().getMetadata();

    public static final String URL = "https://" + ID + "client.xyz/";
    public static final String API_URL = URL + "api/";

    public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), NAME);
    public static final File VERSION_FOLDER = new File(FOLDER, Versions.getMinecraft());

    public static final IEventBus EVENT_BUS = new EventBus();

    public static final Logger LOG = LoggerFactory.getLogger(NAME);

    public static List<String> getSplashes() {
        String username = MinecraftClient.getInstance().getSession().getUsername();
        return Arrays.asList(
                // SPLASHES
                Formatting.RED + "MatHax on top!",
                Formatting.RED + "Matejko06" + Formatting.GRAY + " based god",
                Formatting.RED + "MatHaxClient.xyz",
                Formatting.RED + "MatHaxClient.xyz/Discord",
                Formatting.RED + Versions.getStylized(),
                Formatting.RED + Versions.getMinecraft(),

                // MEME SPLASHES
                Formatting.YELLOW + "cope",
                Formatting.YELLOW + "IntelliJ IDEa",
                Formatting.YELLOW + "I <3 nns",
                Formatting.YELLOW + "haha 69",
                Formatting.YELLOW + "420 XDDDDDD",
                Formatting.YELLOW + "ayy",
                Formatting.YELLOW + "too ez",
                Formatting.YELLOW + "owned",
                Formatting.YELLOW + "your mom :joy:",
                Formatting.YELLOW + "BOOM BOOM BOOM!",
                Formatting.YELLOW + "I <3 forks",
                Formatting.YELLOW + "based",
                Formatting.YELLOW + "Pog",
                Formatting.YELLOW + "Big Rat on top!",
                Formatting.YELLOW + "bigrat.monster",
                Formatting.YELLOW + "Hack on anarchyclef.eyezah.com",
                Formatting.YELLOW + "Hack on 2b2t.org",
                Formatting.YELLOW + "Better Than Wurst",
                Formatting.YELLOW + "Better Than Hypixel",
                Formatting.YELLOW + "Better Than Mineplex",
                Formatting.YELLOW + "Better Than OptiFine",
                Formatting.YELLOW + "Better Than Internet Explorer",
                Formatting.YELLOW + "Better Than Dream",
                Formatting.YELLOW + "Wish was better than Technoblade",
                Formatting.YELLOW + "Chad Water",
                Formatting.YELLOW + "Better than NoComm",
                Formatting.YELLOW + "Better than Minecon",
                Formatting.YELLOW + "L Bozo",

                // PERSONALIZED
                Formatting.YELLOW + "You're cool, " + Formatting.GRAY + username,
                Formatting.YELLOW + "Owning with " + Formatting.GRAY + username,
                Formatting.YELLOW + "Who is " + Formatting.GRAY + username + Formatting.YELLOW + "?",
                Formatting.YELLOW + "Watching hentai with " + Formatting.GRAY + username + Formatting.YELLOW + "!"
        );
    }

    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        long start = Utils.getCurrentTimeMillis();

        // Global minecraft client accessor
        mc = MinecraftClient.getInstance();

        // Start
        LOG.info("Initializing {} {} for Minecraft {} {}...", NAME, Versions.getStylized(), mc.getVersionType(), Versions.getMinecraft());
        Title.setTitle("[Initializing] " + NAME + " " + Versions.getStylized() + " - Minecraft " + mc.getVersionType() + " " + Versions.getMinecraft(), true);
        Icon.setIcon(new MatHaxIdentifier("icons/64.png"), new MatHaxIdentifier("icons/128.png"));

        // Pre-load
        if (!VERSION_FOLDER.exists()) {
            VERSION_FOLDER.mkdir();

            Systems.addPreLoadTask(() -> {
                Modules.get().get(CapesModule.class).forceToggle(true);

                Modules.get().get(Zoom.class).keybind.set(true, GLFW.GLFW_KEY_C);
                Modules.get().get(Zoom.class).toggleOnBindRelease = true;
                Modules.get().get(Zoom.class).chatFeedback = false;

                Modules.get().get(DiscordRPC.class).forceToggle(true);
                //TODO: Pre-enable modules.
            });
        }

        // Register addons
        AddonManager.init();

        // Register event handlers
        EVENT_BUS.registerLambdaFactory(ADDON.getPackage() , (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        AddonManager.ADDONS.forEach(addon -> {
            try {
                EVENT_BUS.registerLambdaFactory(addon.getPackage(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
            } catch (AbstractMethodError exception) {
                throw new RuntimeException("Addon \"%s\" is too old and cannot be ran.".formatted(addon.name), exception);
            }
        });

        // Register init classes
        Init.registerPackages(ADDON.getPackage());

        // Pre init
        Init.init(PreInit.class);

        // Register module categories
        Categories.init();

        // Load systems
        Systems.init();

        // Subscribe after systems are loaded
        EVENT_BUS.subscribe(this);

        // Initialise addons
        AddonManager.ADDONS.forEach(MatHaxAddon::onInitialize);

        // Sort modules after addons have added their own
        Modules.get().sortModules();

        // Load configs
        Systems.load();

        // Post init
        Init.init(PostInit.class);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlinePlayers.leave();
            Systems.save();
        }));

        // End
        LOG.info("{} {} for Minecraft {} {} initialized in {} milliseconds!", NAME, Versions.getStylized(), mc.getVersionType(), Versions.getMinecraft(), Utils.getCurrentTimeMillis() - start);
        Title.setTitle(NAME + " " + Versions.getStylized() + " - Minecraft " + mc.getVersionType() + " " + Versions.getMinecraft(), true);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.currentScreen == null && mc.getOverlay() == null && KeyBinds.OPEN_COMMANDS.wasPressed()) {
            mc.setScreen(new ChatScreen(Config.get().prefixSetting.get()));
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.matchesKey(event.key, 0)) {
            openGui();
        }
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && KeyBinds.OPEN_GUI.matchesMouse(event.button)) {
            openGui();
        }
    }

    private void openGui() {
        if (Utils.canOpenGui()) {
            Tabs.get().get(0).openScreen(Systems.get(Themes.class).getTheme());
        }
    }

    // Hide HUD

    private boolean wasWidgetScreen, wasHudHiddenRoot;

    @EventHandler(priority = EventPriority.LOWEST)
    private void onOpenScreen(OpenScreenEvent event) {
        boolean hideHud = Systems.get(Themes.class).getTheme().hideHud();

        if (hideHud) {
            if (!wasWidgetScreen) {
                wasHudHiddenRoot = mc.options.hudHidden;
            }

            if (event.screen instanceof WidgetScreen) {
                mc.options.hudHidden = true;
            } else if (!wasHudHiddenRoot) {
                mc.options.hudHidden = false;
            }
        }

        wasWidgetScreen = event.screen instanceof WidgetScreen;
    }
}
