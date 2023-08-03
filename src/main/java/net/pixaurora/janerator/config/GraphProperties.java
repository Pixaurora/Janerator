package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.pixaurora.janerator.graphing.ConfiguredGraphLogic;

public class GraphProperties {
    private ResourceKey<Level> dimension;
    private GrapherFactory grapherFactory;
    private FlatLevelSource shadedGenerator;
    private FlatLevelSource outlineGenerator;

    private ThreadLocal<ConfiguredGraphLogic> grapher;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            GrapherFactory.CODEC.fieldOf("function_to_graph").forGetter(GraphProperties::getGrapherFactory),
            FlatLevelSource.CODEC.fieldOf("shaded_in_generator").forGetter(GraphProperties::getShadedGenerator),
            FlatLevelSource.CODEC.fieldOf("outlines_generator").forGetter(GraphProperties::getOutlineGenerator)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, GrapherFactory grapherFactory, FlatLevelSource shadedGenerator, FlatLevelSource outlineGenerator) {
        this.dimension = dimension;

        this.grapherFactory = grapherFactory;

        this.shadedGenerator = shadedGenerator;
        this.outlineGenerator = outlineGenerator;

        this.grapher = ThreadLocal.withInitial(() -> this.grapherFactory.createGraphLogic());
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public GrapherFactory getGrapherFactory() {
        return this.grapherFactory;
    }

    public ConfiguredGraphLogic getLocalGrapher() {
        return this.grapher.get();
    }

    public FlatLevelSource getShadedGenerator() {
        return this.shadedGenerator;
    }

    public FlatLevelSource getOutlineGenerator() {
        return this.outlineGenerator;
    }
}
