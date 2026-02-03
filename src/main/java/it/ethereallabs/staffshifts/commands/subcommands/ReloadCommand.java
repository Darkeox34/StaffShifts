package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.MANAGEMENT)) {
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }

        StaffShifts.getInstance().reloadConfig();
        MessageUtils.loadMessages();
        StaffShifts.getAFKManager().reloadConfig();
        StaffShifts.getShiftsManager().reloadConfig();

        MessageUtils.sendMessage(sender, "plugin-reloaded");

        return true;
    }
}