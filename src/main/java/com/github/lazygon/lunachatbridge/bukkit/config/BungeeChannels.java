package com.github.lazygon.lunachatbridge.bukkit.config;

import java.util.List;

public class BungeeChannels extends CustomConfig {

    private static final BungeeChannels BUNGEE_CHANNELS = new BungeeChannels("bungee-channels.yml");

    private BungeeChannels(String name) {
        super(name);
    }

    /**
     * @return the bungeeChannels config
     */
    public static BungeeChannels getInstance() {
        return BUNGEE_CHANNELS;
    }

    public List<String> getBungeeChannels() {
        return get().getStringList("bungee-channels");
    }

    public boolean addBungeeChannel(String channel) {
        List<String> channels = getBungeeChannels();
        if (channels.contains(channel) || !channels.add(channel)) {
            return false;
        }
        setBungeeChannels(channels);
        return true;
    }

    public boolean removeBungeeChannel(String channel) {
        List<String> channels = getBungeeChannels();
        if (!channels.remove(channel)) {
            return false;
        }
        setBungeeChannels(channels);
        return true;
    }

    public boolean isBungeeChannel(String channel) {
        return getBungeeChannels().contains(channel);
    }

    private void setBungeeChannels(List<String> channels) {
        get().set("bungee-channels", channels);
        save();
    }
}