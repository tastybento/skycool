package com.wasteofplastic.skycool;


import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wasteofplastic.teleport.DelayTeleport;


public class SkyCool extends JavaPlugin {

    @Override
    public void onEnable() {
        // Enable the plugin
        PluginManager manager = getServer().getPluginManager();
        // Check for ASkyBlock
        Plugin asb = manager.getPlugin("ASkyBlock");

        if (asb == null) {
            getLogger().severe("ASkyBlock not loaded. Disabling plugin");

        } else {
            getLogger().info(asb.getDescription().getVersion());
            // Load config
            saveDefaultConfig();
            int delay = getConfig().getInt("delay",5);
            getServer().getPluginManager().registerEvents(new DelayTeleport(this,delay), this);
        }

    }

    @Override
    public void onDisable() {
    }
}
