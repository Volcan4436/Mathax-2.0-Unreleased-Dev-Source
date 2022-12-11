package xyz.mathax.client.events.mathax;

public class EnabledModulesChangedEvent {
    private static final EnabledModulesChangedEvent INSTANCE = new EnabledModulesChangedEvent();

    public static EnabledModulesChangedEvent get() {
        return INSTANCE;
    }
}
