package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.pressable.WTriangle;

public class WSectionTheme extends mathax.client.gui.widgets.containers.WSection {
    public WSectionTheme(String title, boolean expanded, WWidget headerWidget) {
        super(title, expanded, headerWidget);
    }

    @Override
    protected WHeader createHeader() {
        return new WMatHaxHeader(title);
    }

    protected class WMatHaxHeader extends WHeader {
        private WTriangle triangle;

        public WMatHaxHeader(String title) {
            super(title);
        }

        @Override
        public void init() {
            add(theme.horizontalSeparator(title)).expandX();

            if (headerWidget != null) add(headerWidget);

            triangle = new WHeaderTriangle();
            triangle.theme = theme;
            triangle.action = this::onClick;

            add(triangle);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            triangle.rotation = (1 - animProgress) * -90;
        }
    }

    protected static class WHeaderTriangle extends WTriangle implements WidgetTheme {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().textColorSetting.get());
        }
    }
}
