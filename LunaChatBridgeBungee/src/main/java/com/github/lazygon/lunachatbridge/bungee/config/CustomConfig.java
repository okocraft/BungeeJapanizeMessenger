package com.github.lazygon.lunachatbridge.bungee.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            InputStream inputStream = plugin.getResourceAsStream("bungee/" + name);
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
        if (!file.exists()) {
            saveResource(name, false);
        }
    }

    private void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResourceAsStream("bungee/" + resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.file);
        }

        File outFile = new File(plugin.getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf(47);
        File outDir = new File(plugin.getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (outFile.exists() && !replace) {
            plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            return;
        }

        try {
            OutputStream out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
            plugin.getLogger().info("file copied!");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, e);
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