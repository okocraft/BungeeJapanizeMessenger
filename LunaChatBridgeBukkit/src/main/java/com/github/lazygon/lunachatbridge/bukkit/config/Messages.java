package com.github.lazygon.lunachatbridge.bukkit.config;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Messages extends CustomConfig {
    
    private static final Messages INSTANCE = new Messages("messages.yml");
    
    Messages(String name) {
        super(name);
    }

    public static Messages getInstance() {
        return INSTANCE;
    }

    /**
     * Send message to player.
     * 
     * @param sender
     * @param addPrefix
     * @param path
     * @param placeholders
     */
    public void sendMessage(CommandSender sender, boolean addPrefix, String path, Map<String, Object> placeholders) {
        String prefix = addPrefix ? get().getString("command.general.info.plugin-prefix", "&8[&6LunaChatBridge&8]&r") + " " : "";
        String message = prefix + getMessage(path);
        for (Map.Entry<String, Object> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue().toString());
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return;
    }

    /**
     * Send message to player.
     * 
     * @param sender
     * @param path
     * @param placeholders
     */
    public void sendMessage(CommandSender sender, String path, Map<String, Object> placeholders) {
        sendMessage(sender, true, path, placeholders);
    }

    /**
     * Send message to player.
     * 
     * @param sender
     * @param path
     */
    public void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, Map.of());
    }

    /**
     * Send message to player.
     * 
     * @param sender
     * @param addPrefix
     * @param path
     */
    public void sendMessage(CommandSender sender, boolean addPrefix, String path) {
        sendMessage(sender, addPrefix, path, Map.of());
    }

    /**
     * Gets message from key. Returned messages will not translated its color code.
     * 
     * @param path
     * @return
     */
    public String getMessage(String path) {
        return get().getString(path, path);
    }

    public void sendNotEnoughArgument(CommandSender sender) {
        sendMessage(sender, "command.general.error.not-enough-argument");
    }

    public void sendInvalidArgument(CommandSender sender, String inputArgument) {
        sendMessage(sender, "command.general.error.invalid-argument", Map.of("%argument%", inputArgument));
    }

    public void sendNoChannelFound(CommandSender sender, String inputChannel) {
        sendMessage(sender, "command.general.error.invalid-argument", Map.of("%argument%", inputChannel));
    }

    public void sendNoPermission(CommandSender sender, String permission) {
        sendMessage(sender, "command.general.error.no-permission", Map.of("%permission%", permission));
    }
}