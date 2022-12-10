package mathax.client.mixin.canvas;

import grondag.canvas.render.world.CanvasWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CanvasWorldRenderer.class, remap = false)
public class CanvasWorldRendererMixin {
    @ModifyVariable(method = "renderWorld", at = @At("LOAD"), name = "blockOutlines")
    private boolean renderWorld_blockOutlines(boolean blockOutlines) {
        /*if (Modules.get().isEnabled(BlockSelection.class)) {
            return false;
        }*/

        return blockOutlines;
    }

    /*@Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderHead(MatrixStack viewMatrixStack, float tickDelta, long frameStartNanos, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo info) {
        PostProcessShaders.beginRender();
    }

    // Injected through ASM because mixins are fucking retarded and don't work outside of development environment for this one injection
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V", shift = At.Shift.AFTER))
    private void onRenderOutlines(CallbackInfo info) {
        PostProcessShaders.endRender();
    }*/
}
