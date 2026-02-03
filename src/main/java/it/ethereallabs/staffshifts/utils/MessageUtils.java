package it.ethereallabs.staffshifts.utils;

import it.ethereallabs.staffshifts.StaffShifts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageUtils {

    private static FileConfiguration messagesConfig;

    public static void loadMessages() {
        File file = new File(StaffShifts.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) {
            StaffShifts.getInstance().saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static String getMessage(String key) {
        if (messagesConfig == null) {
            loadMessages();
        }
        String prefix = messagesConfig.getString("prefix", "&eStaffShifts &8⇒&7");
        String message = messagesConfig.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public static void sendMessage(CommandSender sender, String key) {
        sender.sendMessage(getMessage(key));
    }
}