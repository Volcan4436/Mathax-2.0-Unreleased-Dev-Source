package xyz.mathax.client.systems.themes.widgets;

import xyz.mathax.client.gui.renderer.GuiRenderer;
import xyz.mathax.client.gui.widgets.containers.WView;

public class WViewTheme extends WView implements WidgetTheme {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColorSetting.get(handlePressed, handleMouseOver));
        }
    }
}
