package xyz.mathax.mathaxclient.utils.settings;

public enum ListMode {
    Whitelist("Whitelist"),
    Blacklist("Blacklist");

    private final String title;

    ListMode(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
