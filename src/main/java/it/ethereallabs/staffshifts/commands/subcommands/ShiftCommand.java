package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.gui.StaffDashboard;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShiftCommand extends BaseCommand {

    public ShiftCommand() {
        super("shift");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)){
            MessageUtils.sendMessage(sender, "only-players");
            return true;
        }

        if(args.length == 0) {
            handleBase(player);
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "addnote":
                if (args.length < 2) {
                    return true;
                }

                String note = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                handleAddNote(player, note);
                break;
            case "end":
                handleEnd(player);
                break;
            case "start":
                handleStart(player);
                break;
        }

        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1){
            List<String> commands = List.of("addnote", "end", "start");
            return commands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        else if(args.length == 2){
            if(args[0].equals("addnote")){
                return List.of("<note>");
            }
        }
        return List.of();
    }

    void handleBase(Player player) {
        StaffDashboard staffDashboard = new StaffDashboard(player);
        staffDashboard.open(player);
    }

    void handleStart(Player player) {
        StaffShifts.getShiftsManager().addShift(player.getUniqueId());
    }

    void handleEnd(Player player) {
        StaffShifts.getShiftsManager().endShift(player.getUniqueId());
    }

    void handleAddNote(Player player, String note) {
        Shift shift = StaffShifts.getShiftsManager().getShift(player.getUniqueId());

        if (shift == null) {
            MessageUtils.sendMessage(player, "no-active-shift");
            return;
        }

        shift.addNote(note);
        MessageUtils.sendMessage(player, "note-added");
    }
}
