package com.github.lazygon.lunachatbridge.bukkit.lc;

import com.github.ucchyocean.lc.channel.ChannelPlayerName;

public class ChannelPlayerExtended extends ChannelPlayerName {

    private final String prefix;
    private final String suffix;
    private final String worldName;
    private final String displayName;

    public ChannelPlayerExtended(String name, String prefix, String suffix, String worldName, String displayName) {
        super(name);
        this.prefix = prefix;
        this.suffix = suffix;
        this.worldName = worldName;
        this.displayName = displayName;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getDisplayName() {
        if (isOnline()) {
            return getPlayer().getDisplayName();
        } else {
            return displayName;
        }
    }

    @Override
    public String getWorldName() {
        if (isOnline()) {
            return super.getWorldName();
        } else {
            return worldName;
        }
    }
}