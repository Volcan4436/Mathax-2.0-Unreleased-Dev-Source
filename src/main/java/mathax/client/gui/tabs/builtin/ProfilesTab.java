package mathax.client.gui.tabs.builtin;

import mathax.client.gui.WindowScreen;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.TabScreen;
import mathax.client.gui.tabs.WindowTabScreen;
import mathax.client.gui.widgets.containers.WContainer;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WMinus;
import mathax.client.systems.profiles.Profile;
import mathax.client.systems.profiles.Profiles;
import mathax.client.systems.themes.Theme;
import mathax.client.utils.Utils;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

import static mathax.client.MatHax.mc;

public class ProfilesTab extends Tab {
    public ProfilesTab() {
        super("Profiles");
    }

    @Override
    public TabScreen createScreen(Theme theme) {
        return new ProfilesScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof ProfilesScreen;
    }

    private static class ProfilesScreen extends WindowTabScreen {
        public ProfilesScreen(Theme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            // Create
            WButton create = add(theme.button("Create")).expandX().widget();
            create.action = () -> mc.setScreen(new EditProfileScreen(theme, null, this::reload));
        }

        private void initTable(WTable table) {
            table.clear();

            if (Profiles.get().isEmpty()) {
                return;
            }

            for (Profile profile : Profiles.get()) {
                table.add(theme.label(profile.nameSetting.get())).expandCellX();

                WButton save = table.add(theme.button("Save")).widget();
                save.action = profile::save;

                WButton load = table.add(theme.button("Load")).widget();
                load.action = profile::load;

                WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
                edit.action = () -> mc.setScreen(new EditProfileScreen(theme, profile, this::reload));

                WMinus remove = table.add(theme.minus()).widget();
                remove.action = () -> {
                    Profiles.get().remove(profile);
                    reload();
                };

                table.row();
            }
        }
    }

    private static class EditProfileScreen extends WindowScreen {
        private WContainer settingsContainer;

        private final Profile profile;

        private final boolean isNew;

        private final Runnable action;

        public EditProfileScreen(Theme theme, Profile profile, Runnable action) {
            super(theme, profile == null ? "New Profile" : "Edit Profile");

            this.isNew = profile == null;
            this.profile = isNew ? new Profile() : profile;
            this.action = action;
        }

        @Override
        public void initWidgets() {
            settingsContainer = add(theme.verticalList()).expandX().minWidth(400).widget();
            settingsContainer.add(theme.settings(profile.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();

            WButton save = add(theme.button(isNew ? "Create" : "Save")).expandX().widget();
            save.action = () -> {
                if (profile.nameSetting.get().isEmpty()) {
                    return;
                }

                if (isNew) {
                    for (Profile p : Profiles.get()) {
                        if (profile.equals(p)) {
                            return;
                        }
                    }
                }

                List<String> valid = new ArrayList<>();
                for (String address : profile.loadOnJoinSetting.get()) {
                    if (Utils.resolveAddress(address)) {
                        valid.add(address);
                    }
                }

                profile.loadOnJoinSetting.set(valid);

                if (isNew) {
                    Profiles.get().add(profile);
                } else {
                    Profiles.get().save();
                }

                close();
            };

            enterAction = save.action;
        }

        @Override
        public void tick() {
            profile.settings.tick(settingsContainer, theme);
        }

        @Override
        protected void onClosed() {
            if (action != null) {
                action.run();
            }
        }
    }
}
