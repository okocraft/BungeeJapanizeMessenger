package com.github.lazygon.lunachatbridge.bukkit;

import com.github.lazygon.lunachatbridge.bukkit.listener.LunaChatListener;
import com.github.lazygon.lunachatbridge.bukkit.listener.PluginMessages;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {

    private static BukkitMain instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "lc:tobukkit", PluginMessages.getInstance());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "lc:tobungee");
        LunaChatListener.start();
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "lc:tobukkit", PluginMessages.getInstance());
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, "lc:tobungee");
        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static BukkitMain getInstance() {
        if (instance == null) {
            instance = (BukkitMain) Bukkit.getPluginManager().getPlugin("LunaChatBridge");
            if (instance == null) {
                throw new ExceptionInInitializerError("Cannot initialize LunaChatBukkit plugin.");
            }
        }

        return instance;
    }

}
