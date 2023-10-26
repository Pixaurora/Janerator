package net.pixaurora.janerator.worldgen.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

public class PreparedGenerators {
    public static final Codec<PreparedGenerators> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, ChunkGenerator.CODEC).fieldOf("defined").forGetter(PreparedGenerators::userGenerators),
            Codec.unboundedMap(Codec.STRING, ResourceKey.codec(Registries.DIMENSION)).fieldOf("default_generator_dimensions").forGetter(PreparedGenerators::defaultGeneratorDimensions),
            RegistryOps.retrieveGetter(Registries.LEVEL_STEM)
        ).apply(instance, PreparedGenerators::new)
    );

    public final Map<String, ChunkGenerator> userGenerators;
    public final Map<String, ResourceKey<Level>> defaultGenerators;

    public final Map<String, ChunkGenerator> allGenerators;

    public PreparedGenerators(Map<String, ChunkGenerator> userGenerators,
            Map<String, ResourceKey<Level>> defaultGeneratorDimensions,
            HolderGetter<LevelStem> dimensionGeneratorRegistry) {
        this.userGenerators = userGenerators;
        this.defaultGenerators = defaultGeneratorDimensions;

        this.allGenerators = new HashMap<>();

        this.allGenerators.putAll(userGenerators);
        for (Map.Entry<String, ResourceKey<Level>> specifiedDefault : defaultGeneratorDimensions.entrySet()) {
            ResourceKey<LevelStem> generatorLocation = Registries.levelToLevelStem(specifiedDefault.getValue());
            LevelStem defaultGenerator = dimensionGeneratorRegistry.get(generatorLocation).get().value();

            this.allGenerators.put(specifiedDefault.getKey(), defaultGenerator.generator());
        }
    }

    public Map<String, ChunkGenerator> userGenerators() {
        return this.userGenerators;
    }

    public Map<String, ResourceKey<Level>> defaultGeneratorDimensions() {
        return this.defaultGenerators;
    }

    public ChunkGenerator get(String name) {
        return this.allGenerators.get(name);
    }

    public Collection<ChunkGenerator> getAll() {
        return this.allGenerators.values();
    }
}
