package it.ethereallabs.staffshifts.gui.abs;

import it.ethereallabs.staffshifts.StaffShifts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;


public class ChatPrompts implements Listener {

    private static final ChatPrompts instance = new ChatPrompts();

    private record Flow(BiConsumer<Player, String> step) {
        public void execute(Player player, String message) {
            if (message != null) {
                step.accept(player, message);
            }
        }
    }

    private final Map<UUID, Flow> pending = new ConcurrentHashMap<>();

    public static ChatPrompts getInstance() {
        return instance;
    }

    public void ask(Player player, String question, BiConsumer<Player, String> onAnswer) {
        player.closeInventory();
        player.sendMessage("§b» " + question + " §7(cancel: §c!cancel§7)");
        pending.put(player.getUniqueId(), new Flow(onAnswer));
    }

    public void cancel(Player player) {
        pending.remove(player.getUniqueId());
        player.sendMessage("§7Operation cancelled.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Flow flow = pending.get(player.getUniqueId());
        if (flow == null) return;

        event.setCancelled(true);

        String msg = event.getMessage();

        if (msg.equalsIgnoreCase("!cancel")) {
            cancel(player);
            return;
        }

        pending.remove(player.getUniqueId());
        Bukkit.getScheduler().runTask(StaffShifts.getInstance(), () -> flow.execute(player, msg));
    }
}
