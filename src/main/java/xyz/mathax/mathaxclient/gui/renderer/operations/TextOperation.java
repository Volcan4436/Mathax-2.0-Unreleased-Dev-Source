package xyz.mathax.mathaxclient.gui.renderer.operations;

import xyz.mathax.mathaxclient.renderer.text.TextRenderer;

public class TextOperation extends GuiRenderOperation<TextOperation> {
    private String text;

    private TextRenderer renderer;

    public boolean title;

    public TextOperation set(String text, TextRenderer renderer, boolean title) {
        this.text = text;
        this.renderer = renderer;
        this.title = title;

        return this;
    }

    @Override
    protected void onRun() {
        renderer.render(text, x, y, color);
    }
}
