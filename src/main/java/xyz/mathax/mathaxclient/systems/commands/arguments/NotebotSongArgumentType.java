package xyz.mathax.mathaxclient.systems.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.notebot.decoder.SongDecoders;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class NotebotSongArgumentType implements ArgumentType<Path> {
    public static NotebotSongArgumentType create() {
        return new NotebotSongArgumentType();
    }

    @Override
    public Path parse(StringReader reader) throws CommandSyntaxException {
        String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        System.out.println("READER: " + text);
        return MatHax.FOLDER.toPath().resolve("notebot/" + text);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            return CommandSource.suggestMatching(Files.list(MatHax.FOLDER.toPath().resolve("Notebot")).filter(SongDecoders::hasDecoder).map(path -> path.getFileName().toString()), builder);
        } catch (IOException exception) {
            return Suggestions.empty();
        }
    }
}