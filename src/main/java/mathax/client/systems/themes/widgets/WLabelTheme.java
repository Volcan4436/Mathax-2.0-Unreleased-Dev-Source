package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;

public class WLabelTheme extends mathax.client.gui.widgets.WLabel implements WidgetTheme {
    public WLabelTheme(String text, boolean title) {
        super(text, title);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!text.isEmpty()) {
            renderer.text(text, x, y, color != null ? color : (title ? theme().titleTextColorSetting.get() : theme().textColorSetting.get()), title);
        }
    }
}
