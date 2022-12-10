package mathax.client.utils.gui;

import mathax.client.utils.misc.ISerializable;
import org.json.JSONObject;

public class WindowConfig implements ISerializable<WindowConfig> {
    public boolean expanded = true;

    public double x = -1, y = -1;

    // Saving

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("expanded", expanded);
        json.put("x", x);
        json.put("y", y);

        return json;
    }

    @Override
    public WindowConfig fromJson(JSONObject json) {
        expanded = json.getBoolean("expanded");
        x = json.getDouble("x");
        y = json.getDouble("y");

        return this;
    }
}