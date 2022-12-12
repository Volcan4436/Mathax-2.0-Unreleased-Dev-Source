package xyz.mathax.mathaxclient.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import xyz.mathax.mathaxclient.systems.commands.Command;
import xyz.mathax.mathaxclient.utils.network.irc.IrcClient;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class IrcCommand extends Command {
    public IrcCommand() {
        super("IRC", "Connects to the IRC server.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("connect").executes(context -> {
            IrcClient.connect();
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("disconnect").executes(context -> {
            IrcClient.disconnect();
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("auth").then(argument("username", StringArgumentType.string()).then(argument("password", StringArgumentType.string()).executes(context -> {
            IrcClient.setAuth(StringArgumentType.getString(context, "username"), StringArgumentType.getString(context, "password"));
            return SINGLE_SUCCESS;
        }))).then(literal("clear").executes(context -> {
            IrcClient.setAuth("", "");
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("send").then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            try {
                IrcClient.send(context.getArgument("message", String.class));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("sendDirect").then(argument("user", StringArgumentType.string()).then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            try {
                IrcClient.sendDirect(StringArgumentType.getString(context, "user"), context.getArgument("message", String.class));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return SINGLE_SUCCESS;
        }))));
    }
}