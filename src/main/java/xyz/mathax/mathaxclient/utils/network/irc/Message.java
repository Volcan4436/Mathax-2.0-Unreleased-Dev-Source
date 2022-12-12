package xyz.mathax.mathaxclient.utils.network.irc;

import com.google.gson.Gson;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;

public class Message {
    public MessageType type;
    public HashMap<String, String> data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public static Message auth(String pub_key, SecretKey secret, int iv) throws Exception {
        String key = Base64.getEncoder().encodeToString(secret.getEncoded());
        Message obj = new Message(MessageType.AUTH);
        obj.data.put("user", IrcClient.username);
        obj.data.put("token", CryptUtils.encryptRSA(iv + "|" + key, pub_key));
        return obj;
    }

    public static Message broadcast(String message) {
        Message obj = new Message(MessageType.BROADCAST);
        obj.data.put("message", message);
        obj.data.put("from", IrcClient.username);
        return obj;
    }

    public static Message directMessage(String to, String message) {
        Message obj = new Message(MessageType.DIRECT_MESSAGE);
        obj.data.put("message", message);
        obj.data.put("from", IrcClient.username);
        obj.data.put("to", to);
        return obj;
    }

    public static Message ping() {return new Message(MessageType.PING);}

    public Message encrypt(SecretKey secret, int iv) throws Exception {
        if (this.data.containsKey("message")) {
            this.data.put("message", CryptUtils.encryptAES(this.data.get("message"), secret, iv));
        }
        return this;
    }

    public Message decrypt(SecretKey secret, int iv) throws Exception {
        if (this.data.containsKey("message")) {
            this.data.put("message", CryptUtils.decryptAES(this.data.get("message"), secret, iv));
        }
        return this;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static Message fromJSON(String json) {
        return new Gson().fromJson(json, Message.class);
    }
}