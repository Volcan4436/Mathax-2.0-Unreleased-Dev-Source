package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.utils.render.color.Color;

public class WVerticalSeparatorTheme extends mathax.client.gui.widgets.WVerticalSeparator implements WidgetTheme {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Color colorEdges = theme.separatorEdgesSetting.get();
        Color colorCenter = theme.separatorCenterSetting.get();

        double s = theme.scale(1);
        double offsetX = Math.round(width / 2.0);

        renderer.quad(x + offsetX, y, s, height / 2, colorEdges, colorEdges, colorCenter, colorCenter);
        renderer.quad(x + offsetX, y + height / 2, s, height / 2, colorCenter, colorCenter, colorEdges, colorEdges);
    }
}
