package xyz.mathax.client.systems.themes.widgets;

import xyz.mathax.client.gui.WidgetScreen;
import xyz.mathax.client.gui.widgets.WAccount;
import xyz.mathax.client.systems.accounts.Account;
import xyz.mathax.client.utils.render.color.Color;

public class WAccountTheme extends WAccount implements WidgetTheme {
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
