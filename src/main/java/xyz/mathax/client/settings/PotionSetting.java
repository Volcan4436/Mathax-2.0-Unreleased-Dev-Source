package xyz.mathax.client.settings;

import xyz.mathax.client.utils.misc.MyPotion;
import xyz.mathax.client.utils.settings.IVisible;

import java.util.function.Consumer;

public class PotionSetting extends EnumSetting<MyPotion> {
    public PotionSetting(String name, String description, MyPotion defaultValue, Consumer<MyPotion> onChanged, Consumer<Setting<MyPotion>> onModuleEnabled, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleEnabled, visible);
    }

    public static class Builder extends EnumSetting.Builder<MyPotion> {
        @Override
        public EnumSetting<MyPotion> build() {
            return new PotionSetting(name, description, defaultValue, onChanged, onModuleEnabled, visible);
        }
    }
}
