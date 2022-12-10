package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.Render3DEvent;
import mathax.client.renderer.ShapeMode;
import mathax.client.settings.*;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.utils.entity.ProjectileEntitySimulator;
import mathax.client.utils.misc.Pool;
import mathax.client.utils.misc.Vec3;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class Trajectories extends Module {
    private final ProjectileEntitySimulator simulator = new ProjectileEntitySimulator();

    private final Pool<Vec3> vec3s = new Pool<>(Vec3::new);

    private final List<Path> paths = new ArrayList<>();

    private final SettingGroup generalSettings = settings.createGroup("General");

    // General

    private final Setting<List<Item>> itemsSetting = generalSettings.add(new ItemListSetting.Builder()
            .name("Items")
            .description("Items to display trajectories for.")
            .defaultValue(getDefaultItems())
            .filter(this::itemFilter)
            .build()
    );

    private final Setting<Boolean> otherPlayersSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Other players")
            .description("Calculates trajectories for other players.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> firedProjectilesSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Fired projectiles")
            .description("Calculates trajectories for already fired projectiles.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> accurateSetting = generalSettings.add(new BoolSetting.Builder()
            .name("Accurate")
            .description("Whether or not to calculate more accurate.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Integer> simulationStepsSetting = generalSettings.add(new IntSetting.Builder()
            .name("Simulation steps")
            .description("How many steps to simulate projectiles (0 for no limit).")
            .defaultValue(500)
            .sliderRange(0, 5000)
            .build()
    );

    private final Setting<ShapeMode> shapeModeSetting = generalSettings.add(new EnumSetting.Builder<ShapeMode>()
            .name("Shape mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColorSetting = generalSettings.add(new ColorSetting.Builder()
            .name("Side color")
            .description("The side color.")
            .defaultValue(new SettingColor(255, 150, 0, 75))
            .build()
    );

    private final Setting<SettingColor> lineColorSetting = generalSettings.add(new ColorSetting.Builder()
            .name("Line color")
            .description("The line color.")
            .defaultValue(new SettingColor(255, 150, 0))
            .build()
    );

    public Trajectories(Category category) {
        super(category, "Trajectories", "Predicts the trajectory of throwable items.");
    }

    private boolean itemFilter(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem || item instanceof FishingRodItem || item instanceof TridentItem || item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem || item instanceof ExperienceBottleItem || item instanceof ThrowablePotionItem;
    }

    private List<Item> getDefaultItems() {
        List<Item> items = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            if (itemFilter(item)) {
                items.add(item);
            }
        }

        return items;
    }

    private Path getEmptyPath() {
        for (Path path : paths) {
            if (path.points.isEmpty()) {
                return path;
            }
        }

        Path path = new Path();
        paths.add(path);

        return path;
    }

    private void calculatePath(PlayerEntity player, double tickDelta) {
        for (Path path : paths) {
            path.clear();
        }

        ItemStack itemStack = player.getMainHandStack();
        if (itemStack == null) {
            itemStack = player.getOffHandStack();
        }

        if (itemStack == null) {
            return;
        }

        if (!itemsSetting.get().contains(itemStack.getItem())) {
            return;
        }

        if (!simulator.set(player, itemStack, 0, accurateSetting.get(), tickDelta)) {
            return;
        }

        getEmptyPath().calculate();

        if (itemStack.getItem() instanceof CrossbowItem && EnchantmentHelper.getLevel(Enchantments.MULTISHOT, itemStack) > 0) {
            if (!simulator.set(player, itemStack, -10, accurateSetting.get(), tickDelta)) {
                return;
            }

            getEmptyPath().calculate();

            if (!simulator.set(player, itemStack, 10, accurateSetting.get(), tickDelta)) {
                return;
            }

            getEmptyPath().calculate();
        }
    }

    private void calculateFiredPath(Entity entity, double tickDelta) {
        for (Path path : paths) {
            path.clear();
        }

        if (!simulator.set(entity, accurateSetting.get(), tickDelta)) {
            return;
        }

        getEmptyPath().calculate();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!otherPlayersSetting.get() && player != mc.player) {
                continue;
            }

            calculatePath(player, event.tickDelta);

            for (Path path : paths) {
                path.render(event);
            }
        }

        if (firedProjectilesSetting.get()) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ProjectileEntity) {
                    calculateFiredPath(entity, event.tickDelta);

                    for (Path path : paths) {
                        path.render(event);
                    }
                }
            }
        }
    }

    private class Path {
        private final List<Vec3> points = new ArrayList<>();

        private boolean hitQuad, hitQuadHorizontal;

        private double hitQuadX1, hitQuadY1, hitQuadZ1, hitQuadX2, hitQuadY2, hitQuadZ2;

        private Entity entity;

        public void clear() {
            for (Vec3 point : points) {
                vec3s.free(point);
            }

            points.clear();

            hitQuad = false;
            entity = null;
        }

        public void calculate() {
            addPoint();

            for (int i = 0; i < (simulationStepsSetting.get() > 0 ? simulationStepsSetting.get() : Integer.MAX_VALUE); i++) {
                HitResult result = simulator.tick();
                if (result != null) {
                    processHitResult(result);
                    break;
                }

                addPoint();
            }
        }

        private void addPoint() {
            points.add(vec3s.get().set(simulator.pos));
        }

        private void processHitResult(HitResult result) {
            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) result;
                hitQuad = true;
                hitQuadX1 = hitResult.getPos().x;
                hitQuadY1 = hitResult.getPos().y;
                hitQuadZ1 = hitResult.getPos().z;
                hitQuadX2 = hitResult.getPos().x;
                hitQuadY2 = hitResult.getPos().y;
                hitQuadZ2 = hitResult.getPos().z;
                if (hitResult.getSide() == Direction.UP || hitResult.getSide() == Direction.DOWN) {
                    hitQuadHorizontal = true;
                    hitQuadX1 -= 0.25;
                    hitQuadZ1 -= 0.25;
                    hitQuadX2 += 0.25;
                    hitQuadZ2 += 0.25;
                } else if (hitResult.getSide() == Direction.NORTH || hitResult.getSide() == Direction.SOUTH) {
                    hitQuadHorizontal = false;
                    hitQuadX1 -= 0.25;
                    hitQuadY1 -= 0.25;
                    hitQuadX2 += 0.25;
                    hitQuadY2 += 0.25;
                } else {
                    hitQuadHorizontal = false;
                    hitQuadZ1 -= 0.25;
                    hitQuadY1 -= 0.25;
                    hitQuadZ2 += 0.25;
                    hitQuadY2 += 0.25;
                }

                points.add(vec3s.get().set(result.getPos()));
            } else if (result.getType() == HitResult.Type.ENTITY) {
                entity = ((EntityHitResult) result).getEntity();

                points.add(vec3s.get().set(result.getPos()).add(0, entity.getHeight() / 2, 0));
            }
        }

        public void render(Render3DEvent event) {
            Vec3 lastPoint = null;
            for (Vec3 point : points) {
                if (lastPoint != null) {
                    event.renderer.line(lastPoint.x, lastPoint.y, lastPoint.z, point.x, point.y, point.z, lineColorSetting.get());
                }

                lastPoint = point;
            }

            if (hitQuad) {
                if (hitQuadHorizontal) {
                    event.renderer.sideHorizontal(hitQuadX1, hitQuadY1, hitQuadZ1, hitQuadX1 + 0.5, hitQuadZ1 + 0.5, sideColorSetting.get(), lineColorSetting.get(), shapeModeSetting.get());
                } else {
                    event.renderer.sideVertical(hitQuadX1, hitQuadY1, hitQuadZ1, hitQuadX2, hitQuadY2, hitQuadZ2, sideColorSetting.get(), lineColorSetting.get(), shapeModeSetting.get());
                }
            }

            if (entity != null) {
                double x = (entity.getX() - entity.prevX) * event.tickDelta;
                double y = (entity.getY() - entity.prevY) * event.tickDelta;
                double z = (entity.getZ() - entity.prevZ) * event.tickDelta;

                Box box = entity.getBoundingBox();
                event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColorSetting.get(), lineColorSetting.get(), shapeModeSetting.get(), 0);
            }
        }
    }
}