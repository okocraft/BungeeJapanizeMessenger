package com.github.lazygon.lunachatbridge.bukkit.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import com.github.lazygon.lunachatbridge.bukkit.BukkitMain;
import com.github.lazygon.lunachatbridge.bukkit.config.BungeeChannels;
import com.github.lazygon.lunachatbridge.bukkit.lc.DataMapsExtended;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.event.PluginMessageEvent;

public class LunaChatListener implements Listener {

    private static final BukkitMain PLUGIN = BukkitMain.getInstance();
    private static final LunaChatListener INSTANCE = new LunaChatListener();

    private LunaChatListener() {
    }

    public static void start() {
        Bukkit.getPluginManager().registerEvents(INSTANCE, PLUGIN);
    }

    public static void stop() {
        HandlerList.unregisterAll(INSTANCE);
    }

    @EventHandler
    private void onChat(LunaChatChannelChatEvent event) {
        if (!event.getPlayer().isOnline()) {
            return;
        }

        if (!BungeeChannels.getInstance().isBungeeChannel(event.getChannelName())
                && !event.getChannel().isPersonalChat()) {
            return;
        }

        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOutStream);

            // 操作
            out.writeUTF("chat");

            // チャンネル名
            out.writeUTF(event.getChannelName());

            // プレイヤー名
            out.writeUTF(event.getPlayer().getName());

            // プレイヤー表示名
            out.writeUTF(event.getPlayer().getDisplayName());

            // プレイヤープレフィックス
            out.writeUTF(event.getPlayer().getPrefix());

            // プレイヤーサフィックス
            out.writeUTF(event.getPlayer().getSuffix());

            // プレイヤーのいるワールド
            out.writeUTF(event.getPlayer().getWorldName());

            // メッセージ
            out.writeUTF(event.getPreReplaceMessage());

            // 日本語化するかどうか
            out.writeBoolean(LunaChat.getInstance().getLunaChatAPI().isPlayerJapanize(event.getPlayer().getName()));

            // カラーコードを使えるかどうか
            out.writeBoolean(event.getPlayer().hasPermission("lunachat.allowcc"));

