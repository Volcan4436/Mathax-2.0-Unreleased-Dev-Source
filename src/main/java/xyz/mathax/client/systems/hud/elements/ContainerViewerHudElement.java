package xyz.mathax.client.systems.hud.elements;

import xyz.mathax.client.gui.renderer.OverlayRenderer;
import xyz.mathax.client.renderer.GL;
import xyz.mathax.client.renderer.Renderer2D;
import xyz.mathax.client.settings.BoolSetting;
import xyz.mathax.client.settings.DoubleSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.systems.hud.HudElement;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.misc.MatHaxIdentifier;
import xyz.mathax.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ContainerViewerHudElement extends HudElement {
    private static final Identifier TEXTURE = new MatHaxIdentifier("textures/container.png");

    private final ItemStack[] inventory = new ItemStack[9 * 3];

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<Double> scaleSetting = generalSettings.add(new DoubleSetting.Builder()
        .name("Scale")
        .description("The scale.")
        .defaultValue(2)
        .min(1)
        .sliderRange(1, 5)
        .build()
    );

    private final Setting<Boolean> echestNoItemSetting = generalSettings.add(new BoolSetting.Builder()
        .name("Echest when empty")
        .description("Display contents of ender chest if not holding any other container.")
        .defaultValue(false)
        .build()
    );

    public ContainerViewerHudElement(Hud hud) {
        super(hud, "Container Viewer", "Displays held containers.", false);
    }

    @Override
    public void update(OverlayRenderer renderer) {
        box.setSize(176 * scaleSetting.get(), 67 * scaleSetting.get());
    }

    @Override
    public void render(OverlayRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        ItemStack container = getContainer();
        if (container == null) {
            return;
        }

        drawBackground((int) x, (int) y, container);

        Utils.getItemsInContainerItem(container, inventory);

        for (int row = 0; row < 3; row++) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory[row * 9 + i];
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                RenderUtils.drawItem(stack, (int) (x + (8 + i * 18) * scaleSetting.get()), (int) (y + (7 + row * 18) * scaleSetting.get()), scaleSetting.get(), true);
            }
        }
    }

    private ItemStack getContainer() {
        if (isInEditor()) {
            return Items.ENDER_CHEST.getDefaultStack();
        }

        ItemStack stack = mc.player.getOffHandStack();
        if (Utils.hasItems(stack)) {
            return stack;
        }

        stack = mc.player.getMainHandStack();
        if (Utils.hasItems(stack)) {
            return stack;
        }

        return echestNoItemSetting.get() ? Items.ENDER_CHEST.getDefaultStack() : null;
    }

    private void drawBackground(int x, int y, ItemStack container) {
        GL.bindTexture(TEXTURE);

        Renderer2D.TEXTURE.begin();
        Renderer2D.TEXTURE.texturedQuad(x, y, box.width, box.height, Utils.getShulkerColor(container));
        Renderer2D.TEXTURE.render(null);
    }
}
