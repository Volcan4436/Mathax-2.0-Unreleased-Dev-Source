package xyz.mathax.mathaxclient.events.world;

import xyz.mathax.mathaxclient.events.Cancellable;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends Cancellable {
    private static final PlaySoundEvent INSTANCE = new PlaySoundEvent();

    public SoundInstance sound;

    public static PlaySoundEvent get(SoundInstance sound) {
        INSTANCE.setCancelled(false);
        INSTANCE.sound = sound;
        return INSTANCE;
    }
}
