package mathax.client.gui.tabs;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.WidgetScreen;
import mathax.client.utils.gui.Cell;
import mathax.client.gui.widgets.WWidget;

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
