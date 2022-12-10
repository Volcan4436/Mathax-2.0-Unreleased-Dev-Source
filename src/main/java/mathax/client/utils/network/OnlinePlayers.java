package mathax.client.utils.network;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.mathax.AccountSwitchedEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.init.PostInit;
import mathax.client.systems.config.Config;
import mathax.client.utils.Utils;
import mathax.client.utils.json.JSONUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mathax.client.MatHax.mc;

public class OnlinePlayers {
    //TODO: Active MatHax players on API. Icon next to their names (tab, nametags...) - Toggleable in client config.
    private static List<UUID> onlinePlayers = new ArrayList<>();

    private static boolean online = false, firstPing = true;

    private static long lastPingTime;

    @PostInit
    public static void init() {
        MatHax.EVENT_BUS.subscribe(OnlinePlayers.class);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        if (!Config.get().onlineSetting.get()) {
            if (online) {
                leave();
            }

            return;
        }

        long time = Utils.getCurrentTimeMillis();
        if (time - lastPingTime > 5000 || firstPing) {
            Executor.execute(() -> Http.post(MatHax.API_URL + "/online/ping?uuid=" + mc.getSession().getProfile().getId()).send());

            Executor.execute(() -> {
                String response = Http.get(MatHax.API_URL + "/online/players").sendString();
                if (response == null) {
                    return;
                }

                JSONObject json = new JSONObject(response);
                if (json.has("online-players") && JSONUtils.isValidJSONArray(json, "online-players")) {
                    for (Object object : json.getJSONArray("online-players")) {
                        if (object instanceof String onlinePlayerUuid) {
                            onlinePlayers.add(UUID.fromString(onlinePlayerUuid));
                        }
                    }
                }
            });

            online = true;
            firstPing = false;

            lastPingTime = Utils.getCurrentTimeMillis();
        }
    }

    @EventHandler
    private static void onAccountSwitched(AccountSwitchedEvent event) {
        leave();
    }

    public static void leave() {
        Executor.execute(() -> Http.post(MatHax.API_URL + "/online/leave?uuid=" + mc.getSession().getProfile().getId()).send());
        online = false;
        firstPing = true;
    }

    public static boolean isOnline() {
        return online;
    }

    public static boolean isPlayerOnline(UUID uuid) {
        return onlinePlayers.contains(uuid);
    }
}
