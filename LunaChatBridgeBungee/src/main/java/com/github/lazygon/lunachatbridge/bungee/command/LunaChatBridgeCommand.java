package com.github.lazygon.lunachatbridge.bungee.command;

import com.github.lazygon.lunachatbridge.bungee.BungeeMain;
import com.github.lazygon.lunachatbridge.bungee.config.Config;
import com.github.lazygon.lunachatbridge.bungee.config.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class LunaChatBridgeCommand extends Command implements TabExecutor {

    private static final Messages MESSAGES = Messages.getInstance();

    private static LunaChatBridgeCommand instance;

    private LunaChatBridgeCommand() {
        super("lunachatbridge", "lunachatbridge.admin", "lcbridge", "lcb");
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeMain.getInstance(), this);
    }

    public static void register() {
        if (instance != null) {
            throw new IllegalStateException("This command is already registered.");
        }

        instance = new LunaChatBridgeCommand();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // /lcb reload

        if (args.length == 0) {
            MESSAGES.sendNotEnoughArgument(sender);
            return;
        }

        Config.getInstance().reload();
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return copyPartialMatches(args[0], List.of("reload"), new ArrayList<>());
        }

        return List.of();
    }

    private static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Objects.requireNonNull(token, "Search token cannot be null");
        Objects.requireNonNull(collection, "Collection cannot be null");
        Objects.requireNonNull(originals, "Originals cannot be null");

        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    private static boolean startsWithIgnoreCase(String string, String prefix) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(string, "Cannot check a null string for a match");
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
