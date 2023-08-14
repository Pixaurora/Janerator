package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Grapher;
import net.pixaurora.janerator.graphing.ConfiguredGrapherSettings;

public class GraphProperties {
    private ResourceKey<Level> dimension;
    private ConfiguredGrapherSettings grapherSettings;
    private ChunkGenerator shadedGenerator;
    private ChunkGenerator outlineGenerator;

    private ThreadLocal<Grapher> grapher;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            ConfiguredGrapherSettings.CODEC.fieldOf("function_to_graph").forGetter(GraphProperties::getGrapherSettings),
            ChunkGenerator.CODEC.fieldOf("shaded_in_generator").forGetter(GraphProperties::getShadedGenerator),
            ChunkGenerator.CODEC.fieldOf("outlines_generator").forGetter(GraphProperties::getOutlineGenerator)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, ConfiguredGrapherSettings grapherSettings, ChunkGenerator shadedGenerator, ChunkGenerator outlineGenerator) {
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

    public ChunkGenerator getShadedGenerator() {
        return this.shadedGenerator;
    }

    public ChunkGenerator getOutlineGenerator() {
        return this.outlineGenerator;
    }
}
