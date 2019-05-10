/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.bjm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
//import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
//import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

//import com.github.ucchyocean.lc.japanize.Japanizer;

/**
 * サーバー間tellコマンドおよびJapanize化プラグイン
 * 
 * @author ucchy
 */
public class BungeeJapanizeMessenger extends Plugin implements Listener {

    //private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd";
    //private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";

    //private SimpleDateFormat dateFormat;
    //private SimpleDateFormat timeFormat;

    private HashMap<String, String> history;
    private BJMConfig config;
    private JapanizeDictionary dictionary;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * 
     * @see net.md_5.bungee.api.plugin.Plugin#onEnable()
     */
    @Override
    public void onEnable() {

        // 初期化
        history = new HashMap<String, String>();
        //dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        //timeFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN);

        // コマンド登録
        for (String command : new String[] { "tell", "msg", "message", "w", "t" }) {
            getProxy().getPluginManager().registerCommand(this, new TellCommand(this, command));
        }
        for (String command : new String[] { "reply", "r" }) {
            getProxy().getPluginManager().registerCommand(this, new ReplyCommand(this, command));
        }
        for (String command : new String[] { "dictionary", "dic" }) {
            getProxy().getPluginManager().registerCommand(this, new DictionaryCommand(this, command));
        }

        getProxy().registerChannel("lunachat:out");
        getProxy().registerChannel("lunachat:in");

        // コンフィグ取得
        config = new BJMConfig(this);

        // 辞書取得
        dictionary = new JapanizeDictionary(this);

        // リスナー登録
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent e){
        if (!e.getTag().equalsIgnoreCase("lunachat:in")){
            return;
        }
        ByteArrayInputStream byteIn = new ByteArrayInputStream(e.getData());
        DataInputStream in = new DataInputStream(byteIn);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        Server sendServer = (Server) e.getSender();

        try {
            String lunaChatChannelName = in.readUTF();
            out.writeUTF(lunaChatChannelName);

            String playerName = in.readUTF();
            out.writeUTF(playerName);

            String chat = in.readUTF();
            out.writeUTF(chat);

            String lineFormat = in.readUTF();
            out.writeUTF(lineFormat);

            String serverName = sendServer.getInfo().getName();
            out.writeUTF(serverName);

            Map<String, ServerInfo> servers = getProxy().getServers();
            for (String sendingServer : servers.keySet()){
                if (servers.get(sendingServer).getPlayers().size() == 0) continue;
                if (sendServer.getInfo().getName().equalsIgnoreCase(servers.get(sendingServer).getName())) continue;

                getProxy().getServers().get(sendingServer).sendData("lunachat:out", byteOut.toByteArray());
            }

            out.close();
            in.close();
            byteIn.close();
            byteOut.close();

        } catch (IOException exception){
            exception.printStackTrace();
            return;
        }
        
    }

    /**
     * コンフィグを返す
     * 
     * @return コンフィグ
     */
    public BJMConfig getConfig() {
        return config;
    }

    /**
     * 辞書を返す
     * 
     * @return 辞書
     */
    public JapanizeDictionary getDictionary() {
        return dictionary;
    }

    /**
     * プライベートメッセージの受信履歴を記録する
     * 
     * @param reciever 受信者
     * @param sender   送信者
     */
    protected void putHistory(String reciever, String sender) {
        history.put(reciever, sender);
    }

    /**
     * プライベートメッセージの受信履歴を取得する
     * 
     * @param reciever 受信者
     * @return 送信者
     */
    protected String getHistory(String reciever) {
        return history.get(reciever);
    }

    /**
     * 指定した対象にメッセージを送信する
     * 
     * @param reciever 送信先
     * @param message  メッセージ
     */
    protected void sendMessage(CommandSender reciever, String message) {
        if (message == null)
            return;
        reciever.sendMessage(TextComponent.fromLegacyText(message));
    }
}