            Bukkit.getServer().sendPluginMessage(PLUGIN, "lc:tobungee", byteOutStream.toByteArray());
            out.close();
            byteOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * メッセージの伝わり方
     * </p>
     * </p>
     * この鯖
     * </p>
     * {@link LunaChatListener#onTell(PlayerCommandPreprocessEvent)} ->
     * {@link PluginMessages#sendTellMessage(ChannelPlayer, String, String)} ->
     * {@link LunaChatListener#onChat(LunaChatChannelChatEvent)} ->
     * </p>
     * バンジーコード
     * </p>
     * {@link PluginMessageListener#onPluginMessageReceived(PluginMessageEvent)} ->
     * </p>
     * ここから他鯖
     * </p>
     * {@link PluginMessages#onPluginMessageReceived(String, Player, byte[])} ->
     * {@link PluginMessages#sendTellMessage(ChannelPlayer, String, String)} ->
     * {@link LunaChatListener#onChat(LunaChatChannelChatEvent)}
     * 
     * @param event
     */
    @EventHandler
    public void onTell(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().hasPermission("lunachatbridge.tell")) {
            return;
        }
        String message = event.getMessage().substring(1);
        if (StringUtil.startsWithIgnoreCase(message, "lunachat:")) {
            message = message.substring(9);
        }

        if (!StringUtil.startsWithIgnoreCase(message, "tell ") && !StringUtil.startsWithIgnoreCase(message, "t")
                && !StringUtil.startsWithIgnoreCase(message, "m ") && !StringUtil.startsWithIgnoreCase(message, "msg")
                && !StringUtil.startsWithIgnoreCase(message, "message ")
                && !StringUtil.startsWithIgnoreCase(message, "w ")) {
            return;
        }

        // index  : 0   1       2
        // length : 1   2       3
        // message: tell player message1 message2
        String[] args = message.split(" ", 3);
        if (args.length != 3) {
            return;
        }
        String inviter = event.getPlayer().getName();
        String invited = args[1];
        if (Bukkit.getPlayer(invited) != null) {
            return;
        }
        for (String playerName : PluginMessages.getInstance().getBungeePlayers()) {
            if (playerName.equalsIgnoreCase(invited)) {
                invited = playerName;
                break;
            }
        }
        if (!PluginMessages.getInstance().getBungeePlayers().contains(invited)) {
            return;
        }
        event.setCancelled(true);
        message = args[2];

        PluginMessages.getInstance().sendTellMessage(ChannelPlayer.getChannelPlayer(inviter), invited, message);
    }

    /**
     * メッセージの伝わり方
     * </p>
     * </p>
     * この鯖
     * </p>
     * {@link LunaChatListener#onTell(PlayerCommandPreprocessEvent)} ->
     * {@link PluginMessages#sendTellMessage(ChannelPlayer, String, String)} ->
     * {@link LunaChatListener#onChat(LunaChatChannelChatEvent)} ->
     * </p>
     * バンジーコード
     * </p>
     * {@link PluginMessageListener#onPluginMessageReceived(PluginMessageEvent)} ->
     * </p>
     * ここから他鯖
     * </p>
     * {@link PluginMessages#onPluginMessageReceived(String, Player, byte[])} ->
     * {@link PluginMessages#sendTellMessage(ChannelPlayer, String, String)} ->
     * {@link LunaChatListener#onChat(LunaChatChannelChatEvent)}
     * 
     * @param event
     */
    @EventHandler
    public void onReply(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().hasPermission("lunachatbridge.tell")) {
            return;
        }
        String message = event.getMessage().substring(1);
        if (StringUtil.startsWithIgnoreCase(message, "lunachat:")) {
            message = message.substring(9);
        }

        if (!StringUtil.startsWithIgnoreCase(message, "reply ") && !StringUtil.startsWithIgnoreCase(message, "r ")) {
            return;
        }

        // index  : 0     1
        // length : 1     2
        // message: reply message1 message2
        String[] args = message.split(" ", 2);
        if (args.length != 2) {
            return;
        }
        String inviter = event.getPlayer().getName();
        String invited = DataMapsExtended.getFromPMMap(inviter);
        if (invited == null || Bukkit.getPlayer(invited) != null) {
            return;
        }
        if (!PluginMessages.getInstance().getBungeePlayers().contains(invited)) {
            return;
        }

        event.setCancelled(true);
        message = args[1];

        PluginMessages.getInstance().sendTellMessage(ChannelPlayer.getChannelPlayer(inviter), invited, message);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        // 1.14以下は未対応
        if (Bukkit.getBukkitVersion().compareTo("1.14") <= 0) {
            return;
        }

        String message = event.getBuffer().substring(1);
        if (StringUtil.startsWithIgnoreCase(message, "lunachat:")) {
            message = message.substring(9);
        }

        if (!StringUtil.startsWithIgnoreCase(message, "tell") && !StringUtil.startsWithIgnoreCase(message, "t")
                && !StringUtil.startsWithIgnoreCase(message, "m") && !StringUtil.startsWithIgnoreCase(message, "msg")
                && !StringUtil.startsWithIgnoreCase(message, "message")
                && !StringUtil.startsWithIgnoreCase(message, "w")) {
            return;
        }

        // index  : 0   1       2
        // length : 1   2       3
        // message: tell player message1 message2
        String[] args = message.split(" ", 3);
        if (args.length == 2) {            
            String player = args[1];
            List<String> players = PluginMessages.getInstance().getBungeePlayers();
            players.removeIf(playerName -> !StringUtil.startsWithIgnoreCase(playerName, player));
            event.setCompletions(players);
        }
    }
}