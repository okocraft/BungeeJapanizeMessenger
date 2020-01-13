package com.github.lazygon.lunachatbridge.bungee.config;

import java.util.HashMap;
import java.util.Map;

import com.github.lazygon.lunachatbridge.bungee.discord.DiscordUtils;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.md_5.bungee.config.Configuration;

public class Config extends CustomConfig {

    private static Config instance;

    private Config() {
        super("config.yml");
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
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

    public Map<String, String> getChannelMap() {
        Map<String, String> channelMap = new HashMap<>();
        Configuration channelMapSection = get().getSection("discord.channel-map");
        if (channelMapSection == null) {
            return channelMap;
        }
        channelMapSection.getKeys().forEach(key -> {
            String discordChannel = channelMapSection.getString(key);
            if (discordChannel != null && discordChannel.matches("\\d{18}")) {
                channelMap.put(key, discordChannel);
            }
        });

        return channelMap;
    }

}