package xyz.mathax.client.systems.themes.widgets.pressable;

import xyz.mathax.client.gui.renderer.GuiRenderer;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.systems.themes.widgets.WidgetTheme;
import xyz.mathax.client.gui.widgets.pressable.WCheckbox;
import xyz.mathax.client.utils.Utils;

public class WCheckboxTheme extends WCheckbox implements WidgetTheme {
    private double animProgress;

    public WCheckboxTheme(boolean checked) {
        super(checked);
        animProgress = checked ? 1 : 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Theme theme = theme();

        animProgress += (checked ? 1 : -1) * delta * 14;
        animProgress = Utils.clamp(animProgress, 0, 1);

        renderBackground(renderer, this, pressed, mouseOver);

        if (animProgress > 0) {
            double cs = (width - theme.scale(2)) / 1.75 * animProgress;
            renderer.quad(x + (width - cs) / 2, y + (height - cs) / 2, cs, cs, theme.checkboxColorSetting.get());
        }
    }
}
