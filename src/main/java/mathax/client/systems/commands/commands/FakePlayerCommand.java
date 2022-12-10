package mathax.client.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.systems.commands.Command;
import mathax.client.systems.commands.arguments.FakePlayerArgumentType;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.client.FakePlayer;
import mathax.client.utils.entity.fakeplayer.FakePlayerEntity;
import mathax.client.utils.entity.fakeplayer.FakePlayerManager;
import mathax.client.utils.text.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class FakePlayerCommand extends Command {
    public FakePlayerCommand() {
        super("Fake Player", "Manages fake players that you can use for testing.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").executes(context -> {
                    FakePlayer fakePlayer = Modules.get().get(FakePlayer.class);
                    FakePlayerManager.add(fakePlayer.nameSetting.get(), fakePlayer.healthSetting.get(), fakePlayer.copyInventorySetting.get());

                    return SINGLE_SUCCESS;
                }).then(argument("name", StringArgumentType.word()).executes(context -> {
                            FakePlayer fakePlayer = Modules.get().get(FakePlayer.class);
                            FakePlayerManager.add(StringArgumentType.getString(context, "name"), fakePlayer.healthSetting.get(), fakePlayer.copyInventorySetting.get());

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("remove").then(argument("fakeplayer", FakePlayerArgumentType.create()).executes(context -> {
                            FakePlayerEntity fakePlayerEntity = FakePlayerArgumentType.get(context);
                            if (fakePlayerEntity == null || !FakePlayerManager.contains(fakePlayerEntity)) {
                                error("Couldn't find a (highlight)Fake Player(default) with that name.");

                                return SINGLE_SUCCESS;
                            }

                            FakePlayerManager.remove(fakePlayerEntity);

                            info("Removed Fake Player %s.".formatted(fakePlayerEntity.getEntityName()));

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("clear").executes(context -> {
                    FakePlayerManager.clear();
                    return SINGLE_SUCCESS;
                })
        );

        builder.then(literal("list").executes(context -> {
                    info("--- Fake Players ((highlight)%s(default)) ---", FakePlayerManager.count());
                    FakePlayerManager.forEach(fakePlayer -> ChatUtils.info("(highlight)%s".formatted(fakePlayer.getEntityName())));
                    return SINGLE_SUCCESS;
                })
        );
    }
}