package com.github.lazygon.lunachatbridge.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private static final BungeeMain PLUGIN = BungeeMain.getInstance();
    private static final PlayerListener INSTANCE = new PlayerListener();

    private PlayerListener() {
    }

    static void start() {
        ProxyServer.getInstance().getPluginManager().registerListener(PLUGIN, INSTANCE);
    }

    static void stop() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(INSTANCE);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        String joinedPlayer = event.getPlayer().getName();
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOut);
            out.writeUTF("joinplayer");
            out.writeUTF(joinedPlayer);
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("lc:tobukkit", byteArrayOut.toByteArray());
            }
            out.close();
            byteArrayOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        String leftPlayer = event.getPlayer().getName();
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOut);
            out.writeUTF("disconnectplayer");
            out.writeUTF(leftPlayer);
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("lc:tobukkit", byteArrayOut.toByteArray());
            }
            out.close();
            byteArrayOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}