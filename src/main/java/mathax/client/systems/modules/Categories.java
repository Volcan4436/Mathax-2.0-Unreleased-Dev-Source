package mathax.client.systems.modules;

import mathax.client.utils.render.color.Color;
import net.minecraft.item.Items;

public class Categories {
    public static final Category Combat = new Category("Combat", Items.DIAMOND_SWORD, new Color(225, 0, 0, 255));
    public static final Category Movement = new Category("Movement", Items.DIAMOND_BOOTS, new Color(0, 125, 255, 255));
    public static final Category Render = new Category("Render", Items.SPYGLASS, new Color(125, 255, 255, 255));
    public static final Category Player = new Category("Player", Items.ARMOR_STAND, new Color(245, 255, 100, 255));
    public static final Category World = new Category("World", Items.GRASS_BLOCK, new Color(0, 150, 0, 255));
    public static final Category Chat = new Category("Chat", Items.OAK_SIGN, new Color(255, 255, 255, 255));
    public static final Category Misc = new Category("Misc", Items.BEACON, new Color(0, 50, 175, 255));
    public static final Category Client = new Category("Client", Items.COMMAND_BLOCK, Color.MATHAX);

    public static boolean REGISTERING;

    public static void init() {
        REGISTERING = true;

        Modules.registerCategory(Combat);
        Modules.registerCategory(Movement);
        Modules.registerCategory(Render);
        Modules.registerCategory(Player);
        Modules.registerCategory(World);
        Modules.registerCategory(Chat);
        Modules.registerCategory(Misc);
        Modules.registerCategory(Client);

        REGISTERING = false;
    }
}
