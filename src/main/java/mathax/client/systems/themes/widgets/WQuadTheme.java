package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.utils.render.color.Color;

public class WQuadTheme extends mathax.client.gui.widgets.WQuad {
    public WQuadTheme(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(x, y, width, height, color);
    }
}
