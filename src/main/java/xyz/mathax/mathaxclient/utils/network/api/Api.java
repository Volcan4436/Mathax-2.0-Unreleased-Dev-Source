package xyz.mathax.mathaxclient.utils.network.api;

import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.misc.ISerializable;
import xyz.mathax.mathaxclient.utils.network.Http;

public class Api implements ISerializable<Api> {
    public static Account loggedAccount = null;

    public static String token = "";

    public static void login(String usernameOrEmail, String password) {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            return;
        }

        String formattedURL = String.format(MatHax.API_URL + "/", usernameOrEmail, password);
        JSONObject json = getJSON(formattedURL);
        if (json == null) {
            return;
        }

        if (json.has("error")) {
            MatHax.LOG.error(json.getString("error"));
            return;
        }

        token = json.getString("token");
        loggedAccount = new Account(token);
    }

    public static JSONObject getVersions() {
        return getJSON(MatHax.API_URL + "/versions/metadata.json");
    }

    public static JSONObject getCapes() {
        return getJSON(MatHax.API_URL + "/capes/metadata.json");
    }

    public static JSONObject getJSON(String URL, String bearer) {
        String response = Http.get(URL).bearer(bearer).sendString();
        if (response == null) {
            return null;
        }

        return new JSONObject(response);
    }

    public static JSONObject getJSON(String URL) {
        return getJSON(URL, "");
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("token", token);
        return json;
    }

    @Override
    public Api fromJson(JSONObject json) {
        if (json.has("token")) {
            token = json.getString("token");
        }

        return this;
    }
}
