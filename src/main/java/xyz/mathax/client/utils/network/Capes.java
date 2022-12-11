package xyz.mathax.client.utils.network;

import com.mojang.util.UUIDTypeAdapter;
import xyz.mathax.client.MatHax;
import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.world.TickEvent;
import xyz.mathax.client.systems.config.Config;
import xyz.mathax.client.utils.json.JSONUtils;
import xyz.mathax.client.utils.misc.MatHaxIdentifier;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static xyz.mathax.client.MatHax.mc;

public class Capes {
    private static final Map<String, Cape> CAPES = new HashMap<>();
    private static final Map<UUID, Cape> OWNERS = new HashMap<>();

    private static final List<Cape> TO_REGISTER = new ArrayList<>();
    private static final List<Cape> TO_RETRY = new ArrayList<>();
    private static final List<Cape> TO_REMOVE = new ArrayList<>();

    private static int timer = 0;

    public static void refresh() {
        clear();

        timer = 0;

        Executor.execute(() -> {
            String response = Http.get(MatHax.API_URL + "/capes/metadata.json").sendString();
            if (response == null) {
                return;
            }

            JSONObject json = new JSONObject(response);
            if (json.has("capes") && JSONUtils.isValidJSONArray(json, "capes")) {
                for (Object object : json.getJSONArray("capes")) {
                    if (object instanceof JSONObject capeJson) {
                        if (capeJson.has("name") && capeJson.has("url")) {
                            String name = capeJson.getString("name");
                            if (!CAPES.containsKey(name)) {
                                CAPES.put(name, new Cape(name, capeJson.getString("url")));
                            }
                        }
                    }
                }
            }

            if (json.has("players") && JSONUtils.isValidJSONArray(json, "players")) {
                for (Object object : json.getJSONArray("players")) {
                    if (object instanceof JSONObject capeOwnersJson) {
                        if (capeOwnersJson.has("cape") && capeOwnersJson.has("uuids") && JSONUtils.isValidJSONArray(capeOwnersJson, "uuids")) {
                            String capeName = capeOwnersJson.getString("cape");
                            if (!CAPES.containsKey(capeName)) {
                                continue;
                            }

                            for (Object object1 : capeOwnersJson.getJSONArray("uuids")) {
                                if (object1 instanceof String stringUuid) {
                                    OWNERS.put(UUIDTypeAdapter.fromString(stringUuid), CAPES.get(capeName));
                                }
                            }
                        }
                    }
                }
            }
        });

        MatHax.EVENT_BUS.subscribe(Capes.class);
    }

    public static void clear() {
        CAPES.clear();
        OWNERS.clear();
        TO_REGISTER.clear();
        TO_RETRY.clear();
        TO_REMOVE.clear();
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        synchronized (TO_REGISTER) {
            for (Cape cape : TO_REGISTER) {
                cape.register();
            }

            TO_REGISTER.clear();
        }

        synchronized (TO_RETRY) {
            TO_RETRY.removeIf(Cape::tick);
        }

        synchronized (TO_REMOVE) {
            for (Cape cape : TO_REMOVE) {
                CAPES.remove(cape.name, cape);
                TO_REGISTER.remove(cape);
                TO_RETRY.remove(cape);
            }

            TO_REMOVE.clear();
        }
    }

    public static Identifier get(PlayerEntity player) {
        Cape cape = OWNERS.get(player.getUuid());
        if (cape == null || !CAPES.containsKey(cape.name)) {
            return null;
        }

        if (cape.isDownloaded()) {
            return cape;
        }

        cape.download();

        return null;
    }

    public static String getPlayerCapeName(UUID uuid) {
        if (OWNERS.containsKey(uuid)) {
            return OWNERS.get(uuid).name;
        }

        return null;
    }

    public static String getPlayerCapeName(PlayerEntity player) {
        return getPlayerCapeName(player.getGameProfile().getId());
    }

    private static class Cape extends MatHaxIdentifier {
        private final String name;
        private final String url;

        private boolean downloading;
        private boolean downloaded;

        private NativeImage img;

        private int retryTimer;

        public Cape(String name, String url) {
            super("capes/" + name.toLowerCase(Locale.ROOT));

            this.name = name;
            this.url = url;
        }

        public void download() {
            if (downloaded || downloading || retryTimer > 0) {
                return;
            }

            downloading = true;

            Executor.execute(() -> {
                try {
                    InputStream in = Http.get(url).sendInputStream();
                    if (in == null) {
                        synchronized (TO_RETRY) {
                            TO_RETRY.add(this);
                            retryTimer = 10 * 20;
                            downloading = false;
                            return;
                        }
                    }

                    img = NativeImage.read(in);

                    synchronized (TO_REGISTER) {
                        TO_REGISTER.add(this);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }

        public void register() {
            mc.getTextureManager().registerTexture(this, new NativeImageBackedTexture(img));
            img = null;

            downloading = false;
            downloaded = true;
        }

        public boolean tick() {
            if (retryTimer > 0) {
                retryTimer--;
            } else {
                download();
                return true;
            }

            return false;
        }

        public boolean isDownloaded() {
            return downloaded;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!Config.get().capesSetting.get() || Config.get().capesAutoReloadDelaySetting.get() == -1) {
            return;
        }

        if (timer >= Config.get().capesAutoReloadDelaySetting.get()) {
            timer = 0;
            Capes.refresh();
        }

        timer++;
    }
}
