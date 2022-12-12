package xyz.mathax.mathaxclient.utils.network.irc;

import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.eventbus.EventHandler;
import xyz.mathax.mathaxclient.events.game.GameJoinedEvent;
import xyz.mathax.mathaxclient.events.game.GameLeftEvent;
import xyz.mathax.mathaxclient.events.world.TickEvent;
import xyz.mathax.mathaxclient.utils.Utils;
import xyz.mathax.mathaxclient.utils.json.JSONUtils;
import xyz.mathax.mathaxclient.utils.text.ChatUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

// TODO: Make IRC MatHax account based.
// TODO: Remove settings saving and loading, won't be needed with accounts.

public class Irc {
    protected String username = "";
    protected String password = "";

    public IrcClient ircClient = null;

    private boolean enabled = false;

    public Irc() {
        File file = new File(MatHax.FOLDER, "IRC.json");
        if (file.exists()) {
            JSONObject json = JSONUtils.loadJSON(file);
            if (json.has("username") && json.has("password")) {
                username = json.getString("username");
                password = json.getString("password");
            }
        }

        MatHax.EVENT_BUS.subscribe(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void forceToggle(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        if (enabled && ircClient == null) {
            connect();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!Utils.canUpdate()) {
            return;
        }

        if (enabled) {
            if (ircClient == null) {
                connect();
            }
        } else if (ircClient != null) {
            disconnect();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (ircClient != null) {
            disconnect();
        }
    }

    private void updateAuth(String username, String password) {
        this.username = username;
        this.password = password;

        File file = new File(MatHax.FOLDER, "IRC.json");
        file.getParentFile().mkdir();

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        JSONUtils.saveJSON(json, file);
    }

    public void setAuth(String username, String password) {
        if (ircClient != null){
            ChatUtils.error("IRC", "You can't change your username or password while connected.");
        } else if (username.isEmpty() || password.isEmpty()) {
            ChatUtils.error("IRC", "Username and password can't be empty.");
        } else {
            updateAuth(username, password);
            ChatUtils.info("IRC", "Username and password updated.");
        }
    }

    public void connect() {
        if (username.isEmpty() || password.isEmpty()) {
            ChatUtils.error("IRC", "Username and password can't be empty. Use .irc auth <username> <password> to set them.");
        } else if (ircClient == null) {
            try {
                ircClient = new IrcClient(new URI("ws://51.161.192.31:8107/irc"));
                ircClient.connect();

                enabled = true;
            } catch (URISyntaxException exception) {
                exception.printStackTrace();
            }
        } else {
            ChatUtils.error("IRC", "You are already connected.");
        }
    }

    public void disconnect() {
        if (ircClient != null) {
            ircClient.close();
            ircClient = null;
        } else {
            ChatUtils.error("IRC", "You are not connected.");
        }
    }

    public void send(String message) {
        if (ircClient != null) {
            ircClient.sendBroadcast(username, message);
        } else {
            ChatUtils.error("IRC", "You are not connected.");
        }
    }

    public void sendDirect(String user, String message) {
        if (ircClient != null) {
            if (user.equals(username)) {
                ChatUtils.error("IRC", "You can't direct message yourself.");
                return;
            }

            ircClient.sendDirect(username, user, message);
            ChatUtils.info("IRC", "To (highlight)%s(default): %s", user, message);
        } else {
            ChatUtils.error("IRC", "You are not connected.");
        }
    }
}