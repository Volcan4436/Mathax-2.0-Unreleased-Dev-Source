package mathax.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.systems.commands.Command;
import mathax.client.systems.commands.arguments.ModuleArgumentType;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("Toggle", "Toggles a module.", "t");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("all")
                .then(literal("on").executes(context -> {
                        new ArrayList<>(Modules.get().getAll()).forEach(module -> module.forceToggle(true));

                        Hud.get().forceToggle(true);

                        return SINGLE_SUCCESS;
                })).then(literal("off").executes(context -> {
                        new ArrayList<>(Modules.get().getAll()).forEach(module -> module.forceToggle(false));

                        Hud.get().forceToggle(false);

                        return SINGLE_SUCCESS;
                    })
                )
        ).then(literal("hud")).executes(context -> {
            Hud.get().toggle();

            return SINGLE_SUCCESS;
        }).then(literal("on").executes(context -> {
            Hud.get().forceToggle(true);

            return SINGLE_SUCCESS;
        })).then(literal("off").executes(context -> {
            Hud.get().forceToggle(false);

            return SINGLE_SUCCESS;
        })).then(argument("module", ModuleArgumentType.create()).executes(context -> {
            Module module = ModuleArgumentType.get(context);
            module.toggle();
            module.sendToggled();

            return SINGLE_SUCCESS;
        }).then(literal("on").executes(context -> {
            Module module = ModuleArgumentType.get(context);
            module.forceToggle(true);
            module.sendToggled();

            return SINGLE_SUCCESS;
        })).then(literal("off").executes(context -> {
            Module module = ModuleArgumentType.get(context);
            module.forceToggle(false);
            module.sendToggled();

            return SINGLE_SUCCESS;
        })));
    }
}
