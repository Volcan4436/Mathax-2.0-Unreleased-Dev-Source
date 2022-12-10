package mathax.client.utils.notebot.decoder;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.Notebot;
import mathax.client.utils.notebot.song.Song;

import java.io.File;

public abstract class SongDecoder {
    protected Notebot notebot = Modules.get().get(Notebot.class);

    public abstract Song parse(File file);
}