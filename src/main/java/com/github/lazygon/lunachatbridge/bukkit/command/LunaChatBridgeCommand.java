package com.github.lazygon.lunachatbridge.bukkit.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.lazygon.lunachatbridge.bukkit.BukkitMain;
import com.github.lazygon.lunachatbridge.bukkit.config.BungeeChannels;
import com.github.lazygon.lunachatbridge.bukkit.config.Messages;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class LunaChatBridgeCommand implements CommandExecutor, TabCompleter {

    private static final BukkitMain PLUGIN = BukkitMain.getInstance();
    private static final Messages MESSAGES = Messages.getInstance();
    private static final LunaChatBridgeCommand INSTANCE = new LunaChatBridgeCommand();
    private static boolean registered = false;

    private LunaChatBridgeCommand() {
    }

    public static boolean register() {
        if (registered) {
            return false;
        }
        registered = true;
        PluginCommand command = PLUGIN.getCommand("lunachatbridge");
        if (command != null) {
            command.setExecutor(INSTANCE);
            command.setTabCompleter(INSTANCE);
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /lcb bungeechannel < add <channel> | remove <channel> | list >
        
        if (args.length < 2) {
            MESSAGES.sendNotEnoughArgument(sender);
            return false;
        }

        if (!args[0].equalsIgnoreCase("bungeechannel")) {
            MESSAGES.sendInvalidArgument(sender, args[0]);
            return false;
        }

        if (args[1].equalsIgnoreCase("list")) {

            if (!sender.hasPermission("lunachatbridge.bungeechannel.list")) {
                MESSAGES.sendNoPermission(sender, "lunachatbridge.bungeechannel.list");
                return false;
            }

            MESSAGES.sendMessage(sender, "command.lunachatbridge.bungeechannel.list-header");
            BungeeChannels.getInstance().getBungeeChannels().forEach(channel -> {
                MESSAGES.sendMessage(sender, false, "command.lunachatbridge.bungeechannel.list-format", Map.of("%channel%", channel));                
            });
            return true;
        }

        if (args.length == 2) {
            MESSAGES.sendNotEnoughArgument(sender);
            return false;
        }


        if (args[1].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("lunachatbridge.bungeechannel.add")) {
                if (!sender.hasPermission("lunachatbridge.bungeechannel.add.moderator")) {
                    MESSAGES.sendNoPermission(sender, "lunachatbridge.bungeechannel.add.moderator");
                    return false;                
                }
                
                Channel channel = LunaChat.getInstance().getLunaChatAPI().getChannel(args[2]);
                if (channel == null
                        || channel.getModerator().stream().map(ChannelPlayer::getName).noneMatch(sender.getName()::equals)) {
                    MESSAGES.sendMessage(sender, "command.general.error.not-channel-moderator");
                    return false;     
                }
            }

            if (BungeeChannels.getInstance().addBungeeChannel(args[2])) {
                MESSAGES.sendMessage(sender, "command.lunachatbridge.bungeechannel.add-success", Map.of("%channel%", args[2]));
                return true;
            }
            
            MESSAGES.sendMessage(sender, "command.lunachatbridge.bungeechannel.add-failure", Map.of("%channel%", args[2]));
            return false;
        }
        
        if (args[1].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("lunachatbridge.bungeechannel.remove")) {
                if (!sender.hasPermission("lunachatbridge.bungeechannel.remove.moderator")) {
                    MESSAGES.sendNoPermission(sender, "lunachatbridge.bungeechannel.remove.moderator");
                    return false;                
                }
                
                Channel channel = LunaChat.getInstance().getLunaChatAPI().getChannel(args[2]);
                if (channel == null
                        || channel.getModerator().stream().map(ChannelPlayer::getName).noneMatch(sender.getName()::equals)) {
                    MESSAGES.sendMessage(sender, "command.general.error.not-channel-moderator");
                    return false;     
                }
            }

            if (BungeeChannels.getInstance().removeBungeeChannel(args[2])) {
                MESSAGES.sendMessage(sender, "command.lunachatbridge.bungeechannel.remove-success", Map.of("%channel%", args[2]));
                return true;
            }
            
            MESSAGES.sendMessage(sender, "command.lunachatbridge.bungeechannel.remove-failure", Map.of("%channel%", args[2]));
            return false;
        }

        MESSAGES.sendInvalidArgument(sender, args[1]);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("bungeechannel"), result);
        }
        
        if (!args[0].equalsIgnoreCase("bungeechannel")) {
            return result;
        }

        List<String> subCommands = List.of("add", "remove", "list");
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], subCommands, result);
        }

        if (args[1].equalsIgnoreCase("add") && args.length == 3) {
            if (!sender.hasPermission("lunachatbridge.bungeechannel.add.moderator")) {
                return result;
            }
            boolean hasPermissionAdd = sender.hasPermission("lunachatbridge.bungeechannel.add");
            List<String> channels = LunaChat.getInstance().getLunaChatAPI().getChannels().stream()
                    .filter(channel -> hasPermissionAdd || channel.getModerator().contains(ChannelPlayer.getChannelPlayer(sender)))
                    .map(Channel::getName).collect(Collectors.toList()); 
            return StringUtil.copyPartialMatches(args[2], channels, result);
        }

        if (args[1].equalsIgnoreCase("remove") && args.length == 3) {
            if (!sender.hasPermission("lunachatbridge.bungeechannel.remove.moderator")) {
                return result;
            }
            List<String> channels = BungeeChannels.getInstance().getBungeeChannels();
            if (!sender.hasPermission("lunachatbridge.bungeechannel.remove")) {
                channels.removeIf(channelName -> !LunaChat.getInstance().getLunaChatAPI().getChannel(channelName).getModerator()
                        .contains(ChannelPlayer.getChannelPlayer(sender)));
            }
            return StringUtil.copyPartialMatches(args[2], channels, result);
        }

        return result;
    }
}