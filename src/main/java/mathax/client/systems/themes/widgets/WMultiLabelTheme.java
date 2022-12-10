package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.utils.render.color.Color;

public class WMultiLabelTheme extends mathax.client.gui.widgets.WMultiLabel implements WidgetTheme {
    public WMultiLabelTheme(String text, boolean title, double maxWidth) {
        super(text, title, maxWidth);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double textHeight = theme.textHeight(title);
        Color defaultColor = theme().textColorSetting.get();
        for (int i = 0; i < lines.size(); i++) {
            renderer.text(lines.get(i), x, y + textHeight * i, color != null ? color : defaultColor, false);
        }
    }
}
