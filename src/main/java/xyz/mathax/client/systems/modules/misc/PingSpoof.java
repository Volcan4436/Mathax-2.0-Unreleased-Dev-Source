package xyz.mathax.client.systems.modules.misc;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.packets.PacketEvent;
import xyz.mathax.client.events.render.Render3DEvent;
import xyz.mathax.client.settings.IntSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.utils.misc.Timer;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

public class PingSpoof extends Module {
    private final Timer timer = new Timer();

    private KeepAliveC2SPacket packet;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Integer> pingSetting = generalSettings.add(new IntSetting.Builder()
            .name("Ping")
            .description("The ping to set.")
            .defaultValue(200)
            .min(0)
            .sliderRange(0, 1000)
            .build()
    );

    public PingSpoof(Category category) {
        super(category, "Ping Spoof", "Modifies your ping.");
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof KeepAliveC2SPacket && packet != event.packet && pingSetting.get() != 0) {
            packet = (KeepAliveC2SPacket) event.packet;
            event.cancel();
            timer.reset();
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (timer.passedMillis(pingSetting.get()) && packet != null) {
            mc.getNetworkHandler().sendPacket(packet);
            packet = null;
        }
    }

    @Override
    public String getInfoString() {
        return pingSetting.get() + "ms";
    }
}