package mathax.client.systems.modules;

import com.google.common.collect.Ordering;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.eventbus.EventPriority;
import mathax.client.events.game.GameJoinedEvent;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.game.OpenScreenEvent;
import mathax.client.events.mathax.EnabledModulesChangedEvent;
import mathax.client.events.mathax.KeyEvent;
import mathax.client.events.mathax.ModuleBindChangedEvent;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.System;
import mathax.client.systems.Systems;
import mathax.client.systems.modules.chat.Spam;
import mathax.client.systems.modules.client.CapesModule;
import mathax.client.systems.modules.client.DiscordRPC;
import mathax.client.systems.modules.client.FakePlayer;
import mathax.client.systems.modules.client.MiddleClickFriend;
import mathax.client.systems.modules.combat.*;
import mathax.client.systems.modules.misc.*;
import mathax.client.systems.modules.movement.*;
import mathax.client.systems.modules.player.*;
import mathax.client.systems.modules.render.*;
import mathax.client.systems.modules.world.*;
import mathax.client.systems.modules.world.Timer;
import mathax.client.utils.Utils;
import mathax.client.utils.input.KeyBind;
import mathax.client.utils.misc.MatHaxIdentifier;
import mathax.client.utils.misc.ValueComparableMap;
import mathax.client.utils.input.Input;
import mathax.client.utils.input.KeyAction;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static mathax.client.MatHax.mc;

public class Modules extends System<Modules> {
    public static final File MODULES_FOLDER = new File(MatHax.VERSION_FOLDER, "Modules");

    public static final ModuleRegistry REGISTRY = new ModuleRegistry();

    private static final List<Category> CATEGORIES = new ArrayList<>();

    private final Map<Class<? extends Module>, Module> moduleInstances = new HashMap<>();
    private final Map<Category, List<Module>> groups = new HashMap<>();

    private final List<Module> modules = new ArrayList<>();
    private final List<Module> enabled = new ArrayList<>();

    private Module moduleToBind;

    public Modules() {
        super("Modules", null);
    }

    public static Modules get() {
        return Systems.get(Modules.class);
    }

    public <T extends Module> T get(Class<T> klass) {
        return (T) moduleInstances.get(klass);
    }

    public Module get(String name) {
        for (Module module : moduleInstances.values()) {
            if (module.name.equalsIgnoreCase(name)) {
                return module;
            }
        }

        return null;
    }

    @Override
    public void init() {
        // Combat
        Category combat = Categories.Combat;
        add(new AutoArmor(combat));
        add(new AutoDisconnect(combat));
        add(new CrystalAura(combat));
        add(new KillAura(combat));
        add(new Surround(combat));

        // Render
        Category render = Categories.Render;
        add(new Ambience(render));
        add(new BetterTooltips(render));
        add(new BlockSelection(render));
        add(new BossStack(render));
        add(new Breadcrumbs(render));
        add(new BreakIndicators(render));
        add(new CameraTweaks(render));
        add(new Chams(render));
        add(new Confetti(render));
        add(new EntityOwner(render));
        add(new ESP(render));
        add(new Freecam(render));
        add(new FreeLook(render));
        add(new Fullbright(render));
        add(new HandView(render));
        add(new HoleESP(render));
        add(new ItemHighlight(render));
        add(new ItemPhysics(render));
        add(new Nametags(render));
        add(new NoRender(render));
        add(new PopChams(render));
        add(new StorageESP(render));
        add(new Tracers(render));
        add(new Trail(render));
        add(new Trajectories(render));
        add(new TunnelESP(render));
        add(new UnfocusedCPU(render));
        add(new VoidESP(render));
        add(new WallHack(render));
        add(new WaypointsModule(render));
        add(new Xray(render));
        add(new Zoom(render));

        // Movement
        Category movement = Categories.Movement;
        add(new AirJump(movement));
        add(new GuiMove(movement));
        add(new IgnoreBorder(movement));
        add(new Sprint(movement));
        add(new TridentBoost(movement));
        add(new Velocity(movement));

        // Player
        Category player = Categories.Player;
        add(new AntiCactus(player));
        add(new AutoTool(player));
        add(new ChestSwap(player));
        add(new EndermanLook(player));
        add(new PacketMine(player));
        add(new InstaMine(player));
        add(new Reach(player));

        // World
        Category world = Categories.World;
        add(new AirPlace(world));
        add(new AutoBreed(world));
        add(new AutoMount(world));
        add(new AutoNametag(world));
        add(new AutoShearer(world));
        add(new AutoSign(world));
        add(new BuildHeight(world));
        add(new EChestFarmer(world));
        add(new Flamethrower(world));
        add(new HighwayBuilder(world));
        add(new InfinityMiner(world));
        add(new LiquidFiller(world));
        add(new Nuker(world));
        add(new SpawnProofer(world));
        add(new StashFinder(world));
        add(new TimeChanger(world));
        add(new Timer(world));
        add(new VeinMiner(world));

        // Chat
        Category chat = Categories.Chat;
        add(new Spam(chat));

        // Misc
        Category misc = Categories.Misc;
        add(new AutoReconnect(misc));
        add(new BetterBeacons(misc));
        add(new BetterTab(misc));
        add(new MountBypass(misc));
        add(new Notebot(misc));
        add(new PingSpoof(misc));
        add(new PortalChat(misc));
        add(new VanillaSpoof(misc));

        // Client
        Category client = Categories.Client;
        add(new CapesModule(client));
        add(new DiscordRPC(client));
        add(new FakePlayer(client));
        add(new MiddleClickFriend(client));
    }

