package xyz.mathax.client.utils.notebot.decoder;

import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.modules.misc.Notebot;
import xyz.mathax.client.utils.notebot.song.Song;

import java.io.File;

public abstract class SongDecoder {
    protected Notebot notebot = Modules.get().get(Notebot.class);

    public abstract Song parse(File file);
}