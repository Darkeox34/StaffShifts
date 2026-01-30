package it.ethereallabs.staffshifts.commands;

import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.commands.abs.CommandHandler;
import it.ethereallabs.staffshifts.commands.subcommands.ShiftCommand;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class CommandRegistry implements CommandExecutor, TabCompleter {
    private final Map<String, CommandHandler> commands = new HashMap<>();

    public CommandRegistry() {
        registerCommand(new ShiftCommand());
    }

    private void registerCommand(BaseCommand handler) {
        commands.put(handler.getName(), handler);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("staffshifts.commands")) {
            MessageUtils.sendMessage(sender, "&4You don't have permission to execute this command!");
            return true;
        }

        if(args.length == 0) {
            return true;
        }

        String subCommand = args[0].toLowerCase();
        CommandHandler handler = commands.get(subCommand);

        if (handler != null) {
            return handler.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            MessageUtils.sendMessage(sender, "&4Command not found!");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            for (String key : commands.keySet()) {
                if (key.startsWith(args[0].toLowerCase())) {
                    result.add(key);
                }
            }
            return result;
        }

        String subCommand = args[0].toLowerCase();
        CommandHandler handler = commands.get(subCommand);
        if (handler == null) {
            return Collections.emptyList();
        }

        return handler.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
