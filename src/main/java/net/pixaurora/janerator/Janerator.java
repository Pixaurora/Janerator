package net.pixaurora.janerator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.pixaurora.janerator.config.Generators;
import net.pixaurora.janerator.config.JaneratorConfig;
import net.pixaurora.janerator.worldgen.MultiGenerator;

public class Janerator {
    public static final Logger LOGGER = LoggerFactory.getLogger("Janerator");

    public static ChunkGenerator getGeneratorAt(
        ResourceKey<Level> dimension,
        ChunkGenerator defaultGenerator,
        ChunkAccess chunk
    ) {
        if (chunk instanceof LevelChunk || chunk instanceof ImposterProtoChunk) {
            return defaultGenerator;
        }

        ChunkGenerator modifiedGenerator = JaneratorConfig.getAlternateGenerators().get(dimension);
        ChunkGenerator outlineGenerator = JaneratorConfig.getOutlineGenerators().get(dimension);

        return new MultiGenerator(modifiedGenerator.getBiomeSource(), defaultGenerator, modifiedGenerator, outlineGenerator, chunk);
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
