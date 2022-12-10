package mathax.client.systems.modules;

import mathax.client.utils.render.color.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Category {
    public final String name;

    public final Item icon;

    public final Color color;

    private final int nameHash;

    public Category(String name, Item icon, Color color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.nameHash = name.hashCode();
    }

    public ItemStack getIconAsItemStack() {
        return icon.getDefaultStack();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Category category = (Category) object;
        return nameHash == category.nameHash;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }
}
