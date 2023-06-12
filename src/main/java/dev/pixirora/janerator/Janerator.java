package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import dev.pixirora.janerator.worldgen.GeneratorFinder;
import dev.pixirora.janerator.worldgen.MultiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class Janerator {
    public static final Logger LOGGER = LoggerFactory.getLogger("Janerator");

    private static Map<ResourceKey<Level>, ChunkGenerator> generators = Maps.newHashMapWithExpectedSize(3);

    private static RegistryCache cache;

    private static int squareSpiralImage[][] = new int[][]{
        new int[]{0,0,0,0,0,0,0,0},
        new int[]{1,1,1,1,1,1,1,0},
        new int[]{1,0,0,0,0,0,1,0},
        new int[]{1,0,1,1,1,0,1,0},
        new int[]{1,0,1,0,1,0,1,0},
        new int[]{1,0,1,0,0,0,1,0},
        new int[]{1,0,1,1,1,1,1,0},
        new int[]{1,0,0,0,0,0,0,0}
    };

    public static int normalize(int value, int divisor) {
        return value - divisor * Math.floorDiv(value, divisor);
    }

    public static int toListCoordinate(int x, int z, int divisor) {
        return divisor * normalize(x, divisor) + normalize(z, divisor);
    }

    private static int sumOfSquares(int value) {
        return value * (value + 1) * (2 * value + 1) / 6;
    }

    private static int inverseSumOfSquares(int value) {
        assert (value < 0 || 31000000 < value);

        int answer = 0;
        while (sumOfSquares(answer) <= value) {
            answer ++;
        }

        return answer - 1;
    }

    private static double slowValueGrowth(double value) {
        double absOfValue = Math.abs(value);
        if (absOfValue <= 4.0) {
            return value;
        }

        return (inverseSumOfSquares((int) absOfValue - 4) + 4) * Math.signum(value);
    }

    private static double makeRepeatingForInfiniteSquareSpiral(double value, double otherValue) {
        double maxValue = Math.max(Math.abs(value), Math.abs(otherValue));
        double oneIfMaxValueIsNegative = value + otherValue < 0.0 ? 1.0 : 0.0;

        if (maxValue < 4.0 + oneIfMaxValueIsNegative) {
            return value;
        }

        double slope = (2.0 + maxValue % 2.0) / maxValue;

        return slope * value;
    }

    private static int positionInSquareSpiral(double x, double z) {
        return Janerator.squareSpiralImage[(int) x + 4][(int) z + 4];
    }

    private static int positionInInfiniteSquareSpiral(double x, double z) {
        return positionInSquareSpiral(makeRepeatingForInfiniteSquareSpiral(x, z), makeRepeatingForInfiniteSquareSpiral(z, x));
    }

    public static boolean shouldOverride(double x, double z) {
        return positionInInfiniteSquareSpiral(slowValueGrowth(x), slowValueGrowth(z)) == 0;
    }

    public static int toListCoordinate(int x, int z) {
        return toListCoordinate(x, z, 16);
    }

    public static boolean shouldOverride(ChunkPos chunkPos) {
        return shouldOverride(chunkPos.x*16, chunkPos.z*16);
    }

    public static boolean shouldOverride(BlockPos pos) {
        return shouldOverride(pos.getX(), pos.getZ());
    }

    public static void makeRegistryCache(MinecraftServer server) {
        Janerator.cache = new RegistryCache(server);
    }

    public static RegistryCache getRegistryCache() {
        return Janerator.cache;
    }

    public static synchronized ChunkGenerator getGenerator(ResourceKey<Level> dimension) {
        if (Janerator.generators.isEmpty()) {
            initializeGenerators();
        }

        ChunkGenerator generator = Janerator.generators.get(dimension);
        return generator != null ? generator : Janerator.generators.get(Level.OVERWORLD);
    }

    public static ChunkGenerator getGeneratorAt(
        ResourceKey<Level> dimension,
        ChunkGenerator defaultGenerator,
        ChunkAccess chunk
    ) {
        if (chunk instanceof LevelChunk || chunk instanceof ImposterProtoChunk) {
            return defaultGenerator;
        }

        ChunkGenerator modifiedGenerator = Janerator.getGenerator(dimension);

        GeneratorFinder generators = new GeneratorFinder(defaultGenerator, modifiedGenerator, chunk);

        if (generators.size() > 1) {
            return new MultiGenerator(modifiedGenerator.getBiomeSource(), generators);
        } else {
            return generators.getDefault();
        }
    }

    private static void initializeGenerators() {
        Janerator.generators.put(Level.OVERWORLD, createOverworldGenerator());
        Janerator.generators.put(Level.NETHER, createNetherGenerator());
        Janerator.generators.put(Level.END, createEndGenerator());
    }

    public static void cleanup() {
        Janerator.cache = null;
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
        List<Holder<PlacedFeature>> placedFeatures = List.of();
        Optional<HolderSet<StructureSet>> optional = Optional.of(HolderSet.direct());

        Holder<Biome> biomeHolder = cache.getRegistry(Registries.BIOME).getHolderOrThrow(biome);
        return new FlatLevelSource(new FlatLevelGeneratorSettings(optional, biomeHolder, placedFeatures)
                .withBiomeAndLayers(layers, optional, biomeHolder));
    }

    public static List<ResourceKey<ConfiguredFeature<?, ?>>> getFilteredFeatures() {
        return List.of(
            CaveFeatures.SCULK_PATCH_ANCIENT_CITY,
            CaveFeatures.SCULK_PATCH_DEEP_DARK,
            CaveFeatures.SCULK_VEIN,
            EndFeatures.CHORUS_PLANT,
            MiscOverworldFeatures.DISK_CLAY,
            MiscOverworldFeatures.DISK_GRAVEL,
            MiscOverworldFeatures.DISK_SAND,
            MiscOverworldFeatures.LAKE_LAVA,
            MiscOverworldFeatures.SPRING_LAVA_FROZEN,
            NetherFeatures.BASALT_BLOBS,
            NetherFeatures.BASALT_PILLAR,
            NetherFeatures.BLACKSTONE_BLOBS,
            NetherFeatures.CRIMSON_FOREST_VEGETATION,
            NetherFeatures.DELTA,
            NetherFeatures.GLOWSTONE_EXTRA,
            NetherFeatures.LARGE_BASALT_COLUMNS,
            NetherFeatures.PATCH_FIRE,
            NetherFeatures.PATCH_SOUL_FIRE,
            NetherFeatures.SMALL_BASALT_COLUMNS,
            NetherFeatures.SPRING_LAVA_NETHER,
            NetherFeatures.SPRING_NETHER_CLOSED,
            NetherFeatures.SPRING_NETHER_OPEN,
            NetherFeatures.WARPED_FOREST_VEGETION, // Yes, this is typoed in the code
            TreeFeatures.ACACIA,
            TreeFeatures.AZALEA_TREE,
            TreeFeatures.BIRCH,
            TreeFeatures.BIRCH_BEES_0002,
            TreeFeatures.BIRCH_BEES_002,
            TreeFeatures.BIRCH_BEES_005,
            TreeFeatures.CHERRY,
            TreeFeatures.CHERRY_BEES_005,
            TreeFeatures.CRIMSON_FUNGUS,
            TreeFeatures.CRIMSON_FUNGUS_PLANTED,
            TreeFeatures.DARK_OAK,
            TreeFeatures.FANCY_OAK,
            TreeFeatures.FANCY_OAK_BEES,
            TreeFeatures.FANCY_OAK_BEES_0002,
            TreeFeatures.FANCY_OAK_BEES_002,
            TreeFeatures.FANCY_OAK_BEES_005,
            TreeFeatures.HUGE_BROWN_MUSHROOM,
            TreeFeatures.HUGE_RED_MUSHROOM,
            TreeFeatures.JUNGLE_BUSH,
            TreeFeatures.JUNGLE_TREE,
            TreeFeatures.JUNGLE_TREE_NO_VINE,
            TreeFeatures.MANGROVE,
            TreeFeatures.MEGA_JUNGLE_TREE,
            TreeFeatures.MEGA_PINE,
            TreeFeatures.MEGA_SPRUCE,
            TreeFeatures.OAK,
            TreeFeatures.OAK_BEES_0002,
            TreeFeatures.OAK_BEES_002,
            TreeFeatures.OAK_BEES_005,
            TreeFeatures.PINE,
            TreeFeatures.SPRUCE,
            TreeFeatures.SUPER_BIRCH_BEES,
            TreeFeatures.SUPER_BIRCH_BEES_0002,
            TreeFeatures.SWAMP_OAK,
            TreeFeatures.TALL_MANGROVE,
            TreeFeatures.WARPED_FUNGUS,
            TreeFeatures.WARPED_FUNGUS_PLANTED,
            VegetationFeatures.BAMBOO_VEGETATION,
            VegetationFeatures.BIRCH_TALL,
            VegetationFeatures.DARK_FOREST_VEGETATION,
            VegetationFeatures.MANGROVE_VEGETATION,
            VegetationFeatures.MEADOW_TREES,
            VegetationFeatures.MUSHROOM_ISLAND_VEGETATION,
            VegetationFeatures.TREES_BIRCH_AND_OAK,
            VegetationFeatures.TREES_FLOWER_FOREST,
            VegetationFeatures.TREES_GROVE,
            VegetationFeatures.TREES_JUNGLE,
            VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA,
            VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA,
            VegetationFeatures.TREES_PLAINS,
            VegetationFeatures.TREES_SAVANNA,
            VegetationFeatures.TREES_SPARSE_JUNGLE,
            VegetationFeatures.TREES_TAIGA,
            VegetationFeatures.TREES_WATER,
            VegetationFeatures.TREES_WINDSWEPT_HILLS
        );
    }
}
