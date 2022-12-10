package mathax.client.settings;

import mathax.client.utils.json.JSONUtils;
import mathax.client.utils.settings.IVisible;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemListSetting extends Setting<List<Item>> {
    public final Predicate<Item> filter;

    private final boolean bypassFilterWhenSavingAndLoading;

    public ItemListSetting(String name, String description, List<Item> defaultValue, Consumer<List<Item>> onChanged, Consumer<Setting<List<Item>>> onModuleActivated, IVisible visible, Predicate<Item> filter, boolean bypassFilterWhenSavingAndLoading) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
        this.bypassFilterWhenSavingAndLoading = bypassFilterWhenSavingAndLoading;
    }

    @Override
    protected List<Item> parseImpl(String string) {
        String[] values = string.split(",");
        List<Item> items = new ArrayList<>(values.length);

        try {
            for (String value : values) {
                Item item = parseId(Registry.ITEM, value);
                if (item != null && (filter == null || filter.test(item))) {
                    items.add(item);
                }
            }
        } catch (Exception ignored) {}

        return items;
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected boolean isValueValid(List<Item> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Registry.ITEM.getIds();
    }

    @Override
    public JSONObject save(JSONObject json) {
        json.put("value", new JSONArray());
        for (Item item : get()) {
            if (bypassFilterWhenSavingAndLoading || (filter == null || filter.test(item))) {
                json.append("value", Registry.ITEM.getId(item).toString());
            }
        }

        return json;
    }

    @Override
    public List<Item> load(JSONObject json) {
        get().clear();

        if (json.has("value") && JSONUtils.isValidJSONArray(json, "value")) {
            for (Object object : json.getJSONArray("value")) {
                if (object instanceof String id) {
                    Item item = Registry.ITEM.get(new Identifier(id));
                    if (bypassFilterWhenSavingAndLoading || (filter == null || filter.test(item))) {
                        get().add(item);
                    }
                }
            }
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, List<Item>, ItemListSetting> {
        private Predicate<Item> filter;
        private boolean bypassFilterWhenSavingAndLoading;

        public Builder() {
            super(new ArrayList<>(0));
        }

        public Builder defaultValue(Item... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        public Builder filter(Predicate<Item> filter) {
            this.filter = filter;
            return this;
        }

        public Builder bypassFilterWhenSavingAndLoading() {
            this.bypassFilterWhenSavingAndLoading = true;
            return this;
        }

        @Override
        public ItemListSetting build() {
            return new ItemListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter, bypassFilterWhenSavingAndLoading);
        }
    }
}
