package mathax.client.renderer.text;

import mathax.client.systems.themes.Themes;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;

public interface TextRenderer {
    static TextRenderer get(TextRendererType rendererType) {
        switch (rendererType) {
            case Custom -> {
                return Fonts.RENDERER;
            }
            case Vanilla -> {
                return VanillaTextRenderer.INSTANCE;
            }
            default -> {
                return Themes.getTheme().customFont() ? Fonts.RENDERER : VanillaTextRenderer.INSTANCE;
            }
        }
    }

    static TextRenderer get() {
        return get(TextRendererType.Config);
    }

    boolean isVanilla();

    double getScale();

    void setScale(double scale);

    double getAlpha();

    void setAlpha(double alpha);

    void begin(double scale, boolean scaleOnly, boolean big);

    default void begin(double scale) {
        begin(scale, false, false);
    }

    default void begin() {
        begin(1, false, false);
    }

    default void beginBig() {
        begin(1, false, true);
    }

    double getWidth(String text, int length, boolean shadow);

    default double getWidth(String text, boolean shadow) {
        return getWidth(text, text.length(), shadow);
    }

    default double getWidth(String text) { return getWidth(text, text.length(), false); }

    double getHeight(boolean shadow);

    default double getHeight() {
        return getHeight(false);
    }

    double render(String text, double x, double y, Color color, boolean shadow);

    default double render(String text, double x, double y, Color color) {
        return render(text, x, y, color, false);
    }

    boolean isBuilding();

    boolean isBuilt();

    void end(MatrixStack matrixStack);

    default void end() {
        end(null);
    }
}