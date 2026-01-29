package it.ethereallabs.staffshifts.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {
    public static void sendMessage(CommandSender sender, String message) {
        String finalMessage = "&eStaffShifts &8⇒ &7" + message;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', finalMessage));
    }
}
