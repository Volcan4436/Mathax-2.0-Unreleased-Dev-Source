package xyz.mathax.client.mixin;

import xyz.mathax.client.systems.modules.Modules;
import xyz.mathax.client.systems.modules.render.Ambience;
import xyz.mathax.client.systems.modules.render.Xray;
import xyz.mathax.client.utils.render.color.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @Unique
    private final ThreadLocal<Integer> alphas = new ThreadLocal<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
        Ambience ambience = Modules.get().get(Ambience.class);
        if (ambience.isEnabled() && ambience.customLavaColorSetting.get() && fluidState.isIn(FluidTags.LAVA)) {
            alphas.set(-2);
        } else {
            int alpha = Xray.getAlpha(fluidState.getBlockState(), pos);
            if (alpha == 0) {
                info.cancel();
            } else {
                alphas.set(alpha);
            }
        }
    }

    @Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
    private void onVertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light, CallbackInfo info) {
        int alpha = alphas.get();
        if (alpha == -2) {
            Color color = Modules.get().get(Ambience.class).lavaColorSetting.get();
            vertex(vertexConsumer, x, y, z, color.r, color.g, color.b, color.a, u, v, light);
            info.cancel();
        } else if (alpha != -1) {
            vertex(vertexConsumer, x, y, z, (int) (red * 255), (int) (green * 255), (int) (blue * 255), alpha, u, v, light);
            info.cancel();
        }
    }

    @Unique
    private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, int red, int green, int blue, int alpha, float u, float v, int light) {
        vertexConsumer.vertex(x, y, z).color(red, green, blue, alpha).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f).next();
    }
}