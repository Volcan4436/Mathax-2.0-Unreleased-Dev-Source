package mathax.client.settings;

import mathax.client.utils.settings.IVisible;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemSetting extends Setting<Item> {
    public final Predicate<Item> filter;

    public ItemSetting(String name, String description, Item defaultValue, Consumer<Item> onChanged, Consumer<Setting<Item>> onModuleActivated, IVisible visible, Predicate<Item> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    protected Item parseImpl(String string) {
        return parseId(Registry.ITEM, string);
    }

    @Override
    protected boolean isValueValid(Item value) {
        return filter == null || filter.test(value);
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Registry.ITEM.getIds();
    }

    @Override
    public JSONObject save(JSONObject json) {
        json.put("value", Registry.ITEM.getId(get()).toString());

        return json;
    }

    @Override
    public Item load(JSONObject json) {
        if (json.has("value")) {
            value = Registry.ITEM.get(new Identifier(json.getString("value")));

            if (filter != null && !filter.test(value)) {
                for (Item item : Registry.ITEM) {
                    if (filter.test(item)) {
                        value = item;
                        break;
                    }
                }
            }
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Item, ItemSetting> {
        private Predicate<Item> filter;

        public Builder() {
            super(null);
        }

        public Builder filter(Predicate<Item> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public ItemSetting build() {
            return new ItemSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
