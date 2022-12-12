package xyz.mathax.mathaxclient.utils.network.irc;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.SecureRandom;

public class IrcClientEndpoint extends WebSocketClient {
    protected SecretKey secretKey;

    protected int iv;

    public IrcClientEndpoint(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        IrcClient.sendToChat(Text.literal("Connected to IRC server."));
        iv = new SecureRandom().nextInt() & Integer.MAX_VALUE;
        try {
            secretKey = CryptUtils.psk2sk(IrcClient.password, iv);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onMessage(String message) {
        Message msg = Message.fromJSON(message);
        try {
            switch (msg.type) {
                case BROADCAST -> {
                    msg = msg.decrypt(this.secretKey, this.iv);
                    IrcClient.sendToChat(Text.literal(msg.data.get("from") + ": " + msg.data.get("message")).formatted(Formatting.WHITE));
                }
                case DIRECT_MESSAGE -> {
                    msg = msg.decrypt(this.secretKey, this.iv);
                    IrcClient.sendToChat(Text.literal("From " + msg.data.get("from") + ": ").formatted(Formatting.RED).append(Text.literal(msg.data.get("message"))));
                }
                case PUB_KEY -> send(Message.auth(msg.data.get("public_key"), this.secretKey, this.iv).toJSON());
                case PING -> send(Message.ping().toJSON());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        IrcClient.sendToChat(Text.literal("Disconnected from IRC server").formatted(Formatting.RED));
        IrcClient.endpoint = null;
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        IrcClient.sendToChat(Text.literal("Error: " + exception.getMessage()).formatted(Formatting.RED));
    }

    public void sendDirect(String to, String message) throws Exception {
        send(Message.directMessage(to, message).encrypt(this.secretKey, this.iv).toJSON());
    }

    public void sendBroadcast(String message) throws Exception {
        send(Message.broadcast(message).encrypt(this.secretKey, this.iv).toJSON());
    }
}