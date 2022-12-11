package xyz.mathax.client.systems.modules.client;

import xyz.mathax.client.eventbus.EventHandler;
import xyz.mathax.client.events.mathax.MouseButtonEvent;
import xyz.mathax.client.settings.BoolSetting;
import xyz.mathax.client.settings.Setting;
import xyz.mathax.client.settings.SettingGroup;
import xyz.mathax.client.settings.StringSetting;
import xyz.mathax.client.systems.friends.Friend;
import xyz.mathax.client.systems.friends.Friends;
import xyz.mathax.client.systems.modules.Category;
import xyz.mathax.client.systems.modules.Module;
import xyz.mathax.client.utils.input.KeyAction;
import xyz.mathax.client.utils.text.ChatUtils;
import net.minecraft.entity.player.PlayerEntity;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriend extends Module {
    private final SettingGroup messagesSettings = settings.createGroup("Messages");
    private final SettingGroup addMessagesSettings = settings.createGroup("Add Messages");
    private final SettingGroup removeMessagesSettings = settings.createGroup("Remove Messages");

    // Messages

    private final Setting<String> messagePrefixSetting = messagesSettings.add(new StringSetting.Builder()
            .name("Message prefix")
            .description("Prefix in front of the messages.")
            .defaultValue("/msg")
            .build()
    );

    // Add Messages

    private final Setting<Boolean> addMessagesSetting = addMessagesSettings.add(new BoolSetting.Builder()
            .name("Enabled")
            .description("Send a message to the player when you add them as a friend.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> addMessageSetting = addMessagesSettings.add(new StringSetting.Builder()
            .name("Message")
            .description("The message sent to the player.")
            .defaultValue("I just friended you on MatHax.")
            .build()
    );

    // Remove Messages

    private final Setting<Boolean> removeMessagesSetting = removeMessagesSettings.add(new BoolSetting.Builder()
            .name("Enabled")
            .description("Send a message to the player when you remove them from friends.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> removeMessageSetting = removeMessagesSettings.add(new StringSetting.Builder()
            .name("Message")
            .description("The message sent to the player.")
            .defaultValue("I just unfriended you on MatHax.")
            .build()
    );

    public MiddleClickFriend(Category category) {
        super(category, "Middle Click Friend", "Adds or removes a player as a friend via middle click.");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity player)) {
            return;
        }

        if (!Friends.get().contains(player)) {
            Friends.get().add(new Friend(player));
            info("Added (highlight)%s(default) to friends.", player.getEntityName());
            if (addMessagesSetting.get()) {
                ChatUtils.sendPlayerMessage(messagePrefixSetting.get() + " " + player.getEntityName() + " " + addMessageSetting.get());
            }
        } else {
            Friends.get().remove(Friends.get().get(player));
            info("Removed (highlight)%s(default) from friends.", player.getEntityName());
            if (removeMessagesSetting.get()) {
                ChatUtils.sendPlayerMessage(messagePrefixSetting.get() + " " + player.getEntityName() + " " + removeMessageSetting.get());
            }
        }
    }
}
