package mathax.client.systems.modules.world;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.packets.PacketEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TimeChanger extends Module {
    private long oldTime;

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Double> timeSetting = generalSettings.add(new DoubleSetting.Builder()
            .name("time")
            .description("The specified time to be set.")
            .defaultValue(0)
            .sliderRange(-20000, 20000)
            .build()
    );

    public TimeChanger(Category category) {
        super(category, "Time Changer", "Makes you able to set a custom time.");
    }

    @Override
    public void onEnable() {
        oldTime = mc.world.getTime();
    }

    @Override
    public void onDisable() {
        mc.world.setTimeOfDay(oldTime);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            oldTime = ((WorldTimeUpdateS2CPacket) event.packet).getTime();
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.world.setTimeOfDay(timeSetting.get().longValue());
    }
}