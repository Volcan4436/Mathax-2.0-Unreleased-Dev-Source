package xyz.mathax.mathaxclient.utils.network.irc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.misc.CryptUtils;
import xyz.mathax.mathaxclient.utils.text.ChatUtils;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.SecureRandom;

public class IrcEndpoint extends WebSocketClient {
    protected SecretKey secretKey;

    protected int iv;

    public IrcEndpoint(URI uri) {
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
    public void onMessage(String message) {
        IrcMessage msg = IrcMessage.fromJSON(message);
        try {
            switch (msg.type) {
                case BROADCAST -> {
                    msg = msg.decrypt(this.secretKey, this.iv);
                    ChatUtils.info("IRC", "(highlight)%s(default): %s", msg.data.get("from"), msg.data.get("message"));
                }
                case DIRECT_MESSAGE -> {
                    msg = msg.decrypt(this.secretKey, this.iv);
                    ChatUtils.info("IRC", "From (highlight)%s(default): %s", msg.data.get("from"), msg.data.get("message"));
                }
                case PUB_KEY -> send(IrcMessage.auth(msg.data.get("user"), msg.data.get("public_key"), this.secretKey, this.iv).toJSON());
                case PING -> send(IrcMessage.ping().toJSON());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        ChatUtils.info("IRC", "Disonnected.");
        MatHax.API.irc.endpoint = null;
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        ChatUtils.error("IRC", "Error: %s", exception.getMessage());
    }

    public void sendDirect(String from, String to, String message) throws Exception {
        send(IrcMessage.directMessage(from, to, message).encrypt(this.secretKey, this.iv).toJSON());
    }

    public void sendBroadcast(String from, String message) throws Exception {
        send(IrcMessage.broadcast(from, message).encrypt(this.secretKey, this.iv).toJSON());
    }
}