    public void add(Module module) {
        // Check if the module's category is registered
        if (!CATEGORIES.contains(module.category)) {
            throw new RuntimeException("Modules.addModule - Module's category was not registered.");
        }

        // Remove the previous module with the same name
        AtomicReference<Module> removedModule = new AtomicReference<>();
        if (moduleInstances.values().removeIf(module1 -> {
            if (module1.name.equals(module.name)) {
                removedModule.set(module1);
                module1.settings.unregisterColorSettings();

                return true;
            }

            return false;
        })) {
            getGroup(removedModule.get().category).remove(removedModule.get());
        }

        // Add the module
        moduleInstances.put(module.getClass(), module);
        modules.add(module);
        getGroup(module.category).add(module);

        // Register color settings for the module
        module.settings.registerColorSettings(module);
    }

    public void sortModules() {
        for (List<Module> modules : groups.values()) {
            modules.sort(Comparator.comparing(module -> module.name));
        }

        modules.sort(Comparator.comparing(module -> module.name));
    }

    public static void registerCategory(Category category) {
        if (!Categories.REGISTERING) {
            throw new RuntimeException("Modules.registerCategory - Cannot register category outside of onRegisterCategories callback.");
        }

        CATEGORIES.add(category);
    }

    public static Iterable<Category> loopCategories() {
        return CATEGORIES;
    }

    public static Category getCategoryByHash(int hash) {
        for (Category category : CATEGORIES) {
            if (category.hashCode() == hash) {
                return category;
            }
        }

        return null;
    }

    public List<Module> getGroup(Category category) {
        return groups.computeIfAbsent(category, category1 -> new ArrayList<>());
    }

    public Collection<Module> getAll() {
        return moduleInstances.values();
    }

    public List<Module> getList() {
        return modules;
    }

    public int getCount() {
        return moduleInstances.values().size();
    }

    public boolean isEnabled(Class<? extends Module> klass) {
        Module module = get(klass);
        return module != null && module.isEnabled();
    }

    public List<Module> getEnabled() {
        synchronized (enabled) {
            return enabled;
        }
    }

    void addEnabled(Module module) {
        synchronized (enabled) {
            if (!enabled.contains(module)) {
                enabled.add(module);
                MatHax.EVENT_BUS.post(EnabledModulesChangedEvent.get());
            }
        }
    }

    void removeEnabled(Module module) {
        synchronized (enabled) {
            if (enabled.remove(module)) {
                MatHax.EVENT_BUS.post(EnabledModulesChangedEvent.get());
            }
        }
    }

    public Set<Module> searchNames(String text) {
        Map<Module, Integer> modules = new ValueComparableMap<>(Ordering.natural());
        for (Module module : this.moduleInstances.values()) {
            int score = Utils.searchLevenshteinDefault(module.name, text, false);
            modules.put(module, modules.getOrDefault(module, 0) + score);
        }

        return modules.keySet();
    }

    public Set<Module> searchSettingNames(String text) {
        Map<Module, Integer> modules = new ValueComparableMap<>(Ordering.natural());
        for (Module module : this.moduleInstances.values()) {
            int lowest = Integer.MAX_VALUE;
            for (SettingGroup settingGroup : module.settings) {
                for (Setting<?> setting : settingGroup) {
                    int score = Utils.searchLevenshteinDefault(setting.name, text, false);
                    if (score < lowest) {
                        lowest = score;
                    }
                }
            }

            modules.put(module, modules.getOrDefault(module, 0) + lowest);
        }

        return modules.keySet();
    }

    // Binding

    public void setModuleToBind(Module moduleToBind) {
        this.moduleToBind = moduleToBind;
    }

    public boolean isBinding() {
        return moduleToBind != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (event.action == KeyAction.Press && onBinding(true, event.key)) {
            event.cancel();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onButtonBinding(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && onBinding(false, event.button)) {
            event.cancel();
        }
    }

    private boolean onBinding(boolean isKey, int value) {
        if (!isBinding()) {
            return false;
        }

        if (moduleToBind.keybind.canBindTo(isKey, value)) {
            moduleToBind.keybind.set(isKey, value);
            moduleToBind.sendBound();
        } else if (value == GLFW.GLFW_KEY_ESCAPE) {
            moduleToBind.keybind.set(KeyBind.none());
            moduleToBind.info("Removed bind.");
        } else {
            return false;
        }

        MatHax.EVENT_BUS.post(ModuleBindChangedEvent.get(moduleToBind));
        moduleToBind = null;

        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Repeat) {
            return;
        }

        onAction(true, event.key, event.action == KeyAction.Press);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Repeat) {
            return;
        }

