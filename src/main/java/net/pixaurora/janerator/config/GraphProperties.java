package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;

public class GraphProperties {
    private ResourceKey<Level> dimension;

    private ChunkGenerator shadedGenerator;
    private ChunkGenerator outlinesGenerator;

    private ChunkGrapher grapher;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            ChunkGrapher.CODEC.fieldOf("grapher").forGetter(GraphProperties::getGrapher),
            ChunkGenerator.CODEC.fieldOf("shaded_in_generator").forGetter(GraphProperties::getShadedGenerator),
            ChunkGenerator.CODEC.fieldOf("outlines_generator").forGetter(GraphProperties::getOutlinesGenerator)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, ChunkGrapher grapher, ChunkGenerator shadedGenerator, ChunkGenerator outlinesGenerator) {
        this.dimension = dimension;

        this.outlinesGenerator = outlinesGenerator;
        this.shadedGenerator = shadedGenerator;

        this.grapher = grapher;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public ChunkGrapher getGrapher() {
        return this.grapher;
    }

    public ChunkGenerator getShadedGenerator() {
        return this.shadedGenerator;
    }

    public ChunkGenerator getOutlinesGenerator() {
        return this.outlinesGenerator;
    }
}
