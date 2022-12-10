package mathax.client.gui.tabs;

import mathax.client.systems.themes.Theme;
import mathax.client.utils.gui.Cell;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WWindow;

public abstract class WindowTabScreen extends TabScreen {
    protected final WWindow window;

    public WindowTabScreen(Theme theme, Tab tab) {
        super(theme, tab);

        window = super.add(theme.window(tab.name)).center().widget();
    }

    @Override
    public <W extends WWidget> Cell<W> add(W widget) {
        return window.add(widget);
    }

    @Override
    public void clear() {
        window.clear();
    }
}
