package mathax.client.gui.tabs.builtin;

import baritone.api.BaritoneAPI;
import baritone.api.utils.SettingsUtil;
import mathax.client.systems.themes.Theme;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.WindowTabScreen;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.settings.*;
import mathax.client.utils.misc.BaritoneSettingValue;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaritoneTab extends Tab {
    private static Settings settings;

    private static Map<String, BaritoneSettingValue> settingValues;

    public BaritoneTab() {
        super("Baritone");
    }

    private static Settings getSettings() {
        if (settings != null) {
            return settings;
        }

        settings = new Settings();

        SettingGroup boolSettings = settings.createGroup("Checkboxes");
        SettingGroup doubleSettings = settings.createGroup("Numbers");
        SettingGroup itnSettings = settings.createGroup("Whole Numbers");
        SettingGroup stringSettings = settings.createGroup("Strings");
        SettingGroup colorSettings = settings.createGroup("Colors");
        SettingGroup blockListSettings = settings.createGroup("Block Lists");
        SettingGroup itemListSettings = settings.createGroup("Item Lists");

        try {
            Class<? extends baritone.api.Settings> klass = BaritoneAPI.getSettings().getClass();
            for (Field field : klass.getDeclaredFields()) {
                Object object = field.get(BaritoneAPI.getSettings());
                if (!(object instanceof baritone.api.Settings.Setting setting)) {
                    continue;
                }

                Object value = setting.value;
                if (value instanceof Boolean) {
                    boolSettings.add(new BoolSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue((boolean) setting.defaultValue)
                        .onChanged(aBoolean -> setting.value = aBoolean)
                        .onModuleActivated(booleanSetting -> booleanSetting.set((Boolean) setting.value))
                        .build()
                    );
                } else if (value instanceof Double) {
                    doubleSettings.add(new DoubleSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue((double) setting.defaultValue)
                        .onChanged(aDouble -> setting.value = aDouble)
                        .onModuleActivated(doubleSetting -> doubleSetting.set((Double) setting.value))
                        .build()
                    );
                } else if (value instanceof Float) {
                    doubleSettings.add(new DoubleSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue(((Float) setting.defaultValue).doubleValue())
                        .onChanged(aDouble -> setting.value = aDouble.floatValue())
                        .onModuleActivated(doubleSetting -> doubleSetting.set(((Float) setting.value).doubleValue()))
                        .build()
                    );
                } else if (value instanceof Integer) {
                    itnSettings.add(new IntSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue((int) setting.defaultValue)
                        .onChanged(integer -> setting.value = integer)
                        .onModuleActivated(integerSetting -> integerSetting.set((Integer) setting.value))
                        .build()
                    );
                } else if (value instanceof Long) {
                    itnSettings.add(new IntSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue(((Long) setting.defaultValue).intValue())
                        .onChanged(integer -> setting.value = integer.longValue())
                        .onModuleActivated(integerSetting -> integerSetting.set(((Long) setting.value).intValue()))
                        .build()
                    );
                } else if (value instanceof String) {
                    stringSettings.add(new StringSetting.Builder()
                            .name(setting.getName())
                            .description(getDescription(setting.getName()))
                            .defaultValue((String) setting.defaultValue)
                            .onChanged(string -> setting.value = string)
                            .onModuleActivated(stringSetting -> stringSetting.set((String) setting.value))
                            .build()
                    );
                } else if (value instanceof Color) {
                    Color color = (Color) setting.value;
                    colorSettings.add(new ColorSetting.Builder()
                        .name(setting.getName())
                        .description(getDescription(setting.getName()))
                        .defaultValue(new SettingColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()))
                        .onChanged(colorValue -> setting.value = new Color(colorValue.r, colorValue.g, colorValue.b, colorValue.a))
                        .onModuleActivated(colorSetting -> colorSetting.set(new SettingColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())))
                        .build()
                    );
                } else if (value instanceof List) {
                    Type listType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    Type type = ((ParameterizedType) listType).getActualTypeArguments()[0];
                    if (type == Block.class) {
                        blockListSettings.add(new BlockListSetting.Builder()
                                .name(setting.getName())
                                .description(getDescription(setting.getName()))
                                .defaultValue((List<Block>) setting.defaultValue)
                                .onChanged(blockList -> setting.value = blockList)
                                .onModuleActivated(blockListSetting -> blockListSetting.set((List<Block>) setting.value))
                                .build()
                        );
                    } else if (type == Item.class) {
                        itemListSettings.add(new ItemListSetting.Builder()
                                .name(setting.getName())
                                .description(getDescription(setting.getName()))
                                .defaultValue((List<Item>) setting.defaultValue)
                                .onChanged(itemList -> setting.value = itemList)
                                .onModuleActivated(itemListSetting -> itemListSetting.set((List<Item>) setting.value))
                                .build()
                        );
                    }
                }
            }
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return settings;
    }

    @Override
    public TabScreen createScreen(Theme theme) {
        return new BaritoneScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof BaritoneScreen;
    }

    private static void addSettingValue(String settingName, String name, String description) {
        settingValues.put(settingName.toLowerCase(), new BaritoneSettingValue(name, description));
    }

    private static String getName(String settingName) {
        if (settingValues == null) {
            loadSettingValues();
        }

        return settingValues.get(settingName.toLowerCase()).name();
    }
    
    private static String getDescription(String settingName) {
        if (settingValues == null) {
            loadSettingValues();
        }

        return settingValues.get(settingName.toLowerCase()).description();
    }

    private static class BaritoneScreen extends WindowTabScreen {
        public BaritoneScreen(Theme theme, Tab tab) {
            super(theme, tab);

            getSettings().onActivated();
        }

        @Override
        public void initWidgets() {
            WTextBox filter = add(theme.textBox("")).minWidth(400).expandX().widget();
            filter.setFocused(true);
            filter.action = () -> {
                clear();

                add(filter);
                add(theme.settings(getSettings(), filter.get().trim())).expandX();
            };

            add(theme.settings(getSettings(), filter.get().trim())).expandX();
        }

        @Override
        protected void onClosed() {
            SettingsUtil.save(BaritoneAPI.getSettings());
        }
    }

    private static void loadSettingValues() {
        settingValues = new HashMap<>();

        addSettingValue("acceptableThrowawayItems", "Acceptable throwaway items", "Blocks that Baritone is allowed to place (as throwaway, for sneak bridging, pillaring, etc.)");
        addSettingValue("allowBreak", "Allow break", "Allow Baritone to break blocks.");
        addSettingValue("allowBreakAnyway", "Allow break anyway", "Blocks that baritone will be allowed to break even with allowBreak set to false.");
        addSettingValue("allowDiagonalAscend", "Allow diagonal ascend", "Allow diagonal ascending.");
        addSettingValue("allowDiagonalDescend", "Allow diagonal descend", "Allow descending diagonally.");
        addSettingValue("allowDownward", "Allow downward", "Allow mining the block directly beneath its feet.");
        addSettingValue("allowInventory", "Allow inventory", "Allow Baritone to move items in your inventory to your hotbar.");
        addSettingValue("allowJumpAt256", "Allow jump at 256", "If true, parkour is allowed to make jumps when standing on blocks at the maximum height, so player feet is y=256.");
        addSettingValue("allowOnlyExposedOres", "Allow only exposed ores", "This will only allow baritone to mine exposed ores, can be used to stop ore obfuscators on servers that use them.");
        addSettingValue("allowOnlyExposedOresDistance", "Allow only exposed ores distance", "When allowOnlyExposedOres is enabled this is the distance around to search.");
        addSettingValue("allowOvershootDiagonalDescend","Allow overshoot diagonal descend", "Is it okay to sprint through a descend followed by a diagonal? The player overshoots the landing, but not enough to fall off.");
        addSettingValue("allowParkour", "Allow parkour", "You know what it is.");
        addSettingValue("allowParkourAscend", "Allow parkour ascend", "This should be monetized it's so good.");
        addSettingValue("allowParkourPlace", "Allow parkour place", "Actually pretty reliable.");
        addSettingValue("allowPlace", "Allow place", "Allow Baritone to place blocks.");
        addSettingValue("allowSprint", "Allow sprint", "Allow Baritone to sprint.");
        addSettingValue("allowVines", "Allow vines", "Enables some more advanced vine features.");
        /*addSettingValue("allowWalkOnBottomSlab", "Slab behavior is complicated, disable this for higher path reliability.");
        addSettingValue("allowWaterBucketFall", "Allow Baritone to fall arbitrary distances and place a water bucket beneath it.");
        addSettingValue("antiCheatCompatibility", "Will cause some minor behavioral differences to ensure that Baritone works on anticheats.");
        addSettingValue("assumeExternalAutoTool", "Disable baritone's auto-tool at runtime, but still assume that another mod will provide auto tool functionality");
        addSettingValue("assumeSafeWalk", "Assume safe walk functionality; don't sneak on a backplace traverse.");
        addSettingValue("assumeStep", "Assume step functionality; don't jump on an Ascend.");
        addSettingValue("assumeWalkOnLava", "If you have Fire Resistance and Jesus then I guess you could turn this on lol");
        addSettingValue("assumeWalkOnWater", "Allow Baritone to assume it can walk on still water just like any other block.");
        addSettingValue("autoTool", "Automatically select the best available tool");
        addSettingValue("avoidance", "Toggle the following 4 settings");
        addSettingValue("avoidBreakingMultiplier", "this multiplies the break speed, if set above 1 it's \"encourage breaking\" instead");
        addSettingValue("avoidUpdatingFallingBlocks", "If this setting is true, Baritone will never break a block that is adjacent to an unsupported falling block.");
        addSettingValue("axisHeight", "The \"axis\" command (aka GoalAxis) will go to a axis, or diagonal axis, at this Y level.");
        addSettingValue("backfill", "Fill in blocks behind you (stealth +100)");
        addSettingValue("backtrackCostFavoringCoefficient", "Set to 1.0 to effectively disable this feature");
        addSettingValue("blacklistClosestOnFailure", "When GetToBlockProcess or MineProcess fails to calculate a path, instead of just giving up, mark the closest instance of that block as \"unreachable\" and go towards the next closest.");
        addSettingValue("blockBreakAdditionalPenalty", "This is just a tiebreaker to make it less likely to break blocks if it can avoid it.");
        addSettingValue("blockPlacementPenalty", "It doesn't actually take twenty ticks to place a block, this cost is so high because we want to generally conserve blocks which might be limited.");
        addSettingValue("blockReachDistance", "Block reach distance");
        addSettingValue("blocksToAvoid", "Blocks that Baritone will attempt to avoid (Used in avoidance)");
        addSettingValue("blocksToAvoidBreaking", "blocks that baritone shouldn't break, but can if it needs to.");
        addSettingValue("blocksToDisallowBreaking", "Blocks that Baritone is not allowed to break");
        addSettingValue("breakCorrectBlockPenaltyMultiplier", "Multiply the cost of breaking a block that's correct in the builder's schematic by this coefficient");
        addSettingValue("breakFromAbove", "Allow standing above a block while mining it, in BuilderProcess");
        addSettingValue("builderTickScanRadius", "Distance to scan every tick for updates.");
        addSettingValue("buildIgnoreBlocks", "A list of blocks to be treated as if they're air.");
        addSettingValue("buildIgnoreDirection", "If this is true, the builder will ignore directionality of certain blocks like glazed terracotta.");
        addSettingValue("buildIgnoreExisting", "If this is true, the builder will treat all non-air blocks as correct.");
        addSettingValue("buildInLayers", "Don't consider the next layer in builder until the current one is done");
        addSettingValue("buildOnlySelection", "Only build the selected part of schematics");
        addSettingValue("buildRepeat", "How far to move before repeating the build.");
        addSettingValue("buildRepeatCount", "How many times to buildrepeat.");
        addSettingValue("buildRepeatSneaky", "Don't notify schematics that they are moved.");
        addSettingValue("buildSkipBlocks", "A list of blocks to be treated as correct.");
        addSettingValue("buildSubstitutes", "A mapping of blocks to blocks to be built instead");
        addSettingValue("buildValidSubstitutes", "A mapping of blocks to blocks treated as correct in their position.");
        addSettingValue("cachedChunksExpirySeconds", "Cached chunks (regardless of if they're in RAM or saved to disk) expire and are deleted after this number of seconds -1 to disable");
        addSettingValue("cachedChunksOpacity", "0.0f = not visible, fully transparent (instead of setting this to 0, turn off renderCachedChunks) 1.0f = fully opaque");
        addSettingValue("cancelOnGoalInvalidation", "Cancel the current path if the goal has changed, and the path originally ended in the goal but doesn't anymore.");
        addSettingValue("censorCoordinates", "Censor coordinates in goals and block positions");
        addSettingValue("censorRanCommands", "Censor arguments to ran commands, to hide, for example, coordinates to #goal");
        addSettingValue("chatControl", "Allow chat based control of Baritone.");
        addSettingValue("chatControlAnyway", "Some clients like Impact try to force chatControl to off, so here's a second setting to do it anyway");
        addSettingValue("chatDebug", "Print all the debug messages to chat");
        addSettingValue("chunkCaching", "The big one.");
        addSettingValue("colorBestPathSoFar", "The color of the best path so far");
        addSettingValue("colorBlocksToBreak", "The color of the blocks to break");
        addSettingValue("colorBlocksToPlace", "The color of the blocks to place");
        addSettingValue("colorBlocksToWalkInto", "The color of the blocks to walk into");
        addSettingValue("colorCurrentPath", "The color of the current path");
        addSettingValue("colorGoalBox", "The color of the goal box");
        addSettingValue("colorInvertedGoalBox", "The color of the goal box when it's inverted");
        addSettingValue("colorMostRecentConsidered", "The color of the path to the most recent considered node");
        addSettingValue("colorNextPath", "The color of the next path");
        addSettingValue("colorSelection", "The color of all selections");
        addSettingValue("colorSelectionPos1", "The color of the selection pos 1");
        addSettingValue("colorSelectionPos2", "The color of the selection pos 2");
        addSettingValue("considerPotionEffects", "For example, if you have Mining Fatigue or Haste, adjust the costs of breaking blocks accordingly.");
        addSettingValue("costHeuristic", "This is the big A* setting.");
        addSettingValue("costVerificationLookahead", "Stop 5 movements before anything that made the path COST_INF.");
        addSettingValue("cutoffAtLoadBoundary", "After calculating a path (potentially through cached chunks), artificially cut it off to just the part that is entirely within currently loaded chunks.");
        addSettingValue("desktopNotifications", "Desktop notifications");
        addSettingValue("disableCompletionCheck", "Turn this on if your exploration filter is enormous, you don't want it to check if it's done, and you are just fine with it just hanging on completion");
        addSettingValue("disconnectOnArrival", "Disconnect from the server upon arriving at your goal");
        addSettingValue("distanceTrim", "Trim incorrect positions too far away, helps performance but hurts reliability in very large schematics");
        addSettingValue("doBedWaypoints", "Allows baritone to save bed waypoints when interacting with beds");
        addSettingValue("doDeathWaypoints", "Allows baritone to save death waypoints");
        addSettingValue("echoCommands", "Echo commands to chat when they are run");
        addSettingValue("enterPortal", "When running a goto towards a nether portal block, walk all the way into the portal instead of stopping one block before.");
        addSettingValue("exploreChunkSetMinimumSize", "Take the 10 closest chunks, even if they aren't strictly tied for distance metric from origin.");
        addSettingValue("exploreForBlocks", "When GetToBlock or non-legit Mine doesn't know any locations for the desired block, explore randomly instead of giving up.");
        addSettingValue("exploreMaintainY", "Attempt to maintain Y coordinate while exploring");
        addSettingValue("extendCacheOnThreshold", "When the cache scan gives less blocks than the maximum threshold (but still above zero), scan the main world too.");
        addSettingValue("fadePath", "Start fading out the path at 20 movements ahead, and stop rendering it entirely 30 movements ahead.");
        addSettingValue("failureTimeoutMS", "Pathing can never take longer than this, even if that means failing to find any path at all");
        addSettingValue("followOffsetDirection", "The actual GoalNear is set in this direction from the entity you're following.");
        addSettingValue("followOffsetDistance", "The actual GoalNear is set this distance away from the entity you're following");
        addSettingValue("followRadius", "The radius (for the GoalNear) of how close to your target position you actually have to be");
        addSettingValue("forceInternalMining", "When mining block of a certain type, try to mine two at once instead of one.");
        addSettingValue("freeLook", "Move without having to force the client-sided rotations");
        addSettingValue("goalBreakFromAbove", "As well as breaking from above, set a goal to up and to the side of all blocks to break.");
        addSettingValue("goalRenderLineWidthPixels", "Line width of the goal when rendered, in pixels");
        addSettingValue("incorrectSize", "The set of incorrect blocks can never grow beyond this size");
        addSettingValue("internalMiningAirException", "Modification to the previous setting, only has effect if forceInternalMining is true If true, only apply the previous setting if the block adjacent to the goal isn't air.");
        addSettingValue("itemSaver", "Stop using tools just before they are going to break.");
        addSettingValue("itemSaverThreshold", "Durability to leave on the tool when using itemSaver");
        addSettingValue("jumpPenalty", "Additional penalty for hitting the space bar (ascend, pillar, or parkour) because it uses hunger");
        addSettingValue("layerHeight", "How high should the individual layers be?");
        addSettingValue("layerOrder", "false = build from bottom to top");
        addSettingValue("legitMine", "Disallow MineBehavior from using X-Ray to see where the ores are.");
        addSettingValue("legitMineIncludeDiagonals", "Magically see ores that are separated diagonally from existing ores.");
        addSettingValue("legitMineYLevel", "What Y level to go to for legit strip mining");
        addSettingValue("logAsToast", "Shows popup message in the upper right corner, similarly to when you make an advancement");
        addSettingValue("mapArtMode", "Build in map art mode, which makes baritone only care about the top block in each column");
        addSettingValue("maxCachedWorldScanCount", "After finding this many instances of the target block in the cache, it will stop expanding outward the chunk search.");
        addSettingValue("maxCostIncrease", "If a movement's cost increases by more than this amount between calculation and execution (due to changes in the environment / world), cancel and recalculate");
        addSettingValue("maxFallHeightBucket", "How far are you allowed to fall onto solid ground (with a water bucket)? It's not that reliable, so I've set it below what would kill an unarmored player (23)");
        addSettingValue("maxFallHeightNoWater", "How far are you allowed to fall onto solid ground (without a water bucket)? 3 won't deal any damage.");
        addSettingValue("maxPathHistoryLength", "If we are more than 300 movements into the current path, discard the oldest segments, as they are no longer useful");
        addSettingValue("mineDropLoiterDurationMSThanksLouca", "While mining, wait this number of milliseconds after mining an ore to see if it will drop an item instead of immediately going onto the next one");
        addSettingValue("mineGoalUpdateInterval", "Rescan for the goal once every 5 ticks.");
        addSettingValue("mineScanDroppedItems", "While mining, should it also consider dropped items of the correct type as a pathing destination (as well as ore blocks)?");
        addSettingValue("minimumImprovementRepropagation", "Don't repropagate cost improvements below 0.01 ticks.");
        addSettingValue("minYLevelWhileMining", "Sets the minimum y level whilst mining - set to 0 to turn off. if world has negative y values, subtract the min world height to get the value to put here");
        addSettingValue("mobAvoidanceCoefficient", "Set to 1.0 to effectively disable this feature");
        addSettingValue("mobAvoidanceRadius", "Distance to avoid mobs.");
        addSettingValue("mobSpawnerAvoidanceCoefficient", "Set to 1.0 to effectively disable this feature");
        addSettingValue("mobSpawnerAvoidanceRadius", "Distance to avoid mob spawners.");
        addSettingValue("movementTimeoutTicks", "If a movement takes this many ticks more than its initial cost estimate, cancel it");
        addSettingValue("notificationOnBuildFinished", "Desktop notification on build finished");
        addSettingValue("notificationOnExploreFinished", "Desktop notification on explore finished");
        addSettingValue("notificationOnFarmFail", "Desktop notification on farm fail");
        addSettingValue("notificationOnMineFail", "Desktop notification on mine fail");
        addSettingValue("notificationOnPathComplete", "Desktop notification on path complete");
        addSettingValue("notifier", "The function that is called when Baritone will send a desktop notification.");
        addSettingValue("okIfAir", "A list of blocks to become air");
        addSettingValue("okIfWater", "Override builder's behavior to not attempt to correct blocks that are currently water");
        addSettingValue("overshootTraverse", "If we overshoot a traverse and end up one block beyond the destination, mark it as successful anyway.");
        addSettingValue("pathCutoffFactor", "Static cutoff factor.");
        addSettingValue("pathCutoffMinimumLength", "Only apply static cutoff for paths of at least this length (in terms of number of movements)");
        addSettingValue("pathHistoryCutoffAmount", "If the current path is too long, cut off this many movements from the beginning.");
        addSettingValue("pathingMapDefaultSize", "Default size of the Long2ObjectOpenHashMap used in pathing");
        addSettingValue("pathingMapLoadFactor", "Load factor coefficient for the Long2ObjectOpenHashMap used in pathing");
        addSettingValue("pathingMaxChunkBorderFetch", "The maximum number of times it will fetch outside loaded or cached chunks before assuming that pathing has reached the end of the known area, and should therefore stop.");
        addSettingValue("pathRenderLineWidthPixels", "Line width of the path when rendered, in pixels");
        addSettingValue("pathThroughCachedOnly", "Exclusively use cached chunks for pathing");
        addSettingValue("pauseMiningForFallingBlocks", "When breaking blocks for a movement, wait until all falling blocks have settled before continuing");
        addSettingValue("planAheadFailureTimeoutMS", "Planning ahead while executing a segment can never take longer than this, even if that means failing to find any path at all");
        addSettingValue("planAheadPrimaryTimeoutMS", "Planning ahead while executing a segment ends after this amount of time, but only if a path has been found");
        addSettingValue("planningTickLookahead", "Start planning the next path once the remaining movements tick estimates sum up to less than this value");
        addSettingValue("preferSilkTouch", "Always prefer silk touch tools over regular tools.");
        addSettingValue("prefix", "The command prefix for chat control");
        addSettingValue("prefixControl", "Whether or not to allow you to run Baritone commands with the prefix");
        addSettingValue("primaryTimeoutMS", "Pathing ends after this amount of time, but only if a path has been found");
        addSettingValue("pruneRegionsFromRAM", "On save, delete from RAM any cached regions that are more than 1024 blocks away from the player");
        addSettingValue("randomLooking", "How many degrees to randomize the pitch and yaw every tick.");
        addSettingValue("randomLooking113", "How many degrees to randomize the yaw every tick. Set to 0 to disable.");
        addSettingValue("renderCachedChunks", "Render cached chunks as semitransparent.");
        addSettingValue("renderGoal", "Render goal", "Render the goal.");
        addSettingValue("renderGoalAnimated", "Render the goal as a sick animated thingy instead of just a box (also controls animation of Goal X Z if R ender goal X Z beacon is enabled).");
        addSettingValue("renderGoalIgnoreDepth", "Ignore depth when rendering the goal.");
        addSettingValue("renderGoalXZBeacon", "Render goal X Z beacon", "Render X/Z type Goals with the vanilla beacon beam effect.");
        addSettingValue("renderPath", "Render path", "Render the path.");
        addSettingValue("renderPathAsLine", "Render the path as a line instead of a frickin thingy.");
        addSettingValue("renderPathIgnoreDepth", "Ignore depth when rendering the path.");
        addSettingValue("renderSelection", "Render selections.");
        addSettingValue("renderSelectionBoxes", "Render selection boxes.");
        addSettingValue("renderSelectionBoxesIgnoreDepth", "Ignore depth when rendering the selection boxes (to break, to place, to walk into).");
        addSettingValue("renderSelectionCorners", "Render selection corners.");
        addSettingValue("renderSelectionIgnoreDepth", "Ignore depth when rendering selections.");
        addSettingValue("repackOnAnyBlockChange", "Whenever a block changes, repack the whole chunk that it's in.");
        addSettingValue("replantCrops", "Replant normal Crops while farming and leave cactus and sugarcane to regrow.");
        addSettingValue("replantNetherWart", "Replant nether wart while farming.");
        addSettingValue("rightClickContainerOnArrival", "When running a goto towards a container block (chest, ender chest, furnace, etc), right click and open it once you arrive.");
        addSettingValue("rightClickSpeed", "How many ticks between right clicks are allowed.");
        addSettingValue("schematicFallbackExtension", "The fallback used by the build command when no extension is specified.");
        addSettingValue("schematicOrientationX", "When this setting is true, build a schematic with the highest X coordinate being the origin, instead of the lowest.");
        addSettingValue("schematicOrientationY", "When this setting is true, build a schematic with the highest Y coordinate being the origin, instead of the lowest.");
        addSettingValue("schematicOrientationZ", "When this setting is true, build a schematic with the highest Z coordinate being the origin, instead of the lowest.");
        addSettingValue("selectionLineWidth", "Line width of the goal when rendered, in pixels.");
        addSettingValue("selectionOpacity", "The opacity of the selection.");
        addSettingValue("shortBaritonePrefix", "Use a short Baritone prefix [B] instead of [Baritone] when logging to chat.");
        addSettingValue("simplifyUnloadedYCoord", "If your goal is a GoalBlock in an unloaded chunk, assume it's far enough away that the Y coord doesn't matter yet, and replace it with a GoalXZ to the same place before calculating a path.");
        addSettingValue("skipFailedLayers", "If a layer is unable to be constructed, just skip it.");
        addSettingValue("slowPath", "For debugging, consider nodes much much slower.");
        addSettingValue("slowPathTimeDelayMS", "Milliseconds between each node.");
        addSettingValue("slowPathTimeoutMS", "The alternative timeout number when slowPath is on.");
        addSettingValue("splicePath", "When a new segment is calculated that doesn't overlap with the current one, but simply begins where the current segment ends, splice it on and make a longer combined path.");
        addSettingValue("sprintAscends", "Sprint and jump a block early on ascends wherever possible.");
        addSettingValue("sprintInWater", "Continue sprinting while in water.");
        addSettingValue("startAtLayer", "Start building the schematic at a specific layer.");
        addSettingValue("toaster", "The function that is called when Baritone will show a toast.");
        addSettingValue("toastTimer", "The time of how long the message in the pop-up will display");
        addSettingValue("useSwordToMine", "Use sword to mine.");
        addSettingValue("verboseCommandExceptions", "Print out ALL command exceptions as a stack trace to stdout, even simple syntax errors");
        addSettingValue("walkOnWaterOnePenalty", "Walking on water uses up hunger really quick, so penalize it");
        addSettingValue("walkWhileBreaking", "Don't stop walking forward when you need to break blocks in your way");
        addSettingValue("worldExploringChunkOffset", "While exploring the world, offset the closest unloaded chunk by this much in both axes.");
        addSettingValue("yLevelBoxSize", "Y level box size", "The size of the box that is rendered when the current goal is a GoalYLevel");
    */}
}
