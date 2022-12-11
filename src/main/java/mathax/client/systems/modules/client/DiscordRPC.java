package mathax.client.systems.modules.client;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.IntSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import mathax.client.utils.network.versions.Versions;
import mathax.client.utils.player.PlayerUtils;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;

public class DiscordRPC extends Module {
    private static final RichPresence rpc = new RichPresence();

    private int ticks;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Integer> updateDelaySetting = generalSettings.add(new IntSetting.Builder()
            .name("Update delay")
            .description("How fast to update the first line in ticks.")
            .defaultValue(100)
            .min(10)
            .sliderRange(10, 200)
            .build()
    );

    public DiscordRPC(Category category) {
        super(category, "Discord RPC", "Shows MatHax as your Discord status.", true);
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
            rpc.setDetails(getVersions() + " | " + getUsername() + (mc.world != null && mc.player != null ? " | " + getHealth() : ""));
            rpc.setLargeImage("mathax", "MatHax " + getVersions());
            rpc.setState(mc.world != null ? "Playing on " + Utils.getWorldName() + " (" + (mc.isInSingleplayer() ? "Singleplayer" : "Multiplayer") + ")" : null);

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
        return /*Modules.get().get(NameProtect.class).getName(*/mc.getSession().getUsername()/*)*/;
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

    @Override
    public void onDisable() {
        DiscordIPC.stop();
    }
}