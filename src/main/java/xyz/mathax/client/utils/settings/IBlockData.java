package xyz.mathax.client.utils.settings;

import xyz.mathax.client.settings.BlockDataSetting;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.WidgetScreen;
import xyz.mathax.client.utils.misc.IChangeable;
import xyz.mathax.client.utils.misc.ICopyable;
import xyz.mathax.client.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(Theme theme, Block block, BlockDataSetting<T> setting);
}
