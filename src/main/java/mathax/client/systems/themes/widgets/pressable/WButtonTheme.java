package mathax.client.systems.themes.widgets.pressable;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.renderer.packer.GuiTexture;
import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.widgets.WidgetTheme;
import mathax.client.gui.widgets.pressable.WButton;

public class WButtonTheme extends WButton implements WidgetTheme {
    public WButtonTheme(String text, GuiTexture texture) {
        super(text, texture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Theme theme = theme();
        double pad = pad();

        renderBackground(renderer, this, pressed, mouseOver);

        if (text != null) {
            renderer.text(text, x + width / 2 - textWidth / 2, y + pad, theme.textColorSetting.get(), false);
        } else {
            double textHeight = theme.textHeight();
            renderer.quad(x + width / 2 - textHeight / 2, y + pad, textHeight, textHeight, texture, theme.textColorSetting.get());
        }
    }
}
