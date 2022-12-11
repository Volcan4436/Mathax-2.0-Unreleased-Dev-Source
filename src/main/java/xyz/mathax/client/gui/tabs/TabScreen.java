package xyz.mathax.client.gui.tabs;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.WidgetScreen;
import xyz.mathax.client.utils.gui.Cell;
import xyz.mathax.client.gui.widgets.WWidget;

public abstract class TabScreen extends WidgetScreen {
    public final Tab tab;

    public TabScreen(Theme theme, Tab tab) {
        super(theme, tab.name);

        this.tab = tab;
    }

    public <T extends WWidget> Cell<T> addDirect(T widget) {
        return super.add(widget);
    }
}
