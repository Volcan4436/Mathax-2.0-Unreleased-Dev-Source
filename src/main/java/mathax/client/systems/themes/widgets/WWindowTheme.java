package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.WWidget;

public class WWindowTheme extends mathax.client.gui.widgets.containers.WWindow implements WidgetTheme {
    public WWindowTheme(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    protected WHeader header(WWidget icon) {
        return new WMatHaxHeader(icon);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0) {
            renderer.quad(x, y + header.height, width, height - header.height, theme().backgroundColorSetting.get());
        }
    }

    private class WMatHaxHeader extends WHeader {
        public WMatHaxHeader(WWidget icon) {
            super(icon);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quad(this, theme().accentColorSetting.get());
        }
    }
}
