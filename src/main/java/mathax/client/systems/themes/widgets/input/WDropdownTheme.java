package mathax.client.systems.themes.widgets.input;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.systems.themes.Theme;
import mathax.client.systems.themes.widgets.WidgetTheme;
import mathax.client.gui.widgets.input.WDropdown;
import mathax.client.utils.render.color.Color;

public class WDropdownTheme<T> extends WDropdown<T> implements WidgetTheme {
    public WDropdownTheme(T[] values, T value) {
        super(values, value);
    }

    @Override
    protected WDropdownRoot createRootWidget() {
        return new WRoot();
    }

    @Override
    protected WDropdownValue createValueWidget() {
        return new WValue();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Theme theme = theme();
        double pad = pad();
        double textHeight = theme.textHeight();

        renderBackground(renderer, this, pressed, mouseOver);

        String text = get().toString();
        double w = theme.textWidth(text);
        renderer.text(text, x + pad + maxValueWidth / 2 - w / 2, y + pad, theme.textColorSetting.get(), false);

        renderer.rotatedQuad(x + pad + maxValueWidth + pad, y + pad, textHeight, textHeight, 0, GuiRenderer.TRIANGLE, theme.textColorSetting.get());
    }

    private static class WRoot extends WDropdownRoot implements WidgetTheme {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            Theme theme = theme();
            double scale = theme.scale(2);
            Color color = theme.outlineColorSetting.get();

            renderer.quad(x, y + height - scale, width, scale, color);
            renderer.quad(x, y, scale, height - scale, color);
            renderer.quad(x + width - scale, y, scale, height - scale, color);
        }
    }

    private class WValue extends WDropdownValue implements WidgetTheme {
        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme.textWidth(value.toString()) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            Theme theme = theme();

            Color color = theme.backgroundColorSetting.get(pressed, mouseOver, true);
            int preA = color.a;
            color.a += color.a / 2;
            color.validate();

            renderer.quad(this, color);

            color.a = preA;

            String text = value.toString();
            renderer.text(text, x + width / 2 - theme.textWidth(text) / 2, y + pad(), theme.textColorSetting.get(), false);
        }
    }
}
