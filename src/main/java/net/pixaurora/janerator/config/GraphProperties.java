package net.pixaurora.janerator.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.pixaurora.janerator.worldgen.generator.MultiGenOrganizer;

public class GraphProperties {
    private ResourceKey<Level> dimension;
    private MultiGenOrganizer organizer;

    public static Codec<GraphProperties> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GraphProperties::getDimension),
            MultiGenOrganizer.CODEC.fieldOf("organization").forGetter(GraphProperties::getOrganizer)
        ).apply(instance, GraphProperties::new)
    );

    public GraphProperties(ResourceKey<Level> dimension, MultiGenOrganizer organization) {
        this.dimension = dimension;
        this.organizer = organization;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public MultiGenOrganizer getOrganizer() {
        return organizer;
    }
}
