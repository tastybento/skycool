package com.wasteofplastic.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.skycool.SkyCool;

public class DelayTeleport implements Listener {
    private static final boolean DEBUG = false;
    private SkyCool plugin;
    private long delayDuration;
    private List<UUID> delayedPlayers;
    private ASkyBlockAPI api = ASkyBlockAPI.getInstance();

    /**
     * @param plugin
     */
    public DelayTeleport(SkyCool plugin, long delayDuration) {
        this.plugin = plugin;
        this.delayDuration = delayDuration; // Seconds
        delayedPlayers = new ArrayList<UUID>();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerTeleport(final PlayerTeleportEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        if (delayedPlayers.contains(e.getPlayer().getUniqueId())) {
            return;
        }
        // Check reason
        //plugin.getLogger().info("DEBUG: reason = " + e.getCause());
        switch (e.getCause()) {
        case ENDER_PEARL:
        case END_PORTAL:
        case NETHER_PORTAL:
        case SPECTATE:
        case UNKNOWN:
            // Don't block
            return;
        default:
            break;
        }
        final Player player = e.getPlayer();
        if (e.getTo() != null && e.getFrom() != null && !e.getTo().equals(e.getFrom())) {
            if (e.getFrom().getWorld().equals(api.getIslandWorld()) || e.getFrom().getWorld().equals(api.getNetherWorld())) {
                e.setCancelled(true);
                delayedPlayers.add(e.getPlayer().getUniqueId());
                player.sendMessage(ChatColor.GOLD + "Teleporting in " + delayDuration + " seconds. Do not move!");
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                    public void run() {
                        if (delayedPlayers.contains(player.getUniqueId())) {
                            if (player.getLocation().toVector().equals(e.getFrom().toVector())) {
                                player.teleport(e.getTo());
                            } else {
                                player.sendMessage(ChatColor.RED + "You moved, teleport cancelled!");
                            }
                            removePlayer(player.getUniqueId());
                        }
                    }}, delayDuration * 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (delayedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(ChatColor.RED + "You moved, teleport cancelled!");
            removePlayer(e.getPlayer().getUniqueId());
            return;
        }
    }

    public void removePlayer(UUID player) {
        delayedPlayers.remove(player);
    }
}
