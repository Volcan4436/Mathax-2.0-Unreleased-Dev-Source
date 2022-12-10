package mathax.client.settings;

import mathax.client.utils.gui.IScreenFactory;
import mathax.client.utils.misc.ICopyable;
import mathax.client.utils.misc.ISerializable;
import mathax.client.utils.settings.IVisible;
import org.json.JSONObject;

import java.util.function.Consumer;

public class GenericSetting<T extends ICopyable<T> & ISerializable<T> & IScreenFactory> extends Setting<T> {
    public GenericSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    public void resetImpl() {
        if (value == null) {
            value = defaultValue.copy();
        }

        value.set(defaultValue);
    }

    @Override
    protected T parseImpl(String string) {
        return defaultValue.copy();
    }

    @Override
    protected boolean isValueValid(T value) {
        return true;
    }

    @Override
    public JSONObject save(JSONObject json) {
        json.put("value", get().toJson());

        return json;
    }

    @Override
    public T load(JSONObject json) {
        get().fromJson(json.getJSONObject("value"));

        return get();
    }

    public static class Builder<T extends ICopyable<T> & ISerializable<T> & IScreenFactory> extends SettingBuilder<Builder<T>, T, GenericSetting<T>> {
        public Builder() {
            super(null);
        }

        @Override
        public GenericSetting<T> build() {
            return new GenericSetting<>(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
