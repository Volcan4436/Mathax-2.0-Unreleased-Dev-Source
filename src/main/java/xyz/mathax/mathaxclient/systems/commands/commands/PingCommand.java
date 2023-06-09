package xyz.mathax.mathaxclient.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import xyz.mathax.mathaxclient.systems.commands.Command;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PingCommand extends Command {
    public PingCommand() {
        super("Ping", "Shows your ping.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String ping;
            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (playerListEntry != null) {
                ping = Integer.toString(playerListEntry.getLatency());
            } else {
                ping = "0";
            }

            info("Your ping is (highlight)%s(default).", ping);

            return SINGLE_SUCCESS;
        });
    }
}
