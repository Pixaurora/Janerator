package net.pixaurora.janerator.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.pixaurora.janerator.RegistryCache;

public class DefaultFlatPresets {
    public static FlatLevelSource createShadedOverworldGenerator() {
        return createGenerator(
            Biomes.MUSHROOM_FIELDS,
            new FlatLayerInfo(1, Blocks.GRASS_BLOCK),
            new FlatLayerInfo(2, Blocks.DIRT),
            new FlatLayerInfo(60, Blocks.STONE),

            new FlatLayerInfo(63, Blocks.DEEPSLATE),
            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    public static FlatLevelSource createOutlineOverworldGenerator() {
        return createGenerator(
            Biomes.MUSHROOM_FIELDS,
            new FlatLayerInfo(127, Blocks.GLOWSTONE)
        );
    }

    public static FlatLevelSource createShadedNetherGenerator() {
        return createGenerator(
            Biomes.DEEP_DARK,
            new FlatLayerInfo(1, Blocks.WARPED_NYLIUM),
            new FlatLayerInfo(30, Blocks.NETHERRACK),

            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    public static FlatLevelSource createShadedEndGenerator() {
        return createGenerator(
            Biomes.DEEP_DARK,
            new FlatLayerInfo(1, Blocks.GRASS_BLOCK),
            new FlatLayerInfo(2, Blocks.DIRT),
            new FlatLayerInfo(59, Blocks.STONE),

            new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

    private static FlatLevelSource createGenerator(ResourceKey<Biome> biome, FlatLayerInfo... layersBackward) {
        List<Holder<PlacedFeature>> placedFeatures = List.of();
        Optional<HolderSet<StructureSet>> optional = Optional.of(HolderSet.direct());

        List<FlatLayerInfo> layers = new ArrayList<>(List.of(layersBackward));
        Collections.reverse(layers);

        Holder<Biome> biomeHolder = RegistryCache.INSTANCE.getRegistry(Registries.BIOME).getHolderOrThrow(biome);
        return new FlatLevelSource(
            new FlatLevelGeneratorSettings(optional, biomeHolder, placedFeatures)
                .withBiomeAndLayers(layers, optional, biomeHolder)
        );
    }
}
