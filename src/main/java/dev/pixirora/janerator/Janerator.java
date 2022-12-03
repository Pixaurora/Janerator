package dev.pixirora.janerator;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public class Janerator {
    private static MinecraftServer server = null;
    private static Map<ResourceKey<Level>, ChunkGenerator> generators = Maps.newHashMapWithExpectedSize(3);

    public static boolean shouldOverride(int x, int z) {
        return x >= 0;
    }

    public static boolean shouldOverride(ChunkPos chunkPos) {
        return shouldOverride(chunkPos.x, chunkPos.z);
    }

    public static void setMinecraftServer(MinecraftServer server) {
        Janerator.server = server;
    }

    public static synchronized ChunkGenerator getGenerator(ResourceKey<Level> dimension) {
        if (Janerator.generators.isEmpty()) {
            initializeGenerators();
        }

        ChunkGenerator generator = Janerator.generators.get(dimension);
        return generator != null ? generator : Janerator.generators.get(Level.OVERWORLD);
    }

    private static <T> Registry<T> getRegistry(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return Janerator.server.registryAccess().registryOrThrow(registryKey);
    }

    private static void initializeGenerators() {
        Janerator.generators.put(Level.OVERWORLD, createOverworldGenerator());
        Janerator.generators.put(Level.NETHER, createNetherGenerator());
        Janerator.generators.put(Level.END, createEndGenerator());
    }

    private static FlatLevelSource createOverworldGenerator() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
        layers.add(new FlatLayerInfo(63, Blocks.DEEPSLATE));

        layers.add(new FlatLayerInfo(60, Blocks.STONE));
        layers.add(new FlatLayerInfo(2, Blocks.DIRT));
        layers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));

        return createGenerator(layers, Biomes.MUSHROOM_FIELDS);
    }

    private static FlatLevelSource createNetherGenerator() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));

        layers.add(new FlatLayerInfo(30, Blocks.NETHERRACK));
        layers.add(new FlatLayerInfo(1, Blocks.WARPED_NYLIUM));

        return createGenerator(layers, Biomes.DEEP_DARK);
    }

    private static FlatLevelSource createEndGenerator() {
        List<FlatLayerInfo> layers = new ArrayList<>();

        layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));

        layers.add(new FlatLayerInfo(59, Blocks.STONE));
        layers.add(new FlatLayerInfo(2, Blocks.DIRT));
        layers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));

        return createGenerator(layers, Biomes.DEEP_DARK);
    }

    private static FlatLevelSource createGenerator(List<FlatLayerInfo> layers, ResourceKey<Biome> biome) {
        Optional<HolderSet<StructureSet>> optional = Optional.of(HolderSet.direct());
        Registry<Biome> biomeRegistry = Janerator.getRegistry(Registry.BIOME_REGISTRY);

        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(optional, biomeRegistry).withLayers(layers, optional);

        settings.setBiome(biomeRegistry.getHolderOrThrow(biome));
        return new FlatLevelSource(Janerator.getRegistry(Registry.STRUCTURE_SET_REGISTRY), settings);
    }
}
