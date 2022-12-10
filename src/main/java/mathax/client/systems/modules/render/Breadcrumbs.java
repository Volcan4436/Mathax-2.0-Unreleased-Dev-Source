package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.Render3DEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Category;
import mathax.client.systems.modules.Module;
import mathax.client.utils.misc.Pool;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayDeque;
import java.util.Queue;

public class Breadcrumbs extends Module {
    private final Pool<Section> sectionPool = new Pool<>(Section::new);
    private final Queue<Section> sections = new ArrayDeque<>();

    private Section section;

    private DimensionType lastDimension;

    private final SettingGroup generalSetting = settings.createGroup("General");

    // General

    private final Setting<SettingColor> colorSetting = generalSetting.add(new ColorSetting.Builder()
            .name("Color")
            .description("The color of the trail.")
            .defaultValue(new SettingColor(225, 25, 25))
            .build()
    );

    private final Setting<Integer> maxSectionsSetting = generalSetting.add(new IntSetting.Builder()
            .name("Max sections")
            .description("The maximum number of sections.")
            .defaultValue(1000)
            .min(1)
            .sliderRange(1, 5000)
            .build()
    );

    private final Setting<Double> sectionLengthSetting = generalSetting.add(new DoubleSetting.Builder()
            .name("Section length")
            .description("The section length in blocks.")
            .defaultValue(0.5)
            .sliderRange(0, 1)
            .build()
    );

    public Breadcrumbs(Category category) {
        super(category, "Breadcrumbs", "Displays a trail behind where you have walked.");
    }

    @Override
    public void onEnable() {
        section = sectionPool.get();
        section.set1();

        lastDimension = mc.world.getDimension();
    }

    @Override
    public void onDisable() {
        for (Section section : sections) {
            sectionPool.free(section);
        }

        sections.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (lastDimension != mc.world.getDimension()) {
            for (Section sec : sections) {
                sectionPool.free(sec);
            }

            sections.clear();
        }

        if (isFarEnough(section.x1, section.y1, section.z1)) {
            section.set2();

            if (sections.size() >= maxSectionsSetting.get()) {
                Section section = sections.poll();
                if (section != null) {
                    sectionPool.free(section);
                }
            }

            sections.add(section);
            section = sectionPool.get();
            section.set1();
        }

        lastDimension = mc.world.getDimension();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        int iLast = -1;
        for (Section section : sections) {
            if (iLast == -1) {
                iLast = event.renderer.lines.vec3(section.x1, section.y1, section.z1).color(colorSetting.get()).next();
            }

            int i = event.renderer.lines.vec3(section.x2, section.y2, section.z2).color(colorSetting.get()).next();
            event.renderer.lines.line(iLast, i);
            iLast = i;
        }
    }

    private boolean isFarEnough(double x, double y, double z) {
        return Math.abs(mc.player.getX() - x) >= sectionLengthSetting.get() || Math.abs(mc.player.getY() - y) >= sectionLengthSetting.get() || Math.abs(mc.player.getZ() - z) >= sectionLengthSetting.get();
    }

    private class Section {
        public float x1, y1, z1;
        public float x2, y2, z2;

        public void set1() {
            x1 = (float) mc.player.getX();
            y1 = (float) mc.player.getY();
            z1 = (float) mc.player.getZ();
        }

        public void set2() {
            x2 = (float) mc.player.getX();
            y2 = (float) mc.player.getY();
            z2 = (float) mc.player.getZ();
        }

        public void render(Render3DEvent event) {
            event.renderer.line(x1, y1, z1, x2, y2, z2, colorSetting.get());
        }
    }
}