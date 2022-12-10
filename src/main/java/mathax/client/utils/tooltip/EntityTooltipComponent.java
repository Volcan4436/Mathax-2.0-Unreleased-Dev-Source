package mathax.client.utils.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import static mathax.client.MatHax.mc;

public class EntityTooltipComponent implements MatHaxTooltipData, TooltipComponent {
    protected final Entity entity;

    public EntityTooltipComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 60;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixStack, ItemRenderer itemRenderer, int z) {
        matrixStack.push();
        matrixStack.translate(15, 2, z);
        entity.setVelocity(1.f, 1.f, 1.f);
        renderEntity(matrixStack, x, y);
        matrixStack.pop();
    }

    protected void renderEntity(MatrixStack matrixStack, int x, int y) {
        if (mc.player == null) {
            return;
        }

        float size = 24;
        if (Math.max(entity.getWidth(), entity.getHeight()) > 1.0) {
            size /= Math.max(entity.getWidth(), entity.getHeight());
        }

        DiffuseLighting.disableGuiDepthLighting();
        matrixStack.push();
        int yOffset = 16;

        if (entity instanceof SquidEntity) {
            size = 16;
            yOffset = 2;
        }

        matrixStack.translate(x + 10, y + yOffset, 1050);
        matrixStack.scale(1f, 1f, -1);
        matrixStack.translate(0, 0, 1000);
        matrixStack.scale(size, size, size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(-10.f);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        setupAngles();
        EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        entity.age = mc.player.age;
        entity.setCustomNameVisible(false);
        entityRenderDispatcher.render(entity, 0, 0, 0, 0.f, 1.f, matrixStack, immediate, 15728880);
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrixStack.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    protected void setupAngles() {
        float yaw = (float) (((System.currentTimeMillis() / 10)) % 360);
        entity.setYaw(yaw);
        entity.setHeadYaw(yaw);
        entity.setPitch(0.f);
        if (entity instanceof LivingEntity) {
            if (entity instanceof GoatEntity) {
                ((LivingEntity) entity).headYaw = yaw;
            }

            ((LivingEntity) entity).bodyYaw = yaw;
        }
    }
}
