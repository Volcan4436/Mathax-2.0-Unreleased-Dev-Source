package xyz.mathax.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import xyz.mathax.client.renderer.text.Fonts;
import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.systems.commands.Command;
import xyz.mathax.client.systems.enemies.Enemies;
import xyz.mathax.client.systems.enemies.Enemy;
import xyz.mathax.client.systems.friends.Friend;
import xyz.mathax.client.systems.friends.Friends;
import xyz.mathax.client.utils.network.Capes;
import xyz.mathax.client.utils.network.Executor;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("Reload", "Reloads the config, modules, theme, friends, enemies, macros, accounts, capes and fonts.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            warning("Reloading all systems, fonts and capes. This may take a while...");

            Systems.load();
            Capes.refresh();
            Fonts.refresh();
            Executor.execute(() -> {
                Friends.get().forEach(Friend::updateInfo);
                Enemies.get().forEach(Enemy::updateInfo);
            });

            info("Reloaded all systems, capes and fonts.");

            return SINGLE_SUCCESS;
        }).then(literal("capes").executes(context -> {

            Capes.refresh();

            info("Reloaded capes.");

            return SINGLE_SUCCESS;
        })).then(literal("fonts").executes(context -> {
            Fonts.refresh();

            info("Reloaded fonts.");

            return SINGLE_SUCCESS;
        }));
    }
}
