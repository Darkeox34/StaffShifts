package it.ethereallabs.staffshifts.commands.subcommands;

import it.ethereallabs.staffshifts.StaffShifts;
import it.ethereallabs.staffshifts.commands.abs.BaseCommand;
import it.ethereallabs.staffshifts.database.DatabaseManager;
import it.ethereallabs.staffshifts.models.Shift;
import it.ethereallabs.staffshifts.models.Staffer;
import it.ethereallabs.staffshifts.utils.MessageUtils;
import it.ethereallabs.staffshifts.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataCommand extends BaseCommand {

    public TestDataCommand() {
        super("testdata", "<n staffer> <minShift> <maxShift> | clear");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.MANAGEMENT)) {
            MessageUtils.sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            handleClear(sender);
            return true;
        }

        if (args.length < 3) {
            sendUsage(sender);
            return true;
        }

        try {
            int nStaffer = Integer.parseInt(args[0]);
            int minShift = Integer.parseInt(args[1]);
            int maxShift = Integer.parseInt(args[2]);
            handleGenerate(sender, nStaffer, minShift, maxShift);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInsert correct number!");
        }

        return true;
    }

    private void handleGenerate(CommandSender sender, int nStaffer, int minShift, int maxShift) {
        sender.sendMessage("§eGenerating data...");

        DatabaseManager db = StaffShifts.getDatabaseManager();

        Bukkit.getScheduler().runTaskAsynchronously(StaffShifts.getInstance(), () -> {
            long startGen = System.currentTimeMillis();
            int totalShifts = 0;

            for (int i = 0; i < nStaffer; i++) {
                UUID staffUuid = UUID.randomUUID();
                String name = "Staff_" + i;

                Staffer staffer = new Staffer(staffUuid, name);

                int shiftsToCreate = ThreadLocalRandom.current().nextInt(minShift, maxShift + 1);

                for (int j = 0; j < shiftsToCreate; j++) {

                    long offset = ThreadLocalRandom.current().nextLong(0, 7L * 24 * 60 * 60 * 1000);
                    long startTime = System.currentTimeMillis() - offset;

                    long active = ThreadLocalRandom.current().nextLong(5 * 60 * 1000, 3 * 60 * 60 * 1000);
                    long idle = ThreadLocalRandom.current().nextLong(5 * 60 * 1000, 3 * 60 * 60 * 1000);
                    long endTime = startTime + active + idle;

                    Shift shift = new Shift(UUID.randomUUID(), staffUuid, startTime, endTime, active, idle, null);

                    db.saveShift(shift, false);
                    staffer.addStats(active, idle);
                    totalShifts++;
                }

                db.updateStafferData(staffer);
            }

            long endGen = System.currentTimeMillis();
            sender.sendMessage(String.format("§aCompleted! Created %d staffer with %d shifts in %dms.",
                    nStaffer, totalShifts, (endGen - startGen)));
        });
    }

    private void handleClear(CommandSender sender) {
        sender.sendMessage("§cDatabase cleanup...");
        Bukkit.getScheduler().runTaskAsynchronously(StaffShifts.getInstance(), () -> {

            StaffShifts.getDatabaseManager().clearAllData();
            sender.sendMessage("§aDatabase cleanup complete!");
        });
    }
}