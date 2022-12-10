package mathax.client.systems.themes.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.pressable.WPressable;
import mathax.client.systems.modules.Module;
import mathax.client.systems.themes.Theme;
import mathax.client.utils.Utils;
import mathax.client.utils.render.Alignment;

import static mathax.client.MatHax.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class WModuleTheme extends WPressable implements WidgetTheme {
    private final Module module;

    private double titleWidth;

    private double animationProgress1;

    private double animationProgress2;

    public WModuleTheme(Module module) {
        this.module = module;
        this.tooltip = module.description;

        if (module.isEnabled()) {
            animationProgress1 = 1;
            animationProgress2 = 1;
        } else {
            animationProgress1 = 0;
            animationProgress2 = 0;
        }
    }

    @Override
    public double pad() {
        return theme.scale(4);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) {
            titleWidth = theme.textWidth(module.name);
        }

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            module.toggle();
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            mc.setScreen(theme.moduleScreen(module));
        }
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Theme theme = theme();
        double pad = pad();

        animationProgress1 += delta * 4 * ((module.isEnabled() || mouseOver) ? 1 : -1);
        animationProgress1 = Utils.clamp(animationProgress1, 0, 1);

        animationProgress2 += delta * 6 * (module.isEnabled() ? 1 : -1);
        animationProgress2 = Utils.clamp(animationProgress2, 0, 1);

        if (animationProgress1 > 0) {
            renderer.quad(x, y, width * animationProgress1, height, theme.moduleBackgroundSetting.get());
        }

        if (animationProgress2 > 0) {
            renderer.quad(x, y + height * (1 - animationProgress2), theme.scale(2), height * animationProgress2, theme.accentColorSetting.get());
        }

        double x = this.x + pad;
        double w = width - pad * 2;

        if (theme.moduleAlignmentSetting.get() == Alignment.X.Center) {
            x += w / 2 - titleWidth / 2;
        } else if (theme.moduleAlignmentSetting.get() == Alignment.X.Right) {
            x += w - titleWidth;
        }

        renderer.text(module.name, x, y + pad, theme.textColorSetting.get(), false);
    }
}
