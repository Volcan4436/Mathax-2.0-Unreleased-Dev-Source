package xyz.mathax.client.settings.screens;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.WindowScreen;
import xyz.mathax.client.gui.widgets.containers.WTable;
import xyz.mathax.client.gui.widgets.pressable.WButton;
import xyz.mathax.client.settings.PotionSetting;
import xyz.mathax.client.utils.misc.MyPotion;

public class PotionSettingScreen extends WindowScreen {
    private final PotionSetting setting;

    public PotionSettingScreen(Theme theme, PotionSetting setting) {
        super(theme, "Select Potion");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        WTable table = add(theme.table()).expandX().widget();

        for (MyPotion potion : MyPotion.values()) {
            table.add(theme.itemWithLabel(potion.potion, potion.potion.getName().getString()));

            WButton select = table.add(theme.button("Select")).widget();
            select.action = () -> {
                setting.set(potion);
                close();
            };

            table.row();
        }
    }
}
