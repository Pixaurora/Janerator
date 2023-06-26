package dev.pixirora.janerator.config;

import java.util.HashMap;
import java.util.Map;

import dev.pixirora.janerator.RegistryCache;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class Generators {
    private static Map<ResourceKey<Level>, ChunkGenerator> generators = new HashMap<>(3);
    public static Map<ResourceKey<Level>, String> configFields = Map.of(
        Level.OVERWORLD, "overworld",
        Level.NETHER, "nether",
        Level.END, "end"
    );

    private static void initializeGenerators() {
        HolderGetter<Block> blockProvider = RegistryCache.INSTANCE.getProvider(Registries.BLOCK);
        HolderGetter<Biome> biomeProvider = RegistryCache.INSTANCE.getProvider(Registries.BIOME);
        HolderGetter<StructureSet> structureSetProvider = RegistryCache.INSTANCE.getProvider(Registries.STRUCTURE_SET);
        HolderGetter<PlacedFeature> placedFeatureProvider = RegistryCache.INSTANCE.getProvider(Registries.PLACED_FEATURE);

        for (ResourceKey<Level> dimension : configFields.keySet()) {
            String preset = JaneratorConfig.getGeneratorPreset(dimension);

            FlatLevelGeneratorSettings presetSettings = FlatLevelGeneratorSettings.getDefault(biomeProvider, structureSetProvider, placedFeatureProvider);
            presetSettings = FlatLevelFactory.createFromString(blockProvider, biomeProvider, structureSetProvider, placedFeatureProvider, preset, presetSettings);

            generators.put(dimension, new FlatLevelSource(presetSettings));
        }
    }

    public static synchronized ChunkGenerator get(ResourceKey<Level> dimension) {
        if (Generators.generators.isEmpty()) {
            initializeGenerators();
        }

        ChunkGenerator generator = Generators.generators.get(dimension);
        return generator != null ? generator : Generators.get(Level.OVERWORLD);
    }
}
