package mathax.client.systems.accounts.screens;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.widgets.WAccount;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.systems.accounts.Account;
import mathax.client.systems.accounts.Accounts;
import mathax.client.systems.accounts.MicrosoftLogin;
import mathax.client.systems.accounts.types.MicrosoftAccount;
import mathax.client.utils.network.Executor;
import org.jetbrains.annotations.Nullable;

import static mathax.client.MatHax.mc;

public class AccountsScreen extends WindowScreen {
    public AccountsScreen(Theme theme) {
        super(theme, "Accounts");
    }

    @Override
    public void initWidgets() {
        // Accounts
        for (Account<?> account : Accounts.get()) {
            WAccount wAccount = add(theme.account(this, account)).expandX().widget();
            wAccount.refreshScreenAction = this::reload;
        }

        // Add account
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        addButton(l, "Cracked", () -> mc.setScreen(new AddCrackedAccountScreen(theme, this)));
        addButton(l, "Altening", () -> mc.setScreen(new AddAlteningAccountScreen(theme, this)));
        addButton(l, "Microsoft", () -> {
            locked = true;

            MicrosoftLogin.getRefreshToken(refreshToken -> {
                locked = false;

                if (refreshToken != null) {
                    MicrosoftAccount account = new MicrosoftAccount(refreshToken);
                    addAccount(null, this, account);
                }
            });
        });
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(@Nullable AddAccountScreen screen, AccountsScreen parent, Account<?> account) {
        if (screen != null) {
            screen.locked = true;
        }

        Executor.execute(() -> {
            if (account.fetchInfo()) {
                account.getCache().loadHead();

                Accounts.get().add(account);
                if (account.login()) Accounts.get().save();

                if (screen != null) {
                    screen.locked = false;
                    screen.close();
                }

                parent.reload();

                return;
            }

            if (screen != null) {
                screen.locked = false;
            }
        });
    }
}
