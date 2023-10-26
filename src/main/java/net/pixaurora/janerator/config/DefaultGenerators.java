package net.pixaurora.janerator.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.pixaurora.janerator.RegistryCache;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;
import net.pixaurora.janerator.worldgen.generator.PreparedGenerators;
import net.pixaurora.janerator.worldgen.generator.SlantedFlatGenerator;
import net.pixaurora.janerator.worldgen.settings.SlantedFlatGeneratorSettings;

public class DefaultGenerators {
    public static ChunkGenerator createShadedOverworldGenerator() {
        return createGenerator(
            Biomes.MUSHROOM_FIELDS,
            new FlatLayerInfo(1, Blocks.GRASS_BLOCK),
            new FlatLayerInfo(2, Blocks.DIRT),
            new FlatLayerInfo(60, Blocks.STONE),

            new FlatLayerInfo(63, Blocks.DEEPSLATE),
            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    public static ChunkGenerator createOutlineOverworldGenerator() {
        return new SlantedFlatGenerator(
            new FixedBiomeSource(getBiome(Biomes.MUSHROOM_FIELDS)),
            new SlantedFlatGeneratorSettings(
                List.of(
                    Blocks.RED_CONCRETE,
                    Blocks.ORANGE_CONCRETE,
                    Blocks.YELLOW_CONCRETE,
                    Blocks.LIME_CONCRETE,
                    Blocks.LIGHT_BLUE_CONCRETE,
                    Blocks.BLUE_CONCRETE,
                    Blocks.PURPLE_CONCRETE,
                    Blocks.MAGENTA_CONCRETE
                ).stream().map(Block::defaultBlockState).toList(),
                128,
                new GraphFunctionDefinition(List.of("x", "z"), List.of(), List.of(), "x + z"))
            );
    }

    public static ChunkGenerator createShadedNetherGenerator() {
        return createGenerator(
            Biomes.DEEP_DARK,
            new FlatLayerInfo(1, Blocks.WARPED_NYLIUM),
            new FlatLayerInfo(30, Blocks.NETHERRACK),

            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    public static ChunkGenerator createShadedEndGenerator() {
        return createGenerator(
            Biomes.DEEP_DARK,
            new FlatLayerInfo(1, Blocks.GRASS_BLOCK),
            new FlatLayerInfo(2, Blocks.DIRT),
            new FlatLayerInfo(59, Blocks.STONE),

            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    private static Holder<Biome> getBiome(ResourceKey<Biome> biomeKey) {
        return RegistryCache.INSTANCE.getRegistry(Registries.BIOME).getHolderOrThrow(biomeKey);
    }

    private static ChunkGenerator createGenerator(ResourceKey<Biome> biomeKey, FlatLayerInfo... layersBackward) {
        List<Holder<PlacedFeature>> placedFeatures = List.of();
        Optional<HolderSet<StructureSet>> optional = Optional.of(HolderSet.direct());

        List<FlatLayerInfo> layers = new ArrayList<>(List.of(layersBackward));
        Collections.reverse(layers);

        Holder<Biome> biome = getBiome(biomeKey);

        return new FlatLevelSource(
            new FlatLevelGeneratorSettings(optional, biome, placedFeatures)
                .withBiomeAndLayers(layers, optional, biome)
        );
    }

    public static PreparedGenerators getOverworldGenerators() {
	return new PreparedGenerators(
            Map.of(
                "grassy_mushroom", createShadedOverworldGenerator(),
                "rainbow_outline", createOutlineOverworldGenerator()
            ),
            Map.of(
                 "default_overworld", Level.OVERWORLD
            ),
            RegistryCache.INSTANCE.getProvider(Registries.LEVEL_STEM)
        );
    }
}
