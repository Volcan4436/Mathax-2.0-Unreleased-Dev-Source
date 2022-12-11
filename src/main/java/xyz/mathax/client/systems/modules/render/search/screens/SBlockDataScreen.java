package xyz.mathax.client.systems.modules.render.search.screens;

import net.minecraft.block.Block;
import xyz.mathax.client.gui.WindowScreen;
import xyz.mathax.client.renderer.ShapeMode;
import xyz.mathax.client.settings.*;
import xyz.mathax.client.systems.modules.render.search.SBlockData;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.utils.render.color.SettingColor;

public class SBlockDataScreen extends WindowScreen {
    private final SBlockData blockData;
    private final Block block;

    private final BlockDataSetting<SBlockData> setting;

    public SBlockDataScreen(Theme theme, SBlockData blockData, Block block, BlockDataSetting<SBlockData> setting) {
        super(theme, "Configure Block");

        this.blockData = blockData;
        this.block = block;
        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        Settings settings = new Settings();
        
        SettingGroup generalSettings = settings.createGroup("General");
        SettingGroup tracerSettings = settings.createGroup("Tracer");

        generalSettings.add(new EnumSetting.Builder<ShapeMode>()
                .name("Shape mode")
                .description("How the shape is rendered.")
                .defaultValue(ShapeMode.Lines)
                .onModuleEnabled(shapeModeSetting -> shapeModeSetting.set(blockData.shapeMode))
                .onChanged(shapeMode -> {
                    blockData.shapeMode = shapeMode;
                    changed(blockData, block, setting);
                })
                .build()
        );

        generalSettings.add(new ColorSetting.Builder()
                .name("Line color")
                .description("Color of lines.")
                .defaultValue(new SettingColor(0, 255, 200))
                .onModuleEnabled(settingColorSetting -> settingColorSetting.set(blockData.lineColor))
                .onChanged(settingColor -> {
                    blockData.lineColor.set(settingColor);
                    changed(blockData, block, setting);
                })
                .build()
        );

        generalSettings.add(new ColorSetting.Builder()
                .name("Side color")
                .description("Color of sides.")
                .defaultValue(new SettingColor(0, 255, 200, 25))
                .onModuleEnabled(settingColorSetting -> settingColorSetting.set(blockData.sideColor))
                .onChanged(settingColor -> {
                    blockData.sideColor.set(settingColor);
                    changed(blockData, block, setting);
                })
                .build()
        );

        tracerSettings.add(new BoolSetting.Builder()
                .name("Tracer")
                .description("If tracer line is allowed to this block.")
                .defaultValue(true)
                .onModuleEnabled(booleanSetting -> booleanSetting.set(blockData.tracer))
                .onChanged(aBoolean -> {
                    blockData.tracer = aBoolean;
                    changed(blockData, block, setting);
                })
                .build()
        );

        tracerSettings.add(new ColorSetting.Builder()
                .name("Tracer color")
                .description("Color of tracer line.")
                .defaultValue(new SettingColor(0, 255, 200, 125))
                .onModuleEnabled(settingColorSetting -> settingColorSetting.set(blockData.tracerColor))
                .onChanged(settingColor -> {
                    blockData.tracerColor = settingColor;
                    changed(blockData, block, setting);
                })
                .build()
        );

        settings.onEnabled();
        add(theme.settings(settings)).expandX();
    }

    private void changed(SBlockData blockData, Block block, BlockDataSetting<SBlockData> setting) {
        if (!blockData.isChanged() && block != null && setting != null) {
            setting.get().put(block, blockData);
            setting.onChanged();
        }

        blockData.changed();
    }
}
