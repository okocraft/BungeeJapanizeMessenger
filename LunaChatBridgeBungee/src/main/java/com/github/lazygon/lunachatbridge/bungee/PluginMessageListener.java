package com.github.lazygon.lunachatbridge.bungee;

import com.github.lazygon.lunachatbridge.bungee.config.Config;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PluginMessageListener implements Listener {

    private static final BungeeMain PLUGIN = BungeeMain.getInstance();
    private static final PluginMessageListener INSTANCE = new PluginMessageListener();

    static void start() {
        ProxyServer.getInstance().getPluginManager().registerListener(PLUGIN, INSTANCE);
    }

    static void stop() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(INSTANCE);
    }

    private PluginMessageListener() {
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equals("lc:tobungee")) {
            return;
        }

        try {
            ByteArrayInputStream byteInStream = new ByteArrayInputStream(event.getData());
            DataInputStream in = new DataInputStream(byteInStream);
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOutStream);

            // 操作
            String operation = in.readUTF();
            out.writeUTF(operation);

            if (operation.equalsIgnoreCase("chat")) {
                // チャンネル名
                String channelName = in.readUTF();
                out.writeUTF(channelName);

                // プレイヤー名
                String playerName = in.readUTF();
                out.writeUTF(playerName);

                // プレイヤー表示名
                String playerDisplayName = in.readUTF();
                out.writeUTF(playerDisplayName);

                // プレイヤープレフィックス
                String playerPrefix = in.readUTF();
                out.writeUTF(playerPrefix);

                // プレイヤーサフィックス
                String playerSuffix = in.readUTF();
                out.writeUTF(playerSuffix);

                // プレイヤーのいるワールド
                String worldName = in.readUTF();
                out.writeUTF(worldName);

                // チャットメッセージ
                String chatMessage = in.readUTF();
                out.writeUTF(chatMessage);

                boolean japanize = in.readBoolean();
                out.writeBoolean(japanize);

                boolean canUseColorCode = in.readBoolean();
                out.writeBoolean(canUseColorCode);

                in.close();
                byteInStream.close();
                out.close();
                byteOutStream.close();

                // 発言者のみが発言のプラグインメッセージを送信する。
                if (!((ProxiedPlayer) event.getReceiver()).getName().equals(playerName)) {
                    return;
                }

                byte[] data = byteOutStream.toByteArray();
                String serverFrom = ProxyServer.getInstance().getPlayer(playerName).getServer().getInfo().getName();

                for (ServerInfo server : getDestServers(serverFrom, channelName)) {
                    server.sendData("lc:tobukkit", data, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets servers that must listen chat message from this server and LunaChat channel.
     *
     * @param server The server speaker currently on.
     * @param lunaChatChannel The lunachat channel which the speaker is in.
     *
     * @return destination servers.
     */
    private Set<ServerInfo> getDestServers(String server, String lunaChatChannel) {
        Set<ServerInfo> result = new HashSet<>();

        if (!Config.getInstance().getOpenedLunaChatChannels().contains(lunaChatChannel)) {
            return result;
        }

        List<String> servers = Config.getInstance().getOpeningServer(lunaChatChannel);
        if (!servers.contains(server)) {
            return result;
        }

        ProxyServer.getInstance().getServers().forEach((name, serverInfo) -> {
            if (servers.contains(name) && !server.equals(name)) {
                result.add(serverInfo);
            }
        });

        return result;
    }
}