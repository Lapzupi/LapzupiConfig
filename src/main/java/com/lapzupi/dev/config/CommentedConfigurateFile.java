package com.lapzupi.dev.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.function.Consumer;

/**
 * @author sarhatabaot
 * @param <T> JavaPlugin
 * @param <U> Loader Type
 * @param <R> Builder Type
 */
public abstract class CommentedConfigurateFile<T extends JavaPlugin, U extends AbstractConfigurationLoader<CommentedConfigurationNode>,R extends AbstractConfigurationLoader.Builder<R, U>> extends ConfigFile<T> {
    protected final U loader;
    protected final R loaderBuilder;

    protected CommentedConfigurationNode rootNode;
    protected Transformation transformation;

    protected CommentedConfigurateFile(@NotNull final T plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);
        this.loaderBuilder = loadBuilder();

        this.loaderBuilder.defaultOptions(opts -> opts.serializers(this::builderOptions));
        this.loader = loaderBuilder.build();
        this.rootNode = loader.load();

        this.saveDefaultConfig();
        this.transformation = getTransformation();
        if (this.transformation != null) {
            loader.save(this.transformation.updateNode(rootNode));
            this.rootNode = loader.load();
        }

        initValues();
        plugin.getLogger().info(() -> "Loading " + fileName);
    }

    protected abstract void initValues() throws ConfigurateException;

    protected abstract R loadBuilder();
    
    protected abstract void builderOptions(TypeSerializerCollection.Builder builder);

    protected abstract Transformation getTransformation();

    @Override
    public void reloadConfig() {
        try {
            this.rootNode = loader.load();
            initValues();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

}
