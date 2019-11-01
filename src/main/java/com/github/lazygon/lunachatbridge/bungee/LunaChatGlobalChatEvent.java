package com.github.lazygon.lunachatbridge.bungee;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class LunaChatGlobalChatEvent extends Event implements Cancellable {
    private final String channelName, playerName, displayName, prefix, suffix, worldName, serverName, message;
    private boolean cancelled;

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
        cancelled = false;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
