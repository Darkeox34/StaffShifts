package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.gui.StaffDashboard;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StafferCommand extends BaseCommand {

    public StafferCommand() {
        super("shift", "addnote <note> | removenote <index> | start | end");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.STAFFER) || !sender.hasPermission(Permissions.MANAGEMENT)) {
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }

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
            case "removenote":
                if (args.length < 2) {
                    sendUsage(sender);
                    return true;
                }
                try {
                    int index = Integer.parseInt(args[1]);
                    handleRemoveNote(player, index);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cPlease insert a valid number.");
                }
                break;
            case "listnotes":
                handleListNotes(player);
                break;
            case "end":
                handleEnd(player);
                break;
            case "start":
                handleStart(player);
                break;
        }

        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1){
            List<String> commands = List.of("addnote", "removenote", "listnotes", "end", "start");
            return commands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        else if(args.length == 2){
            if(args[0].equalsIgnoreCase("removenote")){
                return List.of("<index>");
            }
            if(args[0].equalsIgnoreCase("addnote")){
                return List.of("<note>");
            }
        }
        return List.of();
    }

    void handleListNotes(Player p) {
        Shift shift = StaffShifts.getShiftsManager().getShift(p.getUniqueId());

        if (shift == null) {
            MessageUtils.sendMessage(p, "no-active-shift");
            return;
        }

        p.sendMessage("§7Current Shift Notes:");

        List<String> notes = shift.getNotes();
        for (int i = 0; i < notes.size(); i++) {
            p.sendMessage(" §e" + (i + 1) + ") §7" + notes.get(i));
        }
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
        StaffShifts.getDatabaseManager().saveShift(shift, true);
        MessageUtils.sendMessage(player, "note-added");
    }

    void handleRemoveNote(Player p, int indexInput) {
        Shift shift = StaffShifts.getShiftsManager().getShift(p.getUniqueId());

        if (shift == null) {
            MessageUtils.sendMessage(p, "no-active-shift");
            return;
        }

        int internalIndex = indexInput - 1;

        String removedNote = shift.removeNote(internalIndex);

        if (removedNote == null) {
            p.sendMessage("§cNot valid index. Use a number between 1 and " + shift.getNotes().size());
            return;
        }

        StaffShifts.getDatabaseManager().saveShift(shift, true);
        p.sendMessage("§aNote successfully deleted: §e" + removedNote);
    }
}
