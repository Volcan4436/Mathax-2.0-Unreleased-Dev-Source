package mathax.client.systems.themes.widgets.pressable;

import mathax.client.systems.themes.widgets.WidgetTheme;
import mathax.client.gui.widgets.pressable.WFavorite;
import mathax.client.utils.render.color.Color;

public class WFavoriteTheme extends WFavorite implements WidgetTheme {
    public WFavoriteTheme(boolean checked) {
        super(checked);
    }

    @Override
    protected Color getColor() {
        return theme().favoriteColorSetting.get();
    }
}
