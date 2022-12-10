package mathax.client.systems.themes.widgets.pressable;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.widgets.WidgetTheme;
import mathax.client.gui.widgets.pressable.WPlus;

public class WPlusTheme extends WPlus implements WidgetTheme {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Theme theme = theme();
        double pad = pad();
        double scale = theme.scale(3);

        renderBackground(renderer, this, pressed, mouseOver);
        renderer.quad(x + pad, y + height / 2 - scale / 2, width - pad * 2, scale, theme.plusColorSetting.get());
        renderer.quad(x + width / 2 - scale / 2, y + pad, scale, height - pad * 2, theme.plusColorSetting.get());
    }
}
