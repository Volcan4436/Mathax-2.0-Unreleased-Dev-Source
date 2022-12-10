package mathax.client.settings.screens;

import mathax.client.systems.themes.Theme;
import mathax.client.gui.WindowScreen;
import mathax.client.utils.gui.Cell;
import mathax.client.gui.widgets.WLabel;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.containers.WView;
import mathax.client.gui.widgets.input.WDropdown;
import mathax.client.gui.widgets.input.WTextBox;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.renderer.text.Fonts;
import mathax.client.renderer.text.FontFace;
import mathax.client.renderer.text.FontFamily;
import mathax.client.settings.FontFaceSetting;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FontFaceSettingScreen extends WindowScreen {
    private final FontFaceSetting setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public FontFaceSettingScreen(Theme theme, FontFaceSetting setting) {
        super(theme, "Select Font");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        filter = add(theme.textBox("")).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initTable();
        };

        window.view.hasScrollBar = false;

        enterAction = () -> {
            List<Cell<?>> row = table.getRow(0);
            if (row == null) {
                return;
            }

            WWidget widget = row.get(2).widget();
            if (widget instanceof WButton button) {
                button.action.run();
            }
        };

        WView view = add(theme.view()).expandX().widget();
        view.scrollOnlyWhenMouseOver = false;
        table = view.add(theme.table()).expandX().widget();

        initTable();
    }

    private void initTable() {
        for (FontFamily fontFamily : Fonts.FONT_FAMILIES) {
            String name = fontFamily.getName();

            WLabel item = theme.label(name);
            if (!filterText.isEmpty() && !StringUtils.containsIgnoreCase(name, filterText)) {
                continue;
            }

            table.add(item);

            WDropdown<FontFace.Type> dropdown = table.add(theme.dropdown(FontFace.Type.Regular)).right().widget();

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(fontFamily.get(dropdown.get()));
                close();
            };

            table.row();
        }
    }
}
