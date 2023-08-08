package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.pixaurora.janerator.graphing.Grapher;
import net.pixaurora.janerator.graphing.ConfiguredGrapherSettings;

public class GraphProperties {
    private ResourceKey<Level> dimension;
    private ConfiguredGrapherSettings grapherSettings;
    private FlatLevelSource shadedGenerator;
    private FlatLevelSource outlineGenerator;

    private ThreadLocal<Grapher> grapher;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            ConfiguredGrapherSettings.CODEC.fieldOf("function_to_graph").forGetter(GraphProperties::getGrapherSettings),
            FlatLevelSource.CODEC.fieldOf("shaded_in_generator").forGetter(GraphProperties::getShadedGenerator),
            FlatLevelSource.CODEC.fieldOf("outlines_generator").forGetter(GraphProperties::getOutlineGenerator)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, ConfiguredGrapherSettings grapherSettings, FlatLevelSource shadedGenerator, FlatLevelSource outlineGenerator) {
        this.dimension = dimension;

        this.grapherSettings = grapherSettings;

        this.shadedGenerator = shadedGenerator;
        this.outlineGenerator = outlineGenerator;

        this.grapher = ThreadLocal.withInitial(() -> Grapher.fromConfig(this.grapherSettings));
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public ConfiguredGrapherSettings getGrapherSettings() {
        return this.grapherSettings;
    }

    public Grapher getLocalGrapher() {
        return this.grapher.get();
    }

    public FlatLevelSource getShadedGenerator() {
        return this.shadedGenerator;
    }

    public FlatLevelSource getOutlineGenerator() {
        return this.outlineGenerator;
    }
}
