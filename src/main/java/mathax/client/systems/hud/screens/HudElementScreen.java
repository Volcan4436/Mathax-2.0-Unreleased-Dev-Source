package mathax.client.systems.hud.screens;

import mathax.client.gui.WindowScreen;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.systems.Systems;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.HudElement;
import mathax.client.systems.themes.Theme;
import mathax.client.utils.Utils;

public class HudElementScreen extends WindowScreen {
    public final HudElement element;
    private WContainer settings;

    public HudElementScreen(Theme theme, HudElement element) {
        super(theme, element.name);

        this.element = element;
    }

    @Override
    public void initWidgets() {
        // Description
        add(theme.label(element.description, Utils.getWindowWidth() / 2.0));

        // Settings
        if (element.settings.sizeGroups() > 0) {
            settings = add(theme.verticalList()).expandX().widget();
            settings.add(theme.settings(element.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();
        }

        // Bottom
        WHorizontalList bottomList = add(theme.horizontalList()).expandX().widget();

        //   Active
        bottomList.add(theme.label("Active:"));
        WCheckbox enabled = bottomList.add(theme.checkbox(element.enabled)).widget();
        enabled.action = () -> {
            if (element.enabled != enabled.checked) element.toggle();
        };

        WButton reset = bottomList.add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = () -> {
            if (element.enabled != element.defaultEnabled) {
                element.enabled = enabled.checked = element.defaultEnabled;
            }
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (settings != null) {
            element.settings.tick(settings, theme);
        }
    }

    @Override
    protected void onRenderBefore(float delta) {
        if (!Utils.canUpdate()) {
            Systems.get(Hud.class).render(delta, hudElement -> true);
        }
    }
}