package xyz.mathax.client.settings;

import xyz.mathax.client.gui.widgets.input.WTextBox;
import xyz.mathax.client.utils.gui.CharFilter;
import xyz.mathax.client.utils.settings.IVisible;
import org.json.JSONObject;

import java.util.function.Consumer;

public class StringSetting extends Setting<String> {
    public final Class<? extends WTextBox.Renderer> renderer;

    public final CharFilter filter;

    public final boolean wide;

    public StringSetting(String name, String description, String defaultValue, Consumer<String> onChanged, Consumer<Setting<String>> onModuleActivated, IVisible visible, Class<? extends WTextBox.Renderer> renderer, CharFilter filter, boolean wide) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.renderer = renderer;
        this.filter = filter;
        this.wide = wide;
    }

    @Override
    protected String parseImpl(String string) {
        return string;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }

    @Override
    public JSONObject save(JSONObject json) {
        json.put("value", get());

        return json;
    }

    @Override
    public String load(JSONObject json) {
        set(json.getString("value"));

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, String, StringSetting> {
        private Class<? extends WTextBox.Renderer> renderer;

        private CharFilter filter;

        private boolean wide;

        public Builder() {
            super(null);
        }

        public Builder renderer(Class<? extends WTextBox.Renderer> renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder filter(CharFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder wide() {
            wide = true;
            return this;
        }

        @Override
        public StringSetting build() {
            return new StringSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, renderer, filter, wide);
        }
    }
}