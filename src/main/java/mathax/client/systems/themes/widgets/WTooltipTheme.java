package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;

public class WTooltipTheme extends mathax.client.gui.widgets.WTooltip implements WidgetTheme {
    public WTooltipTheme(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(this, theme().backgroundColorSetting.get());
    }
}
