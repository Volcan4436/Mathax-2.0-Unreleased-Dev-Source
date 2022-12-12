package xyz.mathax.mathaxclient.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.systems.commands.Command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class IrcCommand extends Command {
    public IrcCommand() {
        super("IRC", "Connects to the IRC server.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("connect").executes(context -> {
            MatHax.API.irc.forceToggle(true);
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("disconnect").executes(context -> {
            MatHax.API.irc.forceToggle(false);
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("auth").then(argument("username", StringArgumentType.string()).then(argument("password", StringArgumentType.string()).executes(context -> {
            MatHax.API.irc.setAuth(StringArgumentType.getString(context, "username"), StringArgumentType.getString(context, "password"));
            return SINGLE_SUCCESS;
        }))).then(literal("clear").executes(context -> {
            MatHax.API.irc.setAuth("", "");
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("send").then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            MatHax.API.irc.send(context.getArgument("message", String.class));
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("sendDirect").then(argument("user", StringArgumentType.string()).then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            MatHax.API.irc.sendDirect(StringArgumentType.getString(context, "user"), context.getArgument("message", String.class));
            return SINGLE_SUCCESS;
        }))));
    }
}