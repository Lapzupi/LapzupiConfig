package com.lapzupi.dev.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * A utility class for managing YAML configuration files in a Bukkit/Spigot plugin.
 *
 * @param <T> The plugin class extending {@link JavaPlugin}.
 */
public class ConfigFile<T extends JavaPlugin> {
    private final String resourcePath;

    protected final T plugin;
    protected final String fileName;
    protected final File folder;
    protected File file;

    private FileConfiguration config;
    private boolean copyDefaults = false;

    /**
     * Constructs a new {@link ConfigFile}.
     *
     * @param plugin       The plugin instance.
     * @param resourcePath The path to the resource inside the plugin JAR.
     * @param fileName     The name of the configuration file.
     * @param folder       The folder where the configuration file resides.
     */
    public ConfigFile(@NotNull final T plugin, @NotNull final String resourcePath,
                      @NotNull final String fileName, @NotNull final String folder) {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.fileName = fileName;
        this.folder = new File(plugin.getDataFolder(), folder);
    }

    /**
     * Saves the default configuration file from the JAR if it doesn't exist.
     * Then reloads the configuration into memory.
     */
    public void saveDefaultConfig() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        if (!file.exists()) {
            if (!folder.exists() && !folder.mkdirs()) {
                plugin.getLogger().warning("Failed to create configuration folder: " + folder.getPath());
            }
            plugin.saveResource(resourcePath + fileName, false);
        }

        reloadConfig();
    }

    /**
     * Saves the current in-memory configuration to the file.
     */
    public void saveConfig() {
        if (config == null || file == null) {
            plugin.getLogger().warning("Config file or configuration is not initialized.");
            return;
        }

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save configuration file: %s".formatted(file.getPath()), ex);
        }
    }

    /**
     * Reloads the configuration from the file into memory.
     * If the file does not exist, a new one will be created.
     */
    public void reloadConfig() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Reloads the configuration and sets its defaults from the JAR resource if available.
     */
    public void reloadDefaultConfig() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        config = YamlConfiguration.loadConfiguration(file);
        try (InputStream resource = plugin.getResource(resourcePath + fileName)) {
            if (resource != null) {
                try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                    FileConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
                    if (copyDefaults) {
                        defConfig.options().copyDefaults(true);
                    }
                    config.setDefaults(defConfig);
                }
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload default configuration", ex);
        }
    }

    /**
     * Retrieves the current configuration. Reloads it if not already loaded.
     *
     * @return The current {@link FileConfiguration}.
     */
    @NotNull
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    /**
     * Checks whether default values are copied into the configuration.
     *
     * @return {@code true} if defaults are copied; {@code false} otherwise.
     */
    public boolean isCopyDefaults() {
        return copyDefaults;
    }

    /**
     * Sets whether default values should be copied into the configuration.
     *
     * @param copyDefaults {@code true} to copy defaults; {@code false} otherwise.
     */
    public void setCopyDefaults(boolean copyDefaults) {
        this.copyDefaults = copyDefaults;
    }
}
