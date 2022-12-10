package mathax.client.gui.widgets;

import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.input.KeyBind;

public class WKeybind extends WHorizontalList {
    public Runnable action;
    public Runnable actionOnSet;

    private WLabel label;

    private final KeyBind keybind;
    private final KeyBind defaultValue;
    private boolean listening;

    public WKeybind(KeyBind keybind, KeyBind defaultValue) {
        this.keybind = keybind;
        this.defaultValue = defaultValue;
    }

    @Override
    public void init() {
        label = add(theme.label("")).widget();

        WButton set = add(theme.button("Set")).widget();
        set.action = () -> {
            listening = true;
            label.set(appendBindText("..."));

            if (actionOnSet != null) {
                actionOnSet.run();
            }
        };

        WButton reset = add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = this::resetBind;

        refreshLabel();
    }

    public boolean onAction(boolean isKey, int value) {
        if (listening && keybind.canBindTo(isKey, value)) {
            keybind.set(isKey, value);
            reset();

            return true;
        }

        return false;
    }

    public void resetBind() {
        keybind.set(defaultValue);
        reset();
    }

    public void reset() {
        listening = false;
        refreshLabel();
        if (Modules.get().isBinding()) {
            Modules.get().setModuleToBind(null);
        }
    }

    private void refreshLabel() {
        label.set(appendBindText(keybind.toString()));
    }

    private String appendBindText(String text) {
        return "Bind: " + text;
    }
}
