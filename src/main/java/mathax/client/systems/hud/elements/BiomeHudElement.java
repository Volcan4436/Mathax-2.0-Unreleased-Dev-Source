package mathax.client.systems.hud.elements;

import mathax.client.systems.hud.DoubleTextHudElement;
import mathax.client.systems.hud.Hud;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BiomeHudElement extends DoubleTextHudElement {
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    public BiomeHudElement(Hud hud) {
        super(hud, "Biome", "Displays the biome you are in.");
    }

    @Override
    protected String getLeft() {
        return name + ": ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            return "Plains";
        }

        blockPos.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        Identifier id = mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(mc.world.getBiome(blockPos).value());
        if (id == null) {
            return "Unknown";
        }

        return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
