package com.github.lazygon.lunachatbridge.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.github.lazygon.lunachatbridge.bungee.command.LunaChatBridgeCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

    private static BungeeMain instance;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().getScheduler().schedule(instance, this::updatePlayers, 5, 5, TimeUnit.SECONDS);
        getProxy().registerChannel("lc:tobukkit");
        getProxy().registerChannel("lc:tobungee");
        PluginMessageListener.start();
        PlayerListener.start();
        LunaChatBridgeCommand.register();
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(instance);
        getProxy().unregisterChannel("lc:tobukkit");
        getProxy().unregisterChannel("lc:tobungee");
        PluginMessageListener.stop();
        PlayerListener.stop();
    }

    public static BungeeMain getInstance() {
        if (instance == null) {
            instance = (BungeeMain) ProxyServer.getInstance().getPluginManager().getPlugin("Channels");
            if (instance == null) {
                throw new ExceptionInInitializerError("Cannot initialize LunaChatBridge plugin.");
            }
        }
    
        return instance;
    }

    private void updatePlayers() {
        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOutStream);

            out.writeUTF("updateplayers");
            out.writeUTF(ProxyServer.getInstance().getPlayers().stream()
                    .map(ProxiedPlayer::getName).reduce("", (p1, p2) -> p1 + "," + p2));

            byte[] data = byteOutStream.toByteArray();
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("lc:tobukkit", data);
            }

            out.close();
            byteOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}