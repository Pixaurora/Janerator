package net.pixaurora.janerator.config;

import java.util.HashMap;
import java.util.Map;

import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.ValueMap;

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
import net.pixaurora.janerator.RegistryCache;

public class Generators implements ConfigSerializableObject<ValueMap<String>> {
    private static Map<String, ResourceKey<Level>> configFields = Map.of(
        "overworld_flat_preset", Level.OVERWORLD,
        "nether_flat_preset", Level.NETHER,
        "end_flat_preset", Level.END
    );
    private Map<String, String> presets;
    private Map<ResourceKey<Level>, ChunkGenerator> generators;

    public Generators(Map<String, String> presets) {
        this.presets = presets;
        this.generators = new HashMap<>(3);

        for (String field : configFields.keySet()) {
            if (!this.presets.containsKey(field)) {
                throw new GeneratorConfigException(String.format("Missing field for generator '%s'", field));
            }
        }
    }

    private void initializeGenerators() {
        RegistryCache registry = RegistryCache.INSTANCE;

        HolderGetter<Block> blockProvider = registry.getProvider(Registries.BLOCK);
        HolderGetter<Biome> biomeProvider = registry.getProvider(Registries.BIOME);
        HolderGetter<StructureSet> structureSetProvider = registry.getProvider(Registries.STRUCTURE_SET);
        HolderGetter<PlacedFeature> placedFeatureProvider = registry.getProvider(Registries.PLACED_FEATURE);

        for (String presetKey : Generators.configFields.keySet()) {
            String preset = this.presets.get(presetKey);
            ResourceKey<Level> dimension = Generators.configFields.get(presetKey);

            FlatLevelGeneratorSettings presetSettings = FlatLevelGeneratorSettings.getDefault(biomeProvider, structureSetProvider, placedFeatureProvider);
            presetSettings = FlatLevelFactory.createFromString(blockProvider, biomeProvider, structureSetProvider, placedFeatureProvider, preset, presetSettings);

            this.generators.put(dimension, new FlatLevelSource(presetSettings));
        }
    }

    public synchronized ChunkGenerator get(ResourceKey<Level> dimension) {
        if (this.generators.isEmpty()) {
            this.initializeGenerators();
        }

        ChunkGenerator generator = this.generators.get(dimension);
        return generator != null ? generator : this.generators.get(Level.OVERWORLD);
    }

    @Override
    public Generators copy() {
        return new Generators(this.presets);
    }

    @Override
    public Generators convertFrom(ValueMap<String> representation) {
        return new Generators(representation);
    }

    @Override
    public ValueMap<String> getRepresentation() {
        ValueMap.Builder<String> builder = ValueMap.builder("Enter preset here");

        this.presets.entrySet()
            .stream()
            .forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        return builder.build();
    }
}
