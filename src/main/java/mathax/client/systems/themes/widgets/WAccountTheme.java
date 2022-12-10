package mathax.client.systems.themes.widgets;

import mathax.client.gui.WidgetScreen;
import mathax.client.systems.accounts.Account;
import mathax.client.utils.render.color.Color;

public class WAccountTheme extends mathax.client.gui.widgets.WAccount implements WidgetTheme {
    public WAccountTheme(WidgetScreen screen, Account<?> account) {
        super(screen, account);
    }

    @Override
    protected Color loggedInColor() {
        return theme().loggedInColorSetting.get();
    }

    @Override
    protected Color accountTypeColor() {
        return theme().textSecondaryColorSetting.get();
    }
}
