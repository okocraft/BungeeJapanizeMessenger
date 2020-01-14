package com.github.lazygon.lunachatbridge.bungee.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.lazygon.lunachatbridge.bungee.discord.DiscordUtils;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.md_5.bungee.config.Configuration;

public class Config extends CustomConfig {

    private static Config instance = new Config();

    private Config() {
        super("config.yml");
    }

    public static Config getInstance() {
        return instance;
    }

    public Set<String> getOpenedLunaChatChannels() {
        return getKeys("opened-channels");
    }

    public List<String> getOpeningServer(String lunaChatChannel) {
        return get().getStringList("opened-channels." + lunaChatChannel);
    }

    public String getBotToken() {
        return get().getString("");
    }

    public OnlineStatus getBotStatus() {
        return DiscordUtils.getOnlineStatus(get().getString("discord.bot.status", "DO_NOT_DISTURB"));
    }

    public ActivityType getBotActivity() {
        return DiscordUtils.getActivityType(get().getString("discord.bot.activity", "DEFAULT"));
    }

    public String getPlayingGame() {
        return get().getString("discord.bot.game", "");
    }

    public String getUrl() {
        return get().getString("discord.bot.url");
    }

    public Set<String> getMappedDiscordChannel() {
        return getKeys("discord.channel-map");
    }

    public void removeMappedDiscordChannel(String removalDiscordChannel) {
        get().set("discord.channel-map." + removalDiscordChannel, null);
        save();
    }

    public Set<String> getMappedLunaChatChannel(String discordChannelId) {
        return getKeys("discord.channel-map." + discordChannelId);
    }

    public void removeMappedLunaChatChannel(String discordChannelId, String removalLunaChatChannel) {
        get().set("discord.channel-map." + discordChannelId + "." + removalLunaChatChannel, null);
        save();
    }

    public List<String> getMappedLunaChatChannelServers(String discordChannelId, String lunaChatChannel) {
        return get().getStringList("discord.channel-map." + discordChannelId + "." + lunaChatChannel);
    }

    public void removeMappedLunaChatChannelServer(String discordChannelId, String lunaChatChannel, String removalServer) {
        List<String> currentServers = getMappedLunaChatChannelServers(discordChannelId, lunaChatChannel);
        currentServers.remove(removalServer);
        get().set("discord.channel-map." + discordChannelId + "." + lunaChatChannel, currentServers);
        save();
    }

    private Set<String> getKeys(String key) {
        Configuration section = get().getSection(key);
        if (section == null) {
            return Set.of();
        }

        return (LinkedHashSet<String>) section.getKeys();
    }

    public void reloadAllConfig() {
        reload();
        Messages.getInstance().reload();
    }

}