        onAction(false, event.button, event.action == KeyAction.Press);
    }

    private void onAction(boolean isKey, int value, boolean isPress) {
        if (mc.currentScreen == null && !Input.isKeyPressed(GLFW.GLFW_KEY_F3)) {
            for (Module module : moduleInstances.values()) {
                if (module.keybind.matches(isKey, value) && (isPress || module.toggleOnBindRelease)) {
                    module.toggle();
                    module.sendToggled();
                }
            }
        }
    }

    // End of binding

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onOpenScreen(OpenScreenEvent event) {
        if (!Utils.canUpdate()) {
            return;
        }

        for (Module module : moduleInstances.values()) {
            if (module.toggleOnBindRelease && module.isEnabled()) {
                module.toggle();
                module.sendToggled();
            }
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        synchronized (enabled) {
            for (Module module : modules) {
                if (module.isEnabled() && !module.alwaysRun) {
                    MatHax.EVENT_BUS.subscribe(module);
                    module.onEnable();
                }
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        synchronized (enabled) {
            for (Module module : modules) {
                if (module.isEnabled() && !module.alwaysRun) {
                    MatHax.EVENT_BUS.unsubscribe(module);
                    module.onDisable();
                }
            }
        }
    }

    @Override
    public void save(File folder) {
        if (folder != null) {
            folder = new File(folder, "Modules");
        } else {
            folder = MODULES_FOLDER;
        }

        for (Module module : modules) {
            module.save(folder);
        }
    }

    @Override
    public void load(File folder) {
        if (folder != null) {
            folder = new File(folder, "Modules");
        } else {
            folder = MODULES_FOLDER;
        }

        for (Module module : modules) {
            for (SettingGroup group : module.settings) {
                for (Setting<?> setting : group) {
                    setting.reset();
                }
            }

            module.load(folder);
        }
    }

    public static class ModuleRegistry extends SimpleRegistry<Module> {
        public ModuleRegistry() {
            super(RegistryKey.ofRegistry(new MatHaxIdentifier("modules")), Lifecycle.stable());
        }

        @Override
        public int size() {
            return Modules.get().getAll().size();
        }

        @Override
        public Identifier getId(Module entry) {
            return null;
        }

        @Override
        public Optional<RegistryKey<Module>> getKey(Module entry) {
            return Optional.empty();
        }

        @Override
        public int getRawId(Module entry) {
            return 0;
        }

        @Override
        public Module get(RegistryKey<Module> key) {
            return null;
        }

        @Override
        public Module get(Identifier id) {
            return null;
        }

        @Override
        public Lifecycle getEntryLifecycle(Module object) {
            return null;
        }

        @Override
        public Lifecycle getLifecycle() {
            return null;
        }

        @Override
        public Set<Identifier> getIds() {
            return null;
        }

        @Override
        public boolean containsId(Identifier id) {
            return false;
        }

        @Nullable
        @Override
        public Module get(int index) {
            return null;
        }

        @Override
        public Iterator<Module> iterator() {
            return new ModuleIterator();
        }

        @Override
        public boolean contains(RegistryKey<Module> key) {
            return false;
        }

        @Override
        public Set<Map.Entry<RegistryKey<Module>, Module>> getEntrySet() {
            return null;
        }

        @Override
        public Set<RegistryKey<Module>> getKeys() {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getRandom(Random random) {
            return Optional.empty();
        }

        @Override
        public Registry<Module> freeze() {
            return null;
        }

        @Override
        public RegistryEntry.Reference<Module> createEntry(Module value) {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getEntry(int rawId) {
            return Optional.empty();
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getEntry(RegistryKey<Module> key) {
            return Optional.empty();
        }

        @Override
        public Stream<RegistryEntry.Reference<Module>> streamEntries() {
            return null;
        }

        @Override
        public Optional<RegistryEntryList.Named<Module>> getEntryList(TagKey<Module> tag) {
            return Optional.empty();
        }

        @Override
        public RegistryEntryList.Named<Module> getOrCreateEntryList(TagKey<Module> tag) {
            return null;
        }

        @Override
        public Stream<Pair<TagKey<Module>, RegistryEntryList.Named<Module>>> streamTagsAndEntries() {
            return null;
        }

        @Override
        public Stream<TagKey<Module>> streamTags() {
            return null;
        }

        @Override
        public void clearTags() {}

        @Override
        public void populateTags(Map<TagKey<Module>, List<RegistryEntry<Module>>> tagEntries) {}

        private static class ModuleIterator implements Iterator<Module> {
            private final Iterator<Module> iterator = Modules.get().getAll().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Module next() {
                return iterator.next();
            }
        }
    }
}
