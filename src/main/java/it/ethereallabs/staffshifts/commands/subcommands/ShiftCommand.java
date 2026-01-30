package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.gui.StaffDashboard;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShiftCommand extends BaseCommand {

    public ShiftCommand() {
        super("shift");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)){
            MessageUtils.sendMessage(sender, "&4Only players can execute this command!");
            return true;
        }

        if(args.length == 0) {
            handleBase(player);
            return true;
        }

        switch(args[0]) {
            case "addnote":
                break;
            case "end":
                break;
            case "start":
                break;
        }

        return false;
    }

    void handleBase(Player player) {
        StaffDashboard staffDashboard = new StaffDashboard(player);
        staffDashboard.open(player);
    }
}
