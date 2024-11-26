package com.lapzupi.dev.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.File;
import java.nio.file.Paths;

import org.spongepowered.configurate.ConfigurationNode;

/**
 * A class responsible for handling the JSON-based configuration files using the Configurate library.
 * Provides functionality to load, transform, and save JSON configuration files.
 *
 * @param <T> The type of the JavaPlugin this configuration file is associated with.
 */
public abstract class JsonConfigurateFile<T extends JavaPlugin> extends ConfigFile<T> {

    protected final GsonConfigurationLoader loader;
    protected final GsonConfigurationLoader.Builder loaderBuilder;

    protected ConfigurationNode rootNode;
    protected Transformation transformation;

    /**
     * Constructs a JsonConfigurateFile object.
     *
     * This constructor initializes the configuration file by loading the JSON file
     * from the plugin's folder. It also applies any transformations, saves the
     * configuration, and initializes configuration values.
     *
     * @param plugin       The plugin instance that owns this configuration.
     * @param resourcePath The path within the JAR file where the default resource is located.
     * @param fileName     The name of the configuration file.
     * @param folder       The folder where the configuration file will be stored.
     * @throws ConfigurateException If there is an error loading or processing the configuration file.
     */
    protected JsonConfigurateFile(@NotNull final T plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);

        // Initialize the GsonConfigurationLoader and builder
        this.loaderBuilder = GsonConfigurationLoader.builder();

        // Apply builder-specific options
        builderOptions();

        // Set the path to the configuration file
        this.loader = loaderBuilder.path(Paths.get(folder + File.separator + fileName)).build();

        // Load the root node of the configuration file
        this.rootNode = loader.load();

        // Save the default config and apply transformations if necessary
        this.saveDefaultConfig();
        this.transformation = getTransformation();
        if (this.transformation != null) {
            loader.save(this.transformation.updateNode(rootNode));
            this.rootNode = loader.load();
        }

        // Initialize configuration values
        initValues();

        // Log that the file has been loaded
        plugin.getLogger().info(() -> "Loading " + fileName);
    }

    /**
     * Initializes the values in the configuration file.
     * This method must be implemented by subclasses to handle custom initialization logic.
     *
     * @throws ConfigurateException If there is an error initializing the configuration values.
     */
    protected abstract void initValues() throws ConfigurateException;

    /**
     * Configures the loader options for the GsonConfigurationLoader.
     * This method must be implemented by subclasses to specify custom loader options.
     */
    protected abstract void builderOptions();

    /**
     * Returns the transformation object that will be applied to the configuration node.
     * If no transformation is needed, this method should return null.
     *
     * @return The transformation to apply, or null if no transformation is needed.
     */
    protected abstract Transformation getTransformation();

    /**
     * Reloads the configuration file.
     * This method reinitializes the root node and applies any necessary transformations.
     *
     * @throws ConfigurateException If there is an error reloading the configuration file.
     */
    @Override
    public void reloadConfig() {
        try {
            // Reload the configuration node from the file
            this.rootNode = loader.load();

            // Reinitialize configuration values
            initValues();
        } catch (ConfigurateException e) {
            // Log any errors that occur during reload
            plugin.getLogger().severe(e.getMessage());
        }
    }
}

