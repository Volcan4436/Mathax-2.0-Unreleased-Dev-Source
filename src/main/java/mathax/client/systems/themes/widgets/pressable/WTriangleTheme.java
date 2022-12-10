package mathax.client.systems.themes.widgets.pressable;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.systems.themes.widgets.WidgetTheme;
import mathax.client.gui.widgets.pressable.WTriangle;

public class WTriangleTheme extends WTriangle implements WidgetTheme {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, theme().textColorSetting.get());
    }
}
