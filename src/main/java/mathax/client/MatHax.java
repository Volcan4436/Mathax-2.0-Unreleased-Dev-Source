package mathax.client;

import mathax.client.eventbus.EventBus;
import mathax.client.eventbus.EventHandler;
import mathax.client.eventbus.EventPriority;
import mathax.client.eventbus.IEventBus;
import mathax.client.events.game.OpenScreenEvent;
import mathax.client.events.mathax.KeyEvent;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.init.PostInit;
import mathax.client.init.PreInit;
import mathax.client.systems.modules.client.CapesModule;
import mathax.client.systems.modules.render.Zoom;
import mathax.client.systems.themes.Themes;
import mathax.client.gui.WidgetScreen;
import mathax.client.gui.tabs.Tabs;
import mathax.client.systems.Systems;
import mathax.client.systems.config.Config;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Modules;
import mathax.client.init.Init;
import mathax.client.utils.Utils;
import mathax.client.utils.input.KeyAction;
import mathax.client.utils.input.KeyBinds;
import mathax.client.utils.misc.MatHaxIdentifier;
import mathax.client.utils.network.OnlinePlayers;
import mathax.client.utils.network.versions.Versions;
import mathax.client.utils.window.Icon;
import mathax.client.utils.window.Title;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class MatHax implements ClientModInitializer {
    public static MinecraftClient mc;
    public static MatHax INSTANCE;

    public static final String NAME = "MatHax";
    public static final String ID = NAME.toLowerCase(Locale.ROOT);
    public static final String PACKAGE = ID + ".client";
    public static final ModMetadata META = FabricLoader.getInstance().getModContainer(ID).get().getMetadata();

    public static final String URL = "https://" + ID + "client.xyz/";
    public static final String API_URL = URL + "api/";

    public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), NAME);
    public static final File VERSION_FOLDER = new File(FOLDER, Versions.getMinecraft());

    public static final IEventBus EVENT_BUS = new EventBus();

    public static final Logger LOG = LoggerFactory.getLogger(NAME);

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

        // Register event handlers
        EVENT_BUS.registerLambdaFactory(PACKAGE, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        // Pre-load
        if (!VERSION_FOLDER.exists()) {
            VERSION_FOLDER.mkdir();

            Systems.addPreLoadTask(() -> {
                Modules.get().get(CapesModule.class).forceToggle(true);

                Modules.get().get(Zoom.class).keybind.set(true, GLFW.GLFW_KEY_C);
                Modules.get().get(Zoom.class).toggleOnBindRelease = true;
                Modules.get().get(Zoom.class).chatFeedback = false;

                //Modules.get().get(DiscordRPC.class).forceToggle(true);
                //TODO: Pre-enable modules.
            });
        }

        // Register init classes
        Init.registerPackage(PACKAGE);

        // Pre init
        Init.init(PreInit.class);

        // Register module categories
        Categories.init();

        // Systems init
        Systems.init();

        // Systems load
        Systems.load();

        // Subscribe after systems are loaded
        EVENT_BUS.subscribe(this);

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
