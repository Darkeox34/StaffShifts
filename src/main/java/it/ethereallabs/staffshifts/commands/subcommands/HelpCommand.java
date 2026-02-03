package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.commands.abs.CommandHandler;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.command.CommandSender;
import java.util.Map;

public class HelpCommand extends BaseCommand {
    private final Map<String, CommandHandler> commandMap;

    public HelpCommand(Map<String, CommandHandler> commandMap) {
        super("help", "");
        this.commandMap = commandMap;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.MANAGEMENT) || !sender.hasPermission(Permissions.STAFFER)){
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }
        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        String line = "§7§m" + " ".repeat(32);

        sender.sendMessage(line + " §r §bStaffShifts " + line);

        commandMap.forEach((name, handler) -> {
            if (name.equalsIgnoreCase("help")) return;

            String usage = handler.getUsage();
            String message = "§b> §a/ss " + name + (usage.isEmpty() ? "" : " " + usage);
            sender.sendMessage(message);
        });

        String separator = "§7§m" + " ".repeat(80);

        sender.sendMessage("§7§m" + separator);
    }
}