package com.github.lazygon.lunachatbridge.bungee;

import net.md_5.bungee.api.plugin.Event;

public class LunaChatGlobalChatEvent extends Event {
    private final String channelName, playerName, displayName, prefix, suffix, worldName, serverName, message;

    public LunaChatGlobalChatEvent(String channelName, String playerName, String displayName,
                                   String prefix, String suffix, String worldName, String serverName, String message) {
        this.channelName = channelName;
        this.playerName = playerName;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.worldName = worldName;
        this.serverName = serverName;
        this.message = message;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getMessage() {
        return message;
    }
}
