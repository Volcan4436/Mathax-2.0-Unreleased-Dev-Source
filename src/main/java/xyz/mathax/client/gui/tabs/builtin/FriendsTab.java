package xyz.mathax.client.gui.tabs.builtin;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.tabs.Tab;
import xyz.mathax.client.gui.tabs.TabScreen;
import xyz.mathax.client.gui.tabs.WindowTabScreen;
import xyz.mathax.client.gui.widgets.containers.WHorizontalList;
import xyz.mathax.client.gui.widgets.containers.WTable;
import xyz.mathax.client.gui.widgets.input.WTextBox;
import xyz.mathax.client.gui.widgets.pressable.WMinus;
import xyz.mathax.client.gui.widgets.pressable.WPlus;
import xyz.mathax.client.systems.friends.Friend;
import xyz.mathax.client.systems.friends.Friends;
import xyz.mathax.client.utils.network.Executor;
import net.minecraft.client.gui.screen.Screen;

public class FriendsTab extends Tab {
    public FriendsTab() {
        super("Friends");
    }

    @Override
    public TabScreen createScreen(Theme theme) {
        return new FriendsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof FriendsScreen;
    }

    private static class FriendsScreen extends WindowTabScreen {
        public FriendsScreen(Theme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            // New
            WHorizontalList list = add(theme.horizontalList()).expandX().widget();

            WTextBox nameW = list.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            nameW.setFocused(true);

            WPlus add = list.add(theme.plus()).widget();
            add.action = () -> {
                String name = nameW.get().trim();
                Friend friend = new Friend(name);
                if (Friends.get().add(friend)) {
                    nameW.set("");
                    reload();

                    Executor.execute(() -> {
                        friend.updateInfo();
                        reload();
                    });
                }
            };

            enterAction = add.action;
        }

        private void initTable(WTable table) {
            table.clear();

            if (Friends.get().isEmpty()) {
                return;
            }

            for (Friend friend : Friends.get()) {
                table.add(theme.texture(32, 32, friend.getHead().needsRotate() ? 90 : 0, friend.getHead()));
                table.add(theme.label(friend.getName()));

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);
                    reload();
                };

                table.row();
            }
        }
    }
}