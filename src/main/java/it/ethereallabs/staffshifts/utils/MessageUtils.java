package it.ethereallabs.staffshifts.utils;

import it.ethereallabs.staffshifts.StaffShifts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageUtils {

    private MessageUtils() {}

    private static FileConfiguration messagesConfig;

    public static void loadMessages() {
        File file = new File(StaffShifts.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) {
            StaffShifts.getInstance().saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static String getMessage(String key, Object... args) {
        if (messagesConfig == null) {
            loadMessages();
        }

        String prefix = messagesConfig.getString("prefix", "&eStaffShifts &8⇒ &7");
        String message = messagesConfig.getString(key, key);

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(args[i]));
            }
        }

        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public static void sendMessage(CommandSender sender, String messageKey, Object... args) {
        String finalMessage = getMessage(messageKey, args);
        sender.sendMessage(finalMessage);
    }
}