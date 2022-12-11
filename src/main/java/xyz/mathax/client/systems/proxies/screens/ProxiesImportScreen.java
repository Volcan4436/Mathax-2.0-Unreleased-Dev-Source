package xyz.mathax.client.systems.proxies.screens;

import xyz.mathax.client.systems.themes.Theme;
import xyz.mathax.client.gui.WindowScreen;
import xyz.mathax.client.gui.widgets.containers.WVerticalList;
import xyz.mathax.client.gui.widgets.pressable.WButton;
import xyz.mathax.client.systems.proxies.Proxies;
import xyz.mathax.client.systems.proxies.Proxy;
import xyz.mathax.client.systems.proxies.ProxyType;
import xyz.mathax.client.utils.Utils;
import xyz.mathax.client.utils.render.color.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;

public class ProxiesImportScreen extends WindowScreen {

    private final File file;
    public ProxiesImportScreen(Theme theme, File file) {
        super(theme, "Import Proxies");
        this.file = file;
        this.onClosed(() -> {
            if (parent instanceof ProxiesScreen screen) {
                screen.reload();
            }
        });
    }

    @Override
    public void initWidgets() {
        if (file.exists() && file.isFile()) {
            add(theme.label("Importing proxies from " + file.getName() + "...").color(Color.GREEN));

            WVerticalList list = add(theme.section("Log", false)).widget().add(theme.verticalList()).expandX().widget();

            Proxies proxies = Proxies.get();
            try {
                int importedProxies = 0, invalidProxies = 0;
                for (String line : Files.readAllLines(file.toPath())) {
                    Matcher matcher = Proxies.PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String address = matcher.group(2).replaceAll("\\b0+\\B", "");
                        int port = Integer.parseInt(matcher.group(3));
                        Proxy proxy = new Proxy.Builder().address(address).port(port).name(matcher.group(1) != null ? matcher.group(1) : address + ":" + port).type(matcher.group(4) != null ? ProxyType.parse(matcher.group(4)) : ProxyType.Socks4).build();
                        if (proxies.add(proxy)) {
                            list.add(theme.label("Imported proxy: " + proxy.nameSetting.get()).color(Color.GREEN));
                            importedProxies++;
                        } else {
                            list.add(theme.label("Proxy already exists: " + proxy.nameSetting.get()).color(Color.ORANGE));
                            invalidProxies++;
                        }
                    } else {
                        list.add(theme.label("Invalid proxy: " + line).color(Color.RED));
                        invalidProxies++;
                    }
                }
                add(theme.label("Successfully imported " + importedProxies + "/" + (invalidProxies + importedProxies) + " proxies.").color(Utils.lerp(Color.RED, Color.GREEN, (float) importedProxies / (importedProxies + invalidProxies))));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            add(theme.label("Invalid file!"));
        }

        add(theme.horizontalSeparator()).expandX();

        WButton btnBack = add(theme.button("Back")).expandX().widget();
        btnBack.action = this::close;
    }
}
