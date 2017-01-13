package com.wasteofplastic.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wasteofplastic.skycool.SkyCool;

public class DelayedPlayer {
    /**
     * @param player
     * @param from
     * @param to
     * @param timeOut
     */
    public DelayedPlayer(SkyCool plugin, final Player player, final Location from, final Location to, final Long timeOut) {
	// Delay the player
	player.sendMessage(ChatColor.GOLD + "Teleporting in " + timeOut + " seconds. Do not move!");
	plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

	    public void run() {
		if (player.getLocation().toVector().equals(from.toVector())) {
		    player.teleport(to);
		    
		} else {
		    player.sendMessage(ChatColor.RED + "You moved, teleport cancelled!");
		}
		
	    }}, timeOut);
    }
    
}
