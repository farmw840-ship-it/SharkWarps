package io.github.farmw840.sharkwarps;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager implements Listener {

    private final SharkWarps plugin;
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public TeleportManager(SharkWarps plugin) {
        this.plugin = plugin;

        plugin.getServer()
                .getPluginManager()
                .registerEvents(this, plugin);
    }

    public void teleport(Player player, Location destination, String warpName) {

        UUID uuid = player.getUniqueId();

        cancelTeleport(player, false);

        Location target = destination.clone();

        player.sendMessage("§eTeleportando em 3 segundos. Não se mova!");

        BukkitTask task = new BukkitRunnable() {

            int seconds = 3;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    tasks.remove(uuid);
                    cancel();
                    return;
                }

                if (seconds <= 0) {

                    boolean success = player.teleport(target);

                    if (success) {
                        plugin.getWarpManager().incrementVisits(warpName);
                        plugin.getWarpManager().addVisited(uuid, warpName);
                        player.sendMessage("§aTeleporte concluído!");
                    } else {
                        player.sendMessage("§cNão foi possível teleportar.");
                    }

                    tasks.remove(uuid);
                    cancel();
                    return;
                }

                player.sendActionBar(
                        Component.text("Teleportando em "
                                + seconds + " segundos...")
                );

                seconds--;
            }

        }.runTaskTimer(plugin, 0L, 20L);

        tasks.put(uuid, task);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!tasks.containsKey(player.getUniqueId())
                || event.getTo() == null) {
            return;
        }

        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

            cancelTeleport(player, true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (tasks.containsKey(player.getUniqueId())) {
            cancelTeleport(player, true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancelTeleport(event.getPlayer(), false);
    }

    public void cancelTeleport(Player player, boolean sendMessage) {

        UUID uuid = player.getUniqueId();
        BukkitTask task = tasks.remove(uuid);

        if (task != null) {
            task.cancel();

            if (sendMessage && player.isOnline()) {
                player.sendMessage("§cTeleporte cancelado!");
            }
        }
    }
}

