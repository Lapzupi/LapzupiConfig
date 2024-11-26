package com.lapzupi.dev.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * A base class for loading and handling configuration files with support for custom transformations
 * and the use of a specific loader type.
 *
 * @param <T> The type of JavaPlugin that owns this configuration.
 * @param <U> The type of the loader used for configuration loading.
 * @param <R> The type of the builder used to construct the loader.
 */
public abstract class CommentedConfigurateFile<T extends JavaPlugin,
        U extends AbstractConfigurationLoader<@NotNull CommentedConfigurationNode>,
        R extends AbstractConfigurationLoader.Builder<@NotNull R, U>> extends ConfigFile<T> {

    // The configuration loader instance
    protected final U loader;

    // The builder for the loader
    protected final R loaderBuilder;

    // Root node of the configuration
    protected CommentedConfigurationNode rootNode;

    // Transformation to apply to the configuration
    protected Transformation transformation;

    /**
     * Constructs a new CommentedConfigurateFile instance and initializes the configuration
     * file by loading, applying transformations, and setting up the loader and root node.
     *
     * @param plugin The plugin instance associated with this configuration file.
     * @param resourcePath The path to the resource inside the plugin's JAR file.
     * @param fileName The name of the configuration file.
     * @param folder The folder where the configuration file is stored.
     *
     * @throws ConfigurateException If there is an error loading the configuration or applying transformations.
     */
    protected CommentedConfigurateFile(@NotNull final T plugin,
                                       final String resourcePath,
                                       final String fileName,
                                       final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);

        // Initialize the builder and loader
        this.loaderBuilder = loadBuilder();
        this.loaderBuilder.defaultOptions(opts -> opts.serializers(this::builderOptions));
        this.loader = loaderBuilder.build();

        // Load the root node and apply transformations
        this.rootNode = loader.load();
        this.saveDefaultConfig();

        // Apply transformation if defined
        this.transformation = getTransformation();
        if (this.transformation != null) {
            loader.save(this.transformation.updateNode(rootNode));
            this.rootNode = loader.load();
        }

        // Initialize values for the configuration
        initValues();

        // Log the successful loading of the configuration
        plugin.getLogger().info(() -> "Loading " + fileName);
    }

    /**
     * Abstract method to initialize values in the configuration. Implementing classes
     * should define how to load specific values from the configuration.
     *
     * @throws ConfigurateException If there is an error initializing values.
     */
    protected abstract void initValues() throws ConfigurateException;

    /**
     * Abstract method to load the builder for the configuration loader.
     *
     * @return The builder for the configuration loader.
     */
    protected abstract R loadBuilder();

    /**
     * Abstract method to define the builder options. Implementing classes
     * should configure the serialization options here.
     *
     * @param builder The builder instance to configure.
     */
    protected abstract void builderOptions(TypeSerializerCollection.Builder builder);

    /**
     * Abstract method to retrieve a transformation to apply to the configuration.
     *
     * @return A Transformation instance or null if no transformation is required.
     */
    protected abstract Transformation getTransformation();

    /**
     * Reloads the configuration by reloading the root node and applying any necessary
     * transformations and value initializations.
     */
    @Override
    public void reloadConfig() {
        try {
            // Reload the root node
            this.rootNode = loader.load();
            // Reinitialize values after reloading
            initValues();
        } catch (ConfigurateException e) {
            // Log error if configuration reload fails
            plugin.getLogger().severe(e.getMessage());
        }
    }
}

