package it.ethereallabs.staffshifts.commands.abs;

import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import java.util.List;

public abstract class BaseCommand implements CommandHandler {
    private final String name;
    private final String usage;

    public BaseCommand(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }

    public String getName() { return name; }

    @Override
    public String getUsage() { return usage; }

    public void sendUsage(CommandSender sender) {
        MessageUtils.sendMessage(sender, "invalid-usage");
        sender.sendMessage("§b> §e/ss " + name + (usage.isEmpty() ? "" : " " + usage));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}