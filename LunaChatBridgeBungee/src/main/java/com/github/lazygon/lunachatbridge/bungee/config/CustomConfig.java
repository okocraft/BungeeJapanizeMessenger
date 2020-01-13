package com.github.lazygon.lunachatbridge.bungee.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import com.github.lazygon.lunachatbridge.bungee.BungeeMain;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Class for manipulating yaml files.
 *
 * @author LazyGon
 */
public abstract class CustomConfig {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private static final BungeeMain plugin = BungeeMain.getInstance();
    private final File file;
    private final String name;
    private Configuration config;

    CustomConfig(String name) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), this.name);
        reload();
        if (file.isDirectory()) {
            throw new IllegalArgumentException("file must not be directory");
        }
    }

    CustomConfig(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("file must not be directory");
        }
        this.file = file;
        this.name = file.getName();
        reload();
    }

    /**
     * Gets FileConfiguration of {@code file}.
     *
     * @return Configuration
     * @author LazyGon
     */
    protected Configuration get() {
        if (config == null) {
            reload();
        }

        return config;
    }

    /**
     * Loads Configuration from {@code file}.
     *
     * @author LazyGon
     */
    public void reload() {
        saveDefault();
        try {
            InputStream inputStream = plugin.getResourceAsStream("bungeecord/" + name);
            if (inputStream != null) {
                config = provider.load(file, provider.load(inputStream));
            } else {
                config = provider.load(file);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot load configuration.", e);
            config = provider.load("");
        }

    }

    /**
     * Saves default file which is included in jar.
     *
     * @author LazyGon
     */
    public void saveDefault() {
        try {
            InputStream inputStream = plugin.getResourceAsStream("bungeecord/" + name);
            if (inputStream != null && !file.exists()) {
                provider.save(provider.load(inputStream), file);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "There is no resource named " + name + " in jar file", e);
        }
    }

    /**
     * 設定ファイルを保存する。
     *
     * @author LazyGon
     */
    public void save() {
        if (config == null)
            return;
        try {
            provider.save(get(), file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, e);
        }
    }
}