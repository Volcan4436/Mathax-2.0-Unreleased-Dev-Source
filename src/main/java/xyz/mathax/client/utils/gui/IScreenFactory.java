package xyz.mathax.client.utils.gui;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(Theme theme);
}
