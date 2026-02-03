package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.Permissions;
import it.ethereallabs.staffshifts.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AdminCommand extends BaseCommand {

    private enum Action { ADD, REMOVE, SET }

    public AdminCommand() {
        super("admin", "<addtime|removetime|settime> <player> <active|idle> <time>");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.MANAGEMENT)) {
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "removetime":
                handleModification(sender, args, Action.REMOVE);
                break;
            case "addtime":
                handleModification(sender, args, Action.ADD);
                break;
            case "settime":
                handleModification(sender, args, Action.SET);
                break;
            default:
                sendUsage(sender);
                break;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> commands = List.of("removetime", "addtime", "settime");

        if (args.length == 1) {
            return commands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        else if (args.length == 2 && commands.contains(args[0].toLowerCase())) {
            return List.of("<player>");
        }
        else if (args.length == 3 && commands.contains(args[0].toLowerCase())) {
            return List.of("idle", "active");
        }
        else if (args.length == 4 && commands.contains(args[0].toLowerCase())) {
            return List.of("<time>");
        }

        return List.of();
    }

    private void handleModification(CommandSender sender, String[] args, Action action) {
        if(args.length < 4){
            sendUsage(sender);
            return;
        }

        String targetName = args[1];
        String type = args[2].toLowerCase();
        String timeStr = args[3];

        if (!type.equals("active") && !type.equals("idle")) {
            MessageUtils.sendMessage(sender, "&cType must be 'active' or 'idle'.");
            return;
        }

        long time = TimeUtils.parseDuration(timeStr);
        if (time == -1) {
            MessageUtils.sendMessage(sender, "&cInvalid time format. Use 10s, 1m, 1h30m etc.");
            return;
        }

        Player onlineTarget = Bukkit.getPlayer(targetName);
        if (onlineTarget != null) {
            processModification(sender, onlineTarget.getUniqueId(), targetName, action, type, time);
        } else {
            StaffShifts.getDatabaseManager().getStafferByName(targetName, rs -> {
                try {
                    if (rs.next()) {
                        String uuidStr = rs.getString("uuid");
                        UUID uuid = UUID.fromString(uuidStr);
                        Bukkit.getScheduler().runTask(StaffShifts.getInstance(), () ->
                            processModification(sender, uuid, targetName, action, type, time)
                        );
                    } else {
                        MessageUtils.sendMessage(sender, "&cPlayer " + targetName + " not found in database.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void processModification(CommandSender sender, UUID uuid, String targetName, Action action, String type, long time) {
        Shift currentShift = StaffShifts.getShiftsManager().getShift(uuid);

        StaffShifts.getShiftsManager().getStafferProfile(uuid, staffer -> {
            if (currentShift != null) {
                applyChange(currentShift, action, type, time);
                StaffShifts.getDatabaseManager().saveShift(currentShift, true);
                MessageUtils.sendMessage(sender, "&aUpdated current shift for " + targetName + ".");
            } else {
                StaffShifts.getShiftsManager().getRecentShifts(uuid, 1, shifts -> {
                    if (shifts.isEmpty()) {
                        MessageUtils.sendMessage(sender, "&cNo recent shifts found for " + targetName + ".");
                        return;
                    }
                    Shift lastShift = shifts.getFirst();

                    long oldVal = type.equals("active") ? lastShift.getActiveMillis() : lastShift.getIdleMillis();
                    applyChange(lastShift, action, type, time);
                    long newVal = type.equals("active") ? lastShift.getActiveMillis() : lastShift.getIdleMillis();

                    long delta = newVal - oldVal;

                    if (type.equals("active")) {
                        staffer.addStats(delta, 0);
                    } else {
                        staffer.addStats(0, delta);
                    }

                    StaffShifts.getDatabaseManager().saveShift(lastShift, false);
                    StaffShifts.getDatabaseManager().updateStafferData(staffer);

                    MessageUtils.sendMessage(sender, "&aUpdated last shift for " + targetName + ".");
                });
            }
        });
    }

    private void applyChange(Shift shift, Action action, String type, long value) {
        long newVal = type.equals("active") ? shift.getActiveMillis() : shift.getIdleMillis();

        switch (action) {
            case ADD:
                newVal += value;
                break;
            case REMOVE:
                newVal -= value;
                if (newVal < 0) newVal = 0;
                break;
            case SET:
                newVal = value;
                break;
        }

        if (type.equals("active")) {
            shift.setActiveMillis(newVal);

        } else {
            shift.setIdleMillis(newVal);
        }
    }
}
