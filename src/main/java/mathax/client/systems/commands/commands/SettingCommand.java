package mathax.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.gui.WidgetScreen;
import mathax.client.settings.Setting;
import mathax.client.systems.commands.Command;
import mathax.client.systems.commands.arguments.ModuleArgumentType;
import mathax.client.systems.commands.arguments.SettingArgumentType;
import mathax.client.systems.commands.arguments.SettingValueArgumentType;
import mathax.client.systems.modules.Module;
import mathax.client.systems.themes.Themes;
import mathax.client.utils.Utils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SettingCommand extends Command {
    public SettingCommand() {
        super("Settings", "Allows you to view and change module settings.", "s");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                argument("module", ModuleArgumentType.create()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    WidgetScreen screen = Themes.getTheme().moduleScreen(module);
                    screen.parent = null;

                    Utils.screenToOpen = screen;
                    return SINGLE_SUCCESS;
                }).then(argument("setting", SettingArgumentType.create()).executes(context -> {
                    Setting<?> setting = SettingArgumentType.get(context);
                    ModuleArgumentType.get(context).info("Setting (highlight)%s(default) is (highlight)%s(default).", setting.name, setting.get());

                    return SINGLE_SUCCESS;
                }).then(argument("value", SettingValueArgumentType.create()).executes(context -> {
                    Setting<?> setting = SettingArgumentType.get(context);
                    String value = context.getArgument("value", String.class);
                    if (setting.parse(value)) {
                        ModuleArgumentType.get(context).info("Setting (highlight)%s(default) changed to (highlight)%s(default).", setting.name, value);
                    }

                    return SINGLE_SUCCESS;
                })))
        );
    }
}
