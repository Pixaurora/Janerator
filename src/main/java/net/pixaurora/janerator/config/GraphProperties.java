package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.ChunkGrapher;
import net.pixaurora.janerator.graphing.ConfiguredGrapherSettings;


public class GraphProperties {
    private ResourceKey<Level> dimension;

    private ChunkGenerator shadedGenerator;
    private ChunkGenerator outlineGenerator;

    private ChunkGrapher grapher;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            ConfiguredGrapherSettings.CODEC.fieldOf("function_to_graph").forGetter(GraphProperties::getSettings),
            ChunkGenerator.CODEC.fieldOf("shaded_in_generator").forGetter(GraphProperties::getShadedGenerator),
            ChunkGenerator.CODEC.fieldOf("outlines_generator").forGetter(GraphProperties::getOutlineGenerator)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, ConfiguredGrapherSettings grapherSettings, ChunkGenerator shadedGenerator, ChunkGenerator outlineGenerator) {
        this.dimension = dimension;

        this.outlineGenerator = outlineGenerator;
        this.shadedGenerator = shadedGenerator;

        this.grapher = new ChunkGrapher(grapherSettings);
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public ConfiguredGrapherSettings getSettings() {
        return this.grapher.getSettings();
    }

    public ChunkGrapher getGrapher() {
        return this.grapher;
    }

    public ChunkGenerator getShadedGenerator() {
        return this.shadedGenerator;
    }

    public ChunkGenerator getOutlineGenerator() {
        return this.outlineGenerator;
    }
}
