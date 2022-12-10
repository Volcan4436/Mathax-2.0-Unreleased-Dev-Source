package mathax.client.systems.hud;

import mathax.client.gui.renderer.OverlayRenderer;
import mathax.client.utils.render.color.Color;

public abstract class DoubleTextHudElement extends HudElement {
    protected Color leftColor;
    protected Color rightColor;

    protected boolean visible = true;

    private String left, right;

    private double leftWidth, rightWidth;

    public DoubleTextHudElement(Hud hud, String name, String description, boolean defaultActive) {
        super(hud, name, description, defaultActive);
        this.leftColor = hud.primaryColorSetting.get();
        this.rightColor = hud.secondaryColorSetting.get();
    }

    public DoubleTextHudElement(Hud hud, String name, String description) {
        super(hud, name, description);
        this.leftColor = hud.primaryColorSetting.get();
        this.rightColor = hud.secondaryColorSetting.get();
    }

    @Override
    public void update(OverlayRenderer renderer) {
        left = getLeft();
        leftWidth = renderer.textWidth(left);

        right = getRight();
        rightWidth = renderer.textWidth(right);

        box.setSize(leftWidth + rightWidth, renderer.textHeight());
    }

    @Override
    public void render(OverlayRenderer renderer) {
        if (!visible) {
            return;
        }

        double x = box.getX();
        double y = box.getY();

        renderer.text(left, x, y, leftColor);
        renderer.text(right, x + leftWidth, y, rightColor);
    }

    protected void setLeft(String left) {
        this.left = left;
        this.leftWidth = 0;
    }

    protected abstract String getLeft();

    protected void setRight(String right) {
        this.right = right;
        this.rightWidth = 0;
    }

    protected abstract String getRight();
}