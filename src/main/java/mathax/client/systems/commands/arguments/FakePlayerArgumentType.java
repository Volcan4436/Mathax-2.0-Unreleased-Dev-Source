package mathax.client.systems.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mathax.client.utils.entity.fakeplayer.FakePlayerEntity;
import mathax.client.utils.entity.fakeplayer.FakePlayerManager;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FakePlayerArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = List.of("Matejko06");

    public static FakePlayerArgumentType create() {
        return new FakePlayerArgumentType();
    }

    public static FakePlayerEntity get(CommandContext<?> context) {
        return FakePlayerManager.get(context.getArgument("fakeplayer", String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(FakePlayerManager.stream().map(FakePlayerEntity::getEntityName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}