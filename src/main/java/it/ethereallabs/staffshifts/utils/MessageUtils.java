package it.ethereallabs.staffshifts.utils;

import it.ethereallabs.staffshifts.StaffShifts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class MessageUtils {

    private MessageUtils() {}

    private static FileConfiguration messagesConfig;

    public static void loadMessages() {
        StaffShifts plugin = StaffShifts.getInstance();
        File file = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(file);

        InputStream defStream = plugin.getResource("messages.yml");
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defConfig);
            messagesConfig.options().copyDefaults(true);
            
            try {
                messagesConfig.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save messages.yml", e);
            }
        }
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