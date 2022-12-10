package mathax.client.systems.proxies.screens;

import mathax.client.settings.Settings;
import mathax.client.systems.screens.EditSystemScreen;
import mathax.client.systems.themes.Theme;
import mathax.client.gui.WindowScreen;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.widgets.WLabel;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.gui.widgets.pressable.WCheckbox;
import mathax.client.gui.widgets.pressable.WMinus;
import mathax.client.systems.proxies.Proxies;
import mathax.client.systems.proxies.Proxy;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static mathax.client.MatHax.mc;

public class ProxiesScreen extends WindowScreen {
    private final List<WCheckbox> checkboxes = new ArrayList<>();

    public ProxiesScreen(Theme theme) {
        super(theme, "Proxies");
    }

    public void initWidgets() {
        WTable table = add(theme.table()).expandX().minWidth(400).widget();
        initTable(table);

        add(theme.horizontalSeparator()).expandX();

        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        // New
        WButton newBtn = l.add(theme.button("New")).expandX().widget();
        newBtn.action = () -> mc.setScreen(new EditProxyScreen(theme, null, this::reload));

        // Import
        PointerBuffer filters = BufferUtils.createPointerBuffer(1);

        ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");

        filters.put(txtFilter);
        filters.rewind();

        WButton importBtn = l.add(theme.button("Import")).expandX().widget();
        importBtn.action = () -> {
            String selectedFile = TinyFileDialogs.tinyfd_openFileDialog("Import Proxies", null, filters, null, false);
            if (selectedFile != null) {
                File file = new File(selectedFile);
                mc.setScreen(new ProxiesImportScreen(theme, file));
            }
        };
    }

    private void initTable(WTable table) {
        table.clear();

        if (Proxies.get().isEmpty()) {
            return;
        }

        for (Proxy proxy : Proxies.get()) {
            WCheckbox enabled = table.add(theme.checkbox(proxy.enabledSetting.get())).widget();
            checkboxes.add(enabled);
            enabled.action = () -> {
                boolean checked = enabled.checked;
                Proxies.get().setEnabled(proxy, checked);

                for (WCheckbox checkbox : checkboxes) checkbox.checked = false;
                enabled.checked = checked;
            };

            WLabel name = table.add(theme.label(proxy.nameSetting.get())).widget();
            name.color = theme.textColor();

            WLabel type = table.add(theme.label("(" + proxy.typeSetting.get() + ")")).widget();
            type.color = theme.textSecondaryColor();

            WHorizontalList ipList = table.add(theme.horizontalList()).expandCellX().widget();
            ipList.spacing = 0;

            ipList.add(theme.label(proxy.addressSetting.get()));
            ipList.add(theme.label(":")).widget().color = theme.textSecondaryColor();
            ipList.add(theme.label(Integer.toString(proxy.portSetting.get())));

            WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> mc.setScreen(new EditProxyScreen(theme, proxy, this::reload));

            WMinus remove = table.add(theme.minus()).widget();
            remove.action = () -> {
                Proxies.get().remove(proxy);
                reload();
            };

            table.row();
        }
    }

    protected static class EditProxyScreen extends EditSystemScreen<Proxy> {
        public EditProxyScreen(Theme theme, Proxy value, Runnable reload) {
            super(theme, value, reload);
        }

        @Override
        public Proxy create() {
            return new Proxy.Builder().build();
        }

        @Override
        public boolean save() {
            return value.resolveAddress() && (!isNew || Proxies.get().add(value));
        }

        @Override
        public Settings getSettings() {
            return value.settings;
        }
    }
}
