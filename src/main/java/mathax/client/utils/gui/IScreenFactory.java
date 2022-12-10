package mathax.client.utils.gui;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(Theme theme);
}
