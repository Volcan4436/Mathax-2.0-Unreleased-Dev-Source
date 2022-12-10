package mathax.client.utils.entity.fakeplayer;

import mathax.client.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static mathax.client.MatHax.mc;

public class FakePlayerManager {
    private static final List<FakePlayerEntity> ENTITIES = new ArrayList<>();

    public static FakePlayerEntity get(String name) {
        for (FakePlayerEntity fakePlayer : ENTITIES) {
            if (fakePlayer.getEntityName().equals(name)) {
                return fakePlayer;
            }
        }

        return null;
    }

    public static void add(String name, float health, boolean copyInv) {
        if (!Utils.canUpdate()) {
            return;
        }

        FakePlayerEntity fakePlayer = new FakePlayerEntity(mc.player, name, health, copyInv);
        fakePlayer.spawn();

        ENTITIES.add(fakePlayer);
    }

    public static void remove(FakePlayerEntity fakePlayer) {
        ENTITIES.removeIf(fakePlayer1 -> {
            if (fakePlayer.getEntityName().equals(fakePlayer.getEntityName())) {
                fakePlayer.despawn();
                return true;
            }

            return false;
        });
    }

    public static void clear() {
        if (ENTITIES.isEmpty()) {
            return;
        }

        ENTITIES.forEach(FakePlayerEntity::despawn);
        ENTITIES.clear();
    }

    public static void forEach(Consumer<FakePlayerEntity> action) {
        for (FakePlayerEntity fakePlayer : ENTITIES) {
            action.accept(fakePlayer);
        }
    }

    public static int count() {
        return ENTITIES.size();
    }

    public static Stream<FakePlayerEntity> stream() {
        return ENTITIES.stream();
    }

    public static boolean contains(FakePlayerEntity fakePlayer) {
        return ENTITIES.contains(fakePlayer);
    }
}
