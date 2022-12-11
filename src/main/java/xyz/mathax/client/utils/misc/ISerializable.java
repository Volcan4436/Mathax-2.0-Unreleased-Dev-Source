package xyz.mathax.client.utils.misc;

import org.json.JSONObject;

public interface ISerializable<T> {
    JSONObject toJson();

    T fromJson(JSONObject json);
}
