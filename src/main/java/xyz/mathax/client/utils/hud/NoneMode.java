package xyz.mathax.client.utils.hud;

public enum NoneMode {
    Hide_Item("Hide item"),
    Hide_Count("Hide count"),
    Show_Count("Show count");

    private final String name;

    NoneMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
