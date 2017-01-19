package com.wasteofplastic.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerTeleport(final PlayerTeleportEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        if (e.getPlayer().hasPermission("skycool.bypass")) {
            return;
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
            // Check settings
            boolean delayTeleport = false;
            boolean fromASkyBlock = false;
            boolean toASkyBlock = false;
            if (e.getFrom().getWorld() == api.getIslandWorld() || e.getFrom().getWorld() == api.getNetherWorld()) {
                fromASkyBlock = true;
            }
            if (e.getTo().getWorld() == api.getIslandWorld() || e.getTo().getWorld() == api.getNetherWorld()) {
                toASkyBlock = true;
            }
            if (plugin.getConfig().getBoolean("teleportin", false) && !fromASkyBlock && toASkyBlock) {     
                delayTeleport = true;
            }
            if (plugin.getConfig().getBoolean("teleportout", false) && fromASkyBlock && !toASkyBlock) {     
                delayTeleport = true;
            }
            if (plugin.getConfig().getBoolean("teleportinternal", false) && fromASkyBlock && toASkyBlock) {     
                delayTeleport = true;
            }
            if (delayTeleport) {
                e.setCancelled(true);
                delayedPlayers.add(e.getPlayer().getUniqueId());
                String message = plugin.getConfig().getString("message","Teleporting in [seconds] seconds. Do not move!");
                player.sendMessage(ChatColor.GOLD + message.replace("[seconds]", String.valueOf(delayDuration)));
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                    public void run() {
                        if (delayedPlayers.contains(player.getUniqueId())) {
                            if (player.getLocation().toVector().equals(e.getFrom().toVector())) {
                                player.teleport(e.getTo());
                            } else {
                                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("error","You moved, teleport cancelled!"));
                            }
                            removePlayer(player.getUniqueId());
                        }
                    }}, delayDuration * 20L);
            }
        }
    }

    public void removePlayer(UUID player) {
        delayedPlayers.remove(player);
    }
}
