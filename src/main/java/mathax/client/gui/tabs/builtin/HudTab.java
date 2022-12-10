package mathax.client.gui.tabs.builtin;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.WindowTabScreen;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.systems.hud.Hud;
import mathax.client.systems.hud.screens.HudEditorScreen;
import net.minecraft.client.gui.screen.Screen;

import static mathax.client.MatHax.mc;

public class HudTab extends Tab {
    public HudTab() {
        super("HUD");
    }

    @Override
    public TabScreen createScreen(Theme theme) {
        return new HudScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof HudScreen;
    }

    public static class HudScreen extends WindowTabScreen {
        private final Hud hud;

        public HudScreen(Theme theme, Tab tab) {
            super(theme, tab);

            hud = Hud.get();
            hud.settings.onActivated();
        }

        @Override
        public void initWidgets() {
            add(theme.settings(hud.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();

            WButton openEditor = add(theme.button("Edit")).expandX().widget();
            openEditor.action = () -> mc.setScreen(new HudEditorScreen(theme));

            WButton resetHud = add(theme.button("Reset")).expandX().widget();
            resetHud.action = hud.reset;

            add(theme.horizontalSeparator()).expandX();

            WHorizontalList bottom = add(theme.horizontalList()).expandX().widget();

            bottom.add(theme.label("Active: "));
            WCheckbox enabled = bottom.add(theme.checkbox(hud.enabled)).expandCellX().widget();
            enabled.action = () -> hud.enabled = enabled.checked;

            WButton resetSettings = bottom.add(theme.button(GuiRenderer.RESET)).widget();
            resetSettings.action = hud.settings::reset;
        }
    }
}
