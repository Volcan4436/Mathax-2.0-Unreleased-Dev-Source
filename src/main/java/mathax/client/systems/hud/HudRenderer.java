package mathax.client.systems.hud;

import mathax.client.renderer.text.TextRenderer;
import mathax.client.systems.themes.Themes;
import mathax.client.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private final List<Runnable> postTasks = new ArrayList<>();

    private TextRenderer textRenderer;

    private boolean shadow;

    public double scale;
    public double delta;

    public void begin(double scale, double frameDelta, boolean scaleOnly) {
        this.textRenderer = Themes.getTheme().textRenderer();
        this.textRenderer.begin(scale, scaleOnly, false);

        this.shadow = Themes.getTheme().fontShadow();
        this.scale = scale;
        this.delta = frameDelta;
    }

    public void end() {
        textRenderer.end();

        for (Runnable runnable : postTasks) {
            runnable.run();
        }

        postTasks.clear();
    }

    public void text(String text, double x, double y, Color color, boolean shadow) {
        textRenderer.render(text, x, y, color, shadow);
    }

    public void text(String text, double x, double y, Color color) {
        text(text, x, y, color, shadow);
    }

    public double textWidth(String text, boolean shadow) {
        return textRenderer.getWidth(text, shadow);
    }

    public double textWidth(String text) {
        return textWidth(text, shadow);
    }

    public double textHeight(boolean shadow) {
        return textRenderer.getHeight(shadow);
    }

    public double textHeight() {
        return textHeight(shadow);
    }

    public void addPostTask(Runnable runnable) {
        postTasks.add(runnable);
    }
}