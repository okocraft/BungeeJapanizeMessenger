package com.github.lazygon.lunachatbridge.bukkit.lc;

import com.github.ucchyocean.lc.command.DataMaps;

public class DataMapsExtended extends DataMaps {
    
    public static void putIntoInviteMap(String key, String value) {
        inviteMap.put(key, value);
    }

    public static void removeFromInviteMap(String key) {
        inviteMap.remove(key);
    }

    public static String getFromInviteMap(String key) {
        return inviteMap.get(key);
    }

    public static void putIntoInviterMap(String key, String value) {
        inviterMap.put(key, value);
    }

    public static void removeFromInviterMap(String key) {
        inviterMap.remove(key);
    }

    public static String getFromInviterMap(String key) {
        return inviterMap.get(key);
    }

    public static void putIntoPMMap(String key, String value) {
        privateMessageMap.put(key, value);
    }

    public static void removeFromPMMap(String key) {
        privateMessageMap.remove(key);
    }

    public static String getFromPMMap(String key) {
        return privateMessageMap.get(key);
    }
}