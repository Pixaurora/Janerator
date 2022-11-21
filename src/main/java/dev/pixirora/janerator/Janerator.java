package dev.pixirora.janerator;

import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Janerator {
    private static FlatLevelSource defaultGenerator;
    private static FlatLevelSource netherGenerator;
    private static FlatLevelSource endGenerator;

    private static boolean initialized = false;

    public static final Logger LOGGER = LoggerFactory.getLogger("Janerator");

    public static FlatLevelSource getGenerator(ResourceKey<Level> dimension) {
        if (!initialized) {
            initializeGenerators();
            initialized = true;
        }

        if (dimension == Level.END) {
            return endGenerator;
        } else if (dimension == Level.NETHER) {
            return netherGenerator;
        } else {
            return defaultGenerator;
        }
    }

    public static FlatLevelSource getDefaultGenerator() {
        return defaultGenerator;
    }

    private static synchronized void initializeGenerators() {
        defaultGenerator = Janerator.createSource(Janerator.overworldLayers(), Biomes.MUSHROOM_FIELDS);
        netherGenerator = Janerator.createSource(Janerator.netherLayers(), Biomes.DEEP_DARK);
        endGenerator = Janerator.createSource(Janerator.endLayers(), Biomes.MUSHROOM_FIELDS);
    }

    public static boolean shouldOverride(ChunkPos chunkPos) {
        return shouldOverride(chunkPos.x, chunkPos.z);
    }

    public static boolean shouldOverride(int x, int z) {
        return x >= 0;
    }

    private static List<FlatLayerInfo> overworldLayers() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
        layers.add(new FlatLayerInfo(63, Blocks.DEEPSLATE));

        layers.add(new FlatLayerInfo(60, Blocks.STONE));
        layers.add(new FlatLayerInfo(2, Blocks.DIRT));
        layers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));

        return layers;
    }

    private static List<FlatLayerInfo> netherLayers() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));

        layers.add(new FlatLayerInfo(30, Blocks.NETHERRACK));
        layers.add(new FlatLayerInfo(1, Blocks.WARPED_NYLIUM));

        return layers;
    }

    private static List<FlatLayerInfo> endLayers() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));

        layers.add(new FlatLayerInfo(59, Blocks.STONE));
        layers.add(new FlatLayerInfo(2, Blocks.DIRT));
        layers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));

        return layers;
    }

    private static FlatLevelSource createSource(List<FlatLayerInfo> layers, ResourceKey<Biome> biomeResourceKey) {
        Optional<HolderSet<StructureSet>> optional = Optional.empty();
        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(optional, BuiltinRegistries.BIOME)
                .withLayers(layers, optional);

        settings.setBiome(BuiltinRegistries.BIOME.getOrCreateHolderOrThrow(biomeResourceKey));

        return new FlatLevelSource(BuiltinRegistries.STRUCTURE_SETS, settings);
    }
}
