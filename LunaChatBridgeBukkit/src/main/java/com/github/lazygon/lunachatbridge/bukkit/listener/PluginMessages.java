package com.github.lazygon.lunachatbridge.bukkit.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.lazygon.lunachatbridge.bukkit.config.BungeeChannels;
import com.github.lazygon.lunachatbridge.bukkit.lc.ChannelPlayerExtended;
import com.github.lazygon.lunachatbridge.bukkit.lc.DataMapsExtended;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessages implements PluginMessageListener {

    private static final PluginMessages INSTANCE = new PluginMessages();
    private List<String> players = new ArrayList<>();

    private PluginMessages() {
    }

    public static PluginMessages getInstance() {
        return INSTANCE;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("lc:tobukkit")) {
            return;
        }

        try {
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(message);
            DataInputStream in = new DataInputStream(byteArrayIn);

            String operation = in.readUTF();
            if (operation.equalsIgnoreCase("chat")) {
                String channelName = in.readUTF();
                String playerName = in.readUTF();
                String playerDisplayName = in.readUTF();
                String playerPrefix = in.readUTF();
                String playerSuffix = in.readUTF();
                String worldName = in.readUTF();
                String chatMessage = in.readUTF();
                boolean japanize = in.readBoolean();
                boolean canUseColorCode = in.readBoolean();

                in.close();
                byteArrayIn.close();

                boolean defaultJapanize = LunaChat.getInstance().getLunaChatAPI().isPlayerJapanize(playerName);
                if (japanize != defaultJapanize) {
                    LunaChat.getInstance().getLunaChatAPI().setPlayersJapanize(playerName, japanize);
                }

                ChannelPlayer channelPlayer = new ChannelPlayerExtended(playerName, playerPrefix, playerSuffix,
                        worldName, playerDisplayName, canUseColorCode);

                // プライベートメッセージ
                if (channelName.contains(">")) {
                    if (Bukkit.getPlayer(playerName) != null) {
                        return;
                    }
                    String invited = channelName.substring(channelName.indexOf(">") + 1);

                    if (Bukkit.getPlayer(invited) == null) {
                        return;
                    }

                    sendTellMessage(channelPlayer, invited, chatMessage);
                    return;
                }

                Channel lcChannel = LunaChat.getInstance().getLunaChatAPI().getChannel(channelName);
                if (lcChannel != null) {
                    lcChannel.chat(channelPlayer, chatMessage);
                }

                if (japanize != defaultJapanize) {
                    LunaChat.getInstance().getLunaChatAPI().setPlayersJapanize(playerName, defaultJapanize);
                }

            } else if (operation.equalsIgnoreCase("updateplayers")) {
                players = new ArrayList<>(Arrays.asList(in.readUTF().split(",", -1)));
            } else if (operation.equalsIgnoreCase("joinplayer")) {
                String playerName = in.readUTF();
                if (!players.contains(playerName)) {
                    players.add(playerName);
                }
            } else if (operation.equalsIgnoreCase("disconnectplayer")) {
                players.remove(in.readUTF());
            }

            in.close();
            byteArrayIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getBungeePlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Tellコマンドの実行処理を行う
     * 
     * @author ucchy
     * 
     * @param inviter
     * @param invitedName
     * @param message
     */
    protected void sendTellMessage(ChannelPlayer inviter, String invitedName, String message) {
        String PREERR = Resources.get("errorPrefix");
        ChannelPlayer invited = ChannelPlayer.getChannelPlayer(invitedName);

        // 招待相手が自分自身でないか確認する
        if (inviter.getName().equals(invited.getName())) {
            sendResourceMessage(inviter, PREERR, "errmsgCannotSendPMSelf");
            return;
        }

        // チャンネルが存在するかどうかをチェックする
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        String cname = inviter.getName() + ">" + invited.getName();
        Channel channel = api.getChannel(cname);
        if (channel == null) {
            // チャンネルを作成して、送信者、受信者をメンバーにする
            channel = api.createChannel(cname);
            channel.setVisible(false);
            channel.addMember(inviter);
            channel.addMember(invited);
            channel.setPrivateMessageTo(invited.getName());

        }

        // メッセージがあるなら送信する
        if (message.trim().length() > 0) {
            channel.chat(inviter, message);
        }

        // 送信履歴を残す
        DataMapsExtended.putIntoPMMap(invited.getName(), inviter.getName());
        DataMapsExtended.putIntoPMMap(inviter.getName(), invited.getName());
        return;
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * 
     * @author ucchy
     * 
     * @param cp   メッセージの送り先
     * @param pre  プレフィックス
     * @param key  リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    protected void sendResourceMessage(ChannelPlayer cp, String pre, String key, Object... args) {

        String org = Resources.get(key);
        if (org == null || org.equals("")) {
            return;
        }
        String msg = String.format(pre + org, args);
        cp.sendMessage(msg);
    }
}
