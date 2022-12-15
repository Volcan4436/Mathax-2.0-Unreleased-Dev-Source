package xyz.mathax.mathaxclient.systems.modules.render.blockesp;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import xyz.mathax.mathaxclient.eventbus.EventHandler;
import xyz.mathax.mathaxclient.events.render.Render3DEvent;
import xyz.mathax.mathaxclient.events.world.BlockUpdateEvent;
import xyz.mathax.mathaxclient.events.world.ChunkDataEvent;
import xyz.mathax.mathaxclient.events.world.TickEvent;
import xyz.mathax.mathaxclient.renderer.ShapeMode;
import xyz.mathax.mathaxclient.settings.*;
import xyz.mathax.mathaxclient.systems.modules.Category;
import xyz.mathax.mathaxclient.systems.modules.Module;
import xyz.mathax.mathaxclient.utils.Utils;
import xyz.mathax.mathaxclient.utils.misc.UnorderedArrayList;
import xyz.mathax.mathaxclient.utils.network.Executor;
import xyz.mathax.mathaxclient.utils.player.PlayerUtils;
import xyz.mathax.mathaxclient.utils.render.color.RainbowColors;
import xyz.mathax.mathaxclient.utils.render.color.SettingColor;
import xyz.mathax.mathaxclient.utils.world.Dimension;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BlockESP extends Module {
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    private final Long2ObjectMap<SChunk> chunks = new Long2ObjectOpenHashMap<>();
    private final List<SGroup> groups = new UnorderedArrayList<>();

    private Dimension lastDimension;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<List<Block>> blocksSetting = generalSettings.add(new BlockListSetting.Builder()
            .name("Blocks")
            .description("Blocks to highlight.")
            .onChanged(blocks1 -> {
                if (isEnabled() && Utils.canUpdate()) {
                    onEnable();
                }
            })
            .build()
    );

    private final Setting<SBlockData> defaultBlockConfigSetting = generalSettings.add(new GenericSetting.Builder<SBlockData>()
            .name("Default block config")
            .description("Default config for blocks.")
            .defaultValue(new SBlockData(ShapeMode.Lines, new SettingColor(0, 255, 200), new SettingColor(0, 255, 200, 25), true, new SettingColor(0, 255, 200, 125)))
            .build()
    );

    private final Setting<Map<Block, SBlockData>> blockConfigsSetting = generalSettings.add(new BlockDataSetting.Builder<SBlockData>()
            .name("Block configs")
            .description("Config for each block.")
            .defaultData(defaultBlockConfigSetting)
            .build()
    );

    private final Setting<Boolean> tracerSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Tracers")
            .description("Render tracer lines.")
            .defaultValue(false)
            .build()
    );

    public BlockESP(Category category) {
        super(category, "Block ESP", "Highlights for specified blocks.");

        RainbowColors.register(this::onTickRainbow);
    }

    @Override
    public void onEnable() {
        synchronized (chunks) {
            chunks.clear();
            groups.clear();
        }

        for (Chunk chunk : Utils.chunks()) {
            searchChunk(chunk, null);
        }

        lastDimension = PlayerUtils.getDimension();
    }

    @Override
    public void onDisable() {
        synchronized (chunks) {
            chunks.clear();
            groups.clear();
        }
    }

    private void onTickRainbow() {
        if (!isEnabled()) {
            return;
        }

        defaultBlockConfigSetting.get().tickRainbow();

        for (SBlockData blockData : blockConfigsSetting.get().values()) {
            blockData.tickRainbow();
        }
    }

    SBlockData getBlockData(Block block) {
        SBlockData blockData = blockConfigsSetting.get().get(block);
        return blockData == null ? defaultBlockConfigSetting.get() : blockData;
    }

    private void updateChunk(int x, int z) {
        SChunk chunk = chunks.get(ChunkPos.toLong(x, z));
        if (chunk != null) {
            chunk.update();
        }
    }

    private void updateBlock(int x, int y, int z) {
        SChunk chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
        if (chunk != null) {
            chunk.update(x, y, z);
        }
    }

    public SBlock getBlock(int x, int y, int z) {
        SChunk chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
        return chunk == null ? null : chunk.get(x, y, z);
    }

    public SGroup newGroup(Block block) {
        synchronized (chunks) {
            SGroup group = new SGroup(block);
            groups.add(group);
            return group;
        }
    }

    public void removeGroup(SGroup group) {
        synchronized (chunks) {
            groups.remove(group);
        }
    }

    @EventHandler
    private void onChunkData(ChunkDataEvent event) {
        searchChunk(event.chunk, event);
    }

    private void searchChunk(Chunk chunk, ChunkDataEvent event) {
        Executor.execute(() -> {
            if (!isEnabled()) {
                return;
            }

            SChunk schunk = SChunk.searchChunk(chunk, blocksSetting.get());
            if (schunk.size() > 0) {
                synchronized (chunks) {
                    chunks.put(chunk.getPos().toLong(), schunk);
                    schunk.update();

                    updateChunk(chunk.getPos().x - 1, chunk.getPos().z);
                    updateChunk(chunk.getPos().x + 1, chunk.getPos().z);
                    updateChunk(chunk.getPos().x, chunk.getPos().z - 1);
                    updateChunk(chunk.getPos().x, chunk.getPos().z + 1);
                }
            }

            if (event != null) {
                ChunkDataEvent.returnChunkDataEvent(event);
            }
        });
    }

    @EventHandler
    private void onBlockUpdate(BlockUpdateEvent event) {
        // Minecraft probably reuses the event.pos BlockPos instance because it causes problems when trying to use it inside another thread
        int bx = event.pos.getX();
        int by = event.pos.getY();
        int bz = event.pos.getZ();
        int chunkX = bx >> 4;
        int chunkZ = bz >> 4;
        long key = ChunkPos.toLong(chunkX, chunkZ);
        boolean added = blocksSetting.get().contains(event.newState.getBlock()) && !blocksSetting.get().contains(event.oldState.getBlock());
        boolean removed = !added && !blocksSetting.get().contains(event.newState.getBlock()) && blocksSetting.get().contains(event.oldState.getBlock());
        if (added || removed) {
            Executor.execute(() -> {
                synchronized (chunks) {
                    SChunk chunk = chunks.get(key);
                    if (chunk == null) {
                        chunk = new SChunk(chunkX, chunkZ);
                        if (chunk.shouldBeDeleted()) {
                            return;
                        }

                        chunks.put(key, chunk);
                    }

                    blockPos.set(bx, by, bz);

                    if (added) {
                        chunk.add(blockPos);
                    } else {
                        chunk.remove(blockPos);
                    }

                    for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            for (int y = -1; y < 2; y++) {
                                if (x == 0 && y == 0 && z == 0) {
                                    continue;
                                }

                                updateBlock(bx + x, by + y, bz + z);
                            }
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    private void onPostTick(TickEvent.Post event) {
        Dimension dimension = PlayerUtils.getDimension();
        if (lastDimension != dimension) {
            onEnable();
        }

        lastDimension = dimension;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        synchronized (chunks) {
            for (Iterator<SChunk> it = chunks.values().iterator(); it.hasNext();) {
                SChunk chunk = it.next();
                if (chunk.shouldBeDeleted()) {
                    Executor.execute(() -> {
                        for (SBlock block : chunk.blocks.values()) {
                            block.group.remove(block, false);
                            block.loaded = false;
                        }
                    });

                    it.remove();
                } else {
                    chunk.render(event);
                }
            }

            if (tracerSetting.get()) {
                for (Iterator<SGroup> it = groups.iterator(); it.hasNext();) {
                    SGroup group = it.next();
                    if (group.blocks.isEmpty()) {
                        it.remove();
                    } else {
                        group.render(event);
                    }
                }
            }
        }
    }
}
