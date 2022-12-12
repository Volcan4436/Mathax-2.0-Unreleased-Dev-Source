package xyz.mathax.mathaxclient.utils.network.irc;

import org.json.JSONObject;
import xyz.mathax.mathaxclient.MatHax;
import xyz.mathax.mathaxclient.utils.misc.CryptUtils;
import xyz.mathax.mathaxclient.utils.misc.ISerializable;

import javax.crypto.SecretKey;
import java.util.Base64;

public class IrcMessage implements ISerializable<IrcMessage> {
    public IrcMessageType type;

    public JSONObject data;

    public IrcMessage(IrcMessageType type) {
        this.type = type;
        this.data = new JSONObject();
    }

    public IrcMessage(JSONObject json) {
        fromJson(json);
    }

    public static IrcMessage auth(String pub_key, SecretKey secret, int iv) {
        try {
            String key = Base64.getEncoder().encodeToString(secret.getEncoded());
            IrcMessage ircMessage = new IrcMessage(IrcMessageType.AUTH);
            ircMessage.data.put("token", CryptUtils.encryptRSA(iv + "|" + key, pub_key));
            return ircMessage;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static IrcMessage broadcast(String from, String message) {
        IrcMessage ircMessage = new IrcMessage(IrcMessageType.BROADCAST);
        ircMessage.data.put("from", from);
        ircMessage.data.put("message", message);
        return ircMessage;
    }

    public static IrcMessage directMessage(String from, String to, String message) {
        IrcMessage ircMessage = new IrcMessage(IrcMessageType.DIRECT_MESSAGE);
        ircMessage.data.put("from", from);
        ircMessage.data.put("to", to);
        ircMessage.data.put("message", message);
        return ircMessage;
    }

    public static IrcMessage ping() {
        return new IrcMessage(IrcMessageType.PING);
    }

    public IrcMessage encrypt(SecretKey secret, int iv) {
        try {
            if (data.has("message")) {
                data.put("message", CryptUtils.encryptAES(data.getString("message"), secret, iv));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return this;
    }

    public IrcMessage decrypt(SecretKey secret, int iv) {
        try {
            if (data.has("message")) {
                data.put("message", CryptUtils.decryptAES(data.getString("message"), secret, iv));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return this;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", type.toString());
        json.put("data", data);
        return json;
    }

    @Override
    public IrcMessage fromJson(JSONObject json) {
        if (json.has("type") && json.has("data")) {
            try {
                type = IrcMessageType.valueOf(json.getString("type"));
            } catch (IllegalArgumentException ignored) {}

            data = json.getJSONObject("data");
        }

        return this;
    }
}