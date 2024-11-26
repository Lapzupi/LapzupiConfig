package com.lapzupi.dev.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import static org.spongepowered.configurate.NodePath.path;


/**
 * Abstract class responsible for transforming a configuration node by applying versioned transformations.
 * The transformations are applied based on the configuration version and may update the schema.
 */
public abstract class Transformation {

    private static final Logger logger = LoggerFactory.getLogger(Transformation.class);

    /**
     * Creates an initial transformation that sets the config version to 0.
     * This transformation will be applied to the root node or any existing configuration node.
     *
     * @return A configuration transformation with an action that sets the config version.
     */
    public ConfigurationTransformation initialTransformation() {
        return ConfigurationTransformation.builder()
                .addAction(path("", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    // Set the config version to 0
                    value.node("config-version").set(0);
                    return null; // don't move the value
                })
                .build();
    }

    /**
     * Returns the latest available version of the configuration.
     * Implementing classes should define the version handling logic.
     *
     * @return The latest version.
     */
    public abstract int getLatestVersion();

    /**
     * Creates a versioned configuration transformation.
     * This method should be implemented to return an appropriate versioned transformation object.
     *
     * @return A versioned transformation.
     */
    protected abstract ConfigurationTransformation.Versioned create();

    /**
     * Applies the transformations to a configuration node.
     * This method checks the current version of the configuration and applies the necessary transformation actions.
     * It also logs the version change after the transformation is applied.
     *
     * @param node The node to transform.
     * @param <N> The type of the node.
     * @return The transformed configuration node.
     * @throws ConfigurateException If an error occurs while applying the transformation.
     */
    public <N extends ConfigurationNode> N updateNode(@NotNull N node) throws ConfigurateException {
        if (!node.virtual()) {
            // Only apply transformation to non-virtual nodes
            ConfigurationTransformation.Versioned transformation = create();
            int startVersion = transformation.version(node);
            transformation.apply(node);
            int endVersion = transformation.version(node);

            // Log the version update if there was a change
            if (startVersion != endVersion) {
                logger.info("Updated config schema from {} to {}", startVersion, endVersion);
            }
        }
        return node;
    }
}
