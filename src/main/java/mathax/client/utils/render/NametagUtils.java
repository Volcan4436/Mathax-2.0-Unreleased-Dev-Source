package mathax.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.client.mixininterface.IMatrix4f;
import mathax.client.utils.Utils;
import mathax.client.utils.misc.Vec3;
import mathax.client.utils.misc.Vec4;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import static mathax.client.MatHax.mc;

public class NametagUtils {
    private static final Vec4 vec4 = new Vec4();
    private static final Vec4 mmMat4 = new Vec4();
    private static final Vec4 pmMat4 = new Vec4();

    private static final Vec3 camera = new Vec3();
    private static final Vec3 cameraNegated = new Vec3();

    private static Matrix4f model;
    private static Matrix4f projection;

    private static double windowScale;

    public static double scale;

    public static void onRender(MatrixStack matrixStack, Matrix4f projection) {
        model = matrixStack.peek().getPositionMatrix().copy();

        NametagUtils.projection = projection;

        camera.set(mc.gameRenderer.getCamera().getPos());
        cameraNegated.set(camera);
        cameraNegated.negate();

        windowScale = mc.getWindow().calculateScaleFactor(1, false);
    }

    public static boolean to2D(Vec3 pos, double scale) {
        return to2D(pos, scale, true);
    }

    public static boolean to2D(Vec3 pos, double scale, boolean distanceScaling) {
        NametagUtils.scale = scale;
        if (distanceScaling) {
            NametagUtils.scale *= getScale(pos);
        }

        vec4.set(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1);

        ((IMatrix4f) (Object) model).multiplyMatrix(vec4, mmMat4);
        ((IMatrix4f) (Object) projection).multiplyMatrix(mmMat4, pmMat4);

        if (pmMat4.w <= 0.0f) {
            return false;
        }

        pmMat4.toScreen();

        double x = pmMat4.x * mc.getWindow().getFramebufferWidth();
        double y = pmMat4.y * mc.getWindow().getFramebufferHeight();
        if (Double.isInfinite(x) || Double.isInfinite(y)) {
            return false;
        }

        pos.set(x / windowScale, mc.getWindow().getFramebufferHeight() - y / windowScale, pmMat4.z);

        return true;
    }

    public static void begin(Vec3 pos) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(pos.x, pos.y, 0);
        matrixStack.scale((float) scale, (float) scale, 1);
    }

    public static void end() {
        RenderSystem.getModelViewStack().pop();
    }

    private static double getScale(Vec3 pos) {
        return Utils.clamp(1 - camera.distanceTo(pos) * 0.01, 0.5, Integer.MAX_VALUE);
    }
}