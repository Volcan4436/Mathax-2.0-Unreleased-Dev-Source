package mathax.client.renderer.text;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import static mathax.client.MatHax.mc;

public class VanillaTextRenderer implements TextRenderer {
    public static final VanillaTextRenderer INSTANCE = new VanillaTextRenderer();

    private final BufferBuilder buffer = new BufferBuilder(2048);
    private final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);

    private final MatrixStack matrixStack = new MatrixStack();
    private final Matrix4f emptyMatrix = new Matrix4f();

    public double scale = 2;
    public boolean scaleIndividually;

    public boolean shadow;

    private boolean built;
    private boolean building;
    private double alpha = 1;

    private VanillaTextRenderer() {} // Use INSTANCE

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public boolean getShadow() {
        return shadow;
    }

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) {
            return 0;
        }

        if (length != text.length()) {
            text = text.substring(0, length);
        }

        return (mc.textRenderer.getWidth(text) + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public double getHeight(boolean shadow) {
        return (mc.textRenderer.fontHeight + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big, boolean shadow) {
        if (building) {
            throw new RuntimeException("VanillaTextRenderer.begin() called twice");
        }

        this.scale = scale * 2;
        this.shadow = shadow;
        built = false;
        building = true;
    }

    @Override
    public double render(String text, double x, double y, Color color) {
        return render(text, x, y, color, shadow);
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) {
            begin();
        }

        x += 0.5 * scale;
        y += 0.5 * scale;

        int preA = color.a;
        color.a = (int) ((color.a / 255 * alpha) * 255);

        Matrix4f matrix = emptyMatrix;
        if (scaleIndividually) {
            matrixStack.push();
            matrixStack.scale((float) scale, (float) scale, 1);
            matrix = matrixStack.peek().getPositionMatrix();
        }

        double x2 = mc.textRenderer.draw(text, (float) (x / scale), (float) (y / scale), color.getPacked(), shadow, matrix, immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

        if (scaleIndividually) {
            matrixStack.pop();
        }

        color.a = preA;

        if (!wasBuilding) {
            end();
        }

        return (x2 - 1) * scale;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public void end(MatrixStack matrixStack) {
        if (!building) {
            throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");
        }

        MatrixStack matrixStack1 = RenderSystem.getModelViewStack();

        RenderSystem.disableDepthTest();
        matrixStack1.push();
        if (matrixStack != null) {
            matrixStack1.multiplyPositionMatrix(matrixStack.peek().getPositionMatrix());
        }

        if (!scaleIndividually) {
            matrixStack1.scale((float) scale, (float) scale, 1);
        }

        RenderSystem.applyModelViewMatrix();

        immediate.draw();

        matrixStack1.pop();
        RenderSystem.enableDepthTest();
        RenderSystem.applyModelViewMatrix();

        this.scale = 2;
        building = false;
        built = true;
    }
}
