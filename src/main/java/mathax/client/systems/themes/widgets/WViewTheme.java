package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;

public class WViewTheme extends mathax.client.gui.widgets.containers.WView implements WidgetTheme {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColorSetting.get(handlePressed, handleMouseOver));
        }
    }
}
