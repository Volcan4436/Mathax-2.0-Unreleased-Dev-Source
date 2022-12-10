package mathax.client.systems.themes.widgets;

import mathax.client.utils.render.color.Color;

public class WTopBarTheme extends mathax.client.gui.widgets.WTopBar implements WidgetTheme {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColorSetting.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColorSetting.get();
    }
}
