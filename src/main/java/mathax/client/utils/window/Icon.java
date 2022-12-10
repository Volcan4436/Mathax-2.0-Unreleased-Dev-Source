package mathax.client.utils.window;

import mathax.client.utils.misc.MatHaxIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

import static mathax.client.MatHax.mc;

public class Icon {
    public static boolean iconChanged = false;

    public static void setIcon(MatHaxIdentifier icon1, MatHaxIdentifier icon2) {
        try {
            setIcon(mc.getResourceManager().getResource(icon1).get().getInputStream(), mc.getResourceManager().getResource(icon2).get().getInputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void setIcon(InputStream icon1, InputStream icon2) {
        mc.getWindow().setIcon(icon1, icon2);
        iconChanged = true;
    }

    public static void setMinecraft() {
        try {
            mc.getWindow().setIcon(MinecraftClient.getInstance().getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png")), MinecraftClient.getInstance().getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png")));
            iconChanged = false;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
