package com.github.lazygon.lunachatbridge.bungee.discord;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import javax.security.auth.login.LoginException;

/**
 * Discord Bot にログインし、 JDA (Java Discord API) を取得するクラス。
 */
public class DiscordUtils {

    /**
     * Discord にログインする。
     * 失敗した場合 null を返す
     *
     * @param token Bot にログインする際に必要な token
     * @return ログインできればその Bot の JDA, できなければ null
     * @since 1.0
     */
    public static JDA login(String token) {
        try {
            return new JDABuilder(AccountType.BOT)
                    .setBulkDeleteSplittingEnabled(false)
                    .setToken(token.trim())
                    .setContextEnabled(false)
                    .build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@link OnlineStatus} を文字列から取得する。
     * {@link OnlineStatus} で列挙されている識別子以外の文字列を渡すと
     * 強制的に {@link OnlineStatus#DO_NOT_DISTURB} (赤) を返す。
     *
     * @param status 取得したいステータスの識別子
     * @return 取得結果。失敗したら {@link OnlineStatus#DO_NOT_DISTURB}
     * @since 1.2-4.0.0-56
     */
    public static OnlineStatus getOnlineStatus(String status) {
        try {
            return OnlineStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return OnlineStatus.DO_NOT_DISTURB;
        }
    }

    /**
     * {@link Activity.ActivityType} を文字列から取得する。
     * {@link Activity.ActivityType} で列挙されている識別子以外の文字列を渡すと
     * 強制的に {@link Activity.ActivityType#DEFAULT} (赤) を返す。
     *
     * @param activity 取得したいアクティビティの識別子
     * @return 取得結果。失敗したら {@link Activity.ActivityType#DEFAULT}
     * @since 1.3-4.0.0_59
     */
    public static Activity.ActivityType getActivityType(String activity) {
        try {
            return ActivityType.valueOf(activity);
        } catch (IllegalArgumentException e) {
            return ActivityType.DEFAULT;
        }
    }
}
