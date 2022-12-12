package xyz.mathax.mathaxclient.utils.network.api;

import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.json.JSONUtils;
import xyz.mathax.mathaxclient.utils.network.Http;
import xyz.mathax.mathaxclient.utils.network.irc.Irc;

import java.io.File;

public class Api {
    private static final File FOLDER = new File(MatHax.FOLDER, "API");

    public Irc irc;

    public Account loggedAccount = null;

    public String token = "";

    public void createIrc() {
        irc = new Irc();
    }

    public void login(String usernameOrEmail, String password) {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            return;
        }

        String formattedURL = String.format(MatHax.API_URL + "/accounts/login?usernameOrEmail=%s&password=%s", usernameOrEmail, password);
        login(getJSON(formattedURL));
    }

    public void login(String token) {
        if (token.isBlank()) {
            return;
        }

        String formattedURL = String.format(MatHax.API_URL + "/accounts/login?token=%s", token);
        login(getJSON(formattedURL));
    }

    public void login(JSONObject json) {
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

    public JSONObject getVersions() {
        return getJSON(MatHax.API_URL + "/versions/metadata.json");
    }

    public JSONObject getCapes() {
        return getJSON(MatHax.API_URL + "/capes/metadata.json");
    }

    public JSONObject getJSON(String URL, String bearer) {
        String response = Http.get(URL).bearer(bearer).sendString();
        if (response == null) {
            return null;
        }

        return new JSONObject(response);
    }

    public JSONObject getJSON(String URL) {
        return getJSON(URL, "");
    }

    public void save() {
        JSONObject json = new JSONObject();
        json.put("token", token);
        JSONUtils.saveJSON(json, new File(FOLDER, "Account.json"));
    }

    public void load() {
        File file = new File(FOLDER, "Account.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }

        JSONObject json = JSONUtils.loadJSON(file);
        if (json != null && json.has("token")) {
            token = json.getString("token");
            login(token);
        }
    }
}
