package xyz.mathax.mathaxclient.utils.network.irc;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.json.JSONUtils;
import xyz.mathax.mathaxclient.utils.misc.ISerializable;
import xyz.mathax.mathaxclient.utils.network.api.Api;
import xyz.mathax.mathaxclient.utils.render.color.Color;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static xyz.mathax.mathaxclient.MatHax.mc;

public class IrcClient {
    protected static String username;
    protected static String password;

    public static MutableText prefix = Text.literal("[IRC] ").formatted(Formatting.BLUE);

    public static IrcClientEndpoint endpoint = null;

    static {
        File file = new File(MatHax.FOLDER, "IRC.json");
        if (file.exists()) {
            JSONObject json = JSONUtils.loadJSON(file);
            if (json.has("username") && json.has("password")) {
                username = json.getString("username");
                password = json.getString("password");
            }
        }
    }

    public static void sendToChat(Text text) {
        mc.inGameHud.getChatHud().addMessage(prefix.copy().append(text));
    }

    private static void updateAuth(String username, String password) {
        IrcClient.username = username;
        IrcClient.password = password;

        File file = new File(MatHax.FOLDER, "IRC.json");
        file.getParentFile().mkdir();

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        JSONUtils.saveJSON(json, file);
    }

    public static void setAuth(String username, String password) {
        if (endpoint != null){
            MutableText text = Text.literal("You can't change your username or password while connected to the IRC server.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        } else if (username.isEmpty() || password.isEmpty()) {
            MutableText text = Text.literal("Username and password can't be empty.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        } else {
            updateAuth(username, password);
            MutableText text = Text.literal("Username and password updated.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.GREEN)));
            sendToChat(text);
        }
    }

    public static void connect() {
        if (username.isEmpty() || password.isEmpty()) {
            MutableText text = Text.literal("Username and password can't be empty. Use .irc auth <username> <password> to set them.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        } else if (endpoint == null) {
            try {
                endpoint = new IrcClientEndpoint(new URI("ws://51.161.192.31:8107/irc"));
                endpoint.connect();
            } catch (URISyntaxException exception) {
                exception.printStackTrace();
            }
        } else {
            MutableText text = Text.literal("You are already connected to the IRC server.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        }
    }

    public static void disconnect() {
        if (endpoint != null) {
            endpoint.close();
            endpoint = null;
        } else {
            MutableText text = Text.literal("You are not connected to the IRC server.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        }
    }

    public static void send(String message) throws Exception {
        if (endpoint != null) {
            endpoint.sendBroadcast(message);
        } else {
            MutableText text = Text.literal(" You are not connected to the IRC server.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        }
    }

    public static void sendDirect(String user, String message) throws Exception {
        if (endpoint != null) {
            endpoint.sendDirect(user, message);
            sendToChat(Text.literal("To " + user + ": " + message).formatted(Formatting.RED));
        } else {
            MutableText text = Text.literal("You are not connected to the IRC server.");
            text.setStyle(text.getStyle().withColor(Color.fromRGBA(Color.RED)));
            sendToChat(text);
        }
    }
}