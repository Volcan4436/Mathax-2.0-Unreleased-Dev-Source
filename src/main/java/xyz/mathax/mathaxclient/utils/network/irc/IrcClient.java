package xyz.mathax.mathaxclient.utils.network.irc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.systems.config.Config;
import xyz.mathax.mathaxclient.utils.misc.CryptUtils;
import xyz.mathax.mathaxclient.utils.text.ChatUtils;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.SecureRandom;

public class IrcClient extends WebSocketClient {
    protected SecretKey secretKey;

    protected int iv;

    public IrcClient(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        ChatUtils.info("IRC", "Connected.");

        iv = new SecureRandom().nextInt() & Integer.MAX_VALUE;
        try {
            secretKey = CryptUtils.psk2sk(MatHax.API.irc.password, iv);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onMessage(String messageJson) {
        IrcMessage message = new IrcMessage(new JSONObject(messageJson));
        try {
            switch (message.type) {
                case BROADCAST -> {
                    message = message.decrypt(secretKey, iv);
                    ChatUtils.info("IRC", "(highlight)%s(default): %s", message.data.getString("from"), message.data.getString("message"));
                }
                case DIRECT_MESSAGE -> {
                    message = message.decrypt(secretKey, iv);
                    ChatUtils.info("IRC", "From (highlight)%s(default): %s", message.data.getString("from"), message.data.getString("message"));
                }
                case PUB_KEY -> send(IrcMessage.auth(message.data.getString("public_key"), secretKey, iv).toJson().toString(4));
                case PING -> send(IrcMessage.ping().toJson().toString(4));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        ChatUtils.info("IRC", "Disconnected.");
        MatHax.API.irc.ircClient = null;

        Config.get().ircSetting.set(false);
    }

    @Override
    public void onError(Exception exception) {
        MatHax.LOG.info("[IRC] Error: {}", exception.getMessage());
        ChatUtils.error("IRC", "Error: %s", exception.getMessage());
    }

    public void sendBroadcast(String from, String message) {
        send(IrcMessage.broadcast(from, message).encrypt(secretKey, iv).toJson().toString(4));
    }

    public void sendDirect(String from, String to, String message) {
        // TODO: FIX: DISCONNECTS FOR NO REASON??????
        send(IrcMessage.directMessage(from, to, message).encrypt(secretKey, iv).toJson().toString(4));
    }
}