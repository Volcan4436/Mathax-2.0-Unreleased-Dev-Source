package mathax.client.settings;

import mathax.client.utils.misc.MyPotion;
import mathax.client.utils.settings.IVisible;

import java.util.function.Consumer;

public class PotionSetting extends EnumSetting<MyPotion> {
    public PotionSetting(String name, String description, MyPotion defaultValue, Consumer<MyPotion> onChanged, Consumer<Setting<MyPotion>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    public static class Builder extends EnumSetting.Builder<MyPotion> {
        @Override
        public EnumSetting<MyPotion> build() {
            return new PotionSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
