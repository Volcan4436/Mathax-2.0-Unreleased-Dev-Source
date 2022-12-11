package xyz.mathax.client.systems.hud.screens;

import xyz.mathax.client.gui.WindowScreen;
import xyz.mathax.client.gui.renderer.GuiRenderer;
import xyz.mathax.client.gui.widgets.containers.WContainer;
import xyz.mathax.client.gui.widgets.containers.WHorizontalList;
import xyz.mathax.client.gui.widgets.pressable.WButton;
import xyz.mathax.client.gui.widgets.pressable.WCheckbox;
import xyz.mathax.client.systems.Systems;
import xyz.mathax.client.systems.hud.Hud;
import xyz.mathax.client.systems.hud.HudElement;
import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.utils.Utils;

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