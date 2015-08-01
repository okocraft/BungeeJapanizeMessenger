/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.bjm;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * BungeeJapanizeMessengerのdictionaryコマンド実装クラス
 * @author ucchy
 */
public class DictionaryCommand extends Command {

    private BungeeJapanizeMessenger parent;

    /**
     * コンストラクタ
     * @param parent
     * @param name コマンド
     */
    public DictionaryCommand(BungeeJapanizeMessenger parent, String name) {
        super(name);
        this.parent = parent;
    }

    /**
     * コマンドが実行された時に呼び出されるメソッド
     * @param sender コマンド実行者
     * @param args コマンド引数
     * @see net.md_5.bungee.api.plugin.Command#execute(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    @Override
    public void execute(CommandSender sender, String[] args) {

        // パーミッションチェック
        if ( !sender.hasPermission("bungeejapanizemessenger.dictionary") ) {
            sendMessage(sender, "コマンドの実行権限がありません。");
            return;
        }

        if ( args.length >= 3 && args[0].equalsIgnoreCase("add") ) {

            parent.getDictionary().put(args[1], args[2]);
            String message = String.format("[BJM日本語変換] %s を %s と覚えました。", args[1], args[2]);
            parent.getProxy().broadcast(TextComponent.fromLegacyText(message));
            return;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("remove") ) {

            parent.getDictionary().remove(args[1]);
            String message = String.format("[BJM日本語変換] %s を辞書から削除しました。", args[1]);
            parent.getProxy().broadcast(TextComponent.fromLegacyText(message));
            return;

        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("view") ) {

            HashMap<String, String> dictionary = parent.getDictionary().getDictionary();
            for ( String key : dictionary.keySet() ) {
                String value = dictionary.get(key);
                sendMessage(sender, key + ChatColor.GRAY + " -> " + ChatColor.WHITE + value);
            }
            return;

        } else {

            sendMessage(sender, "日本語変換の辞書登録コマンド：");
            sendMessage(sender, "/dic add (辞書登録する単語) (変換後の単語) - 新しい単語を登録する。");
            sendMessage(sender, "/dic remove (辞書から削除する単語) - 単語を辞書から削除する。");
            sendMessage(sender, "/dic view - 辞書に登録されている単語一覧を参照する。");
            return;
        }
    }

    /**
     * 指定した対象にメッセージを送信する
     * @param reciever 送信先
     * @param message メッセージ
     */
    protected void sendMessage(CommandSender reciever, String message) {
        if ( message == null ) return;
        reciever.sendMessage(TextComponent.fromLegacyText(message));
    }
}
