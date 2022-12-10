package mathax.client.utils.settings;

import mathax.client.settings.BlockDataSetting;
import mathax.client.systems.themes.Theme;
import mathax.client.gui.WidgetScreen;
import mathax.client.utils.misc.IChangeable;
import mathax.client.utils.misc.ICopyable;
import mathax.client.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(Theme theme, Block block, BlockDataSetting<T> setting);
}
