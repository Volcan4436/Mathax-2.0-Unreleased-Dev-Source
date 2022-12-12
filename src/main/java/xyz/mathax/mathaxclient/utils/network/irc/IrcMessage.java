package xyz.mathax.mathaxclient.utils.network.irc;

import com.google.gson.Gson;
import xyz.mathax.mathaxclient.utils.misc.CryptUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;

public class IrcMessage {
    public IrcMessageType type;
    public HashMap<String, String> data;

    public IrcMessage(IrcMessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public static IrcMessage auth(String username, String pub_key, SecretKey secret, int iv) throws Exception {
        String key = Base64.getEncoder().encodeToString(secret.getEncoded());
        IrcMessage obj = new IrcMessage(IrcMessageType.AUTH);
        obj.data.put("user", username);
        obj.data.put("token", CryptUtils.encryptRSA(iv + "|" + key, pub_key));
        return obj;
    }

    public static IrcMessage broadcast(String username, String message) {
        IrcMessage obj = new IrcMessage(IrcMessageType.BROADCAST);
        obj.data.put("message", message);
        obj.data.put("from", username);
        return obj;
    }

    public static IrcMessage directMessage(String username, String to, String message) {
        IrcMessage obj = new IrcMessage(IrcMessageType.DIRECT_MESSAGE);
        obj.data.put("message", message);
        obj.data.put("from", username);
        obj.data.put("to", to);
        return obj;
    }

    public static IrcMessage ping() {return new IrcMessage(IrcMessageType.PING);}

    public IrcMessage encrypt(SecretKey secret, int iv) throws Exception {
        if (this.data.containsKey("message")) {
            this.data.put("message", CryptUtils.encryptAES(this.data.get("message"), secret, iv));
        }

        return this;
    }

    public IrcMessage decrypt(SecretKey secret, int iv) throws Exception {
        if (this.data.containsKey("message")) {
            this.data.put("message", CryptUtils.decryptAES(this.data.get("message"), secret, iv));
        }

        return this;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static IrcMessage fromJSON(String json) {
        return new Gson().fromJson(json, IrcMessage.class);
    }
}