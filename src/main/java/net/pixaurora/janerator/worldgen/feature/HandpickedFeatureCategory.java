package net.pixaurora.janerator.worldgen.feature;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.features.PileFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.pixaurora.janerator.config.EnumCodec;

/**
 * Some feature categories that are aimed at making feature filter configuration
 * more concise.
 *
 * The categories are mainly by effect rather than biome or any other factor.
 */
public enum HandpickedFeatureCategory {
    /**
     * Large surface plants that take up a lot of space and might be preferred for
     * being filtered out.
     *
     * This also includes giant mushrooms, and the nether equivalent of trees.
     */
    ALL_TREES(List.of(CaveFeatures.ROOTED_AZALEA_TREE, TreeFeatures.ACACIA, TreeFeatures.AZALEA_TREE, TreeFeatures.BIRCH,
            TreeFeatures.BIRCH_BEES_0002, TreeFeatures.BIRCH_BEES_002, TreeFeatures.BIRCH_BEES_005, TreeFeatures.CHERRY,
            TreeFeatures.CHERRY_BEES_005, TreeFeatures.CRIMSON_FUNGUS, TreeFeatures.CRIMSON_FUNGUS_PLANTED, TreeFeatures.DARK_OAK,
            TreeFeatures.FANCY_OAK, TreeFeatures.FANCY_OAK_BEES, TreeFeatures.FANCY_OAK_BEES_0002, TreeFeatures.FANCY_OAK_BEES_002,
            TreeFeatures.FANCY_OAK_BEES_005, TreeFeatures.HUGE_BROWN_MUSHROOM, TreeFeatures.HUGE_RED_MUSHROOM, TreeFeatures.JUNGLE_BUSH,
            TreeFeatures.JUNGLE_TREE, TreeFeatures.JUNGLE_TREE_NO_VINE, TreeFeatures.MANGROVE, TreeFeatures.MEGA_JUNGLE_TREE,
            TreeFeatures.MEGA_PINE, TreeFeatures.MEGA_SPRUCE, TreeFeatures.OAK, TreeFeatures.OAK_BEES_0002, TreeFeatures.OAK_BEES_002,
            TreeFeatures.OAK_BEES_005, TreeFeatures.PINE, TreeFeatures.SPRUCE, TreeFeatures.SUPER_BIRCH_BEES,
            TreeFeatures.SUPER_BIRCH_BEES_0002, TreeFeatures.SWAMP_OAK, TreeFeatures.TALL_MANGROVE, TreeFeatures.WARPED_FUNGUS,
            TreeFeatures.WARPED_FUNGUS_PLANTED, VegetationFeatures.BAMBOO_VEGETATION, VegetationFeatures.BIRCH_TALL,
            VegetationFeatures.DARK_FOREST_VEGETATION, VegetationFeatures.MANGROVE_VEGETATION, VegetationFeatures.MEADOW_TREES,
            VegetationFeatures.MUSHROOM_ISLAND_VEGETATION, VegetationFeatures.TREES_BIRCH_AND_OAK, VegetationFeatures.TREES_FLOWER_FOREST,
            VegetationFeatures.TREES_GROVE, VegetationFeatures.TREES_JUNGLE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA,
            VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA, VegetationFeatures.TREES_PLAINS, VegetationFeatures.TREES_SAVANNA,
            VegetationFeatures.TREES_SPARSE_JUNGLE, VegetationFeatures.TREES_TAIGA, VegetationFeatures.TREES_WATER,
            VegetationFeatures.TREES_WINDSWEPT_HILLS)),

    /**
     * Small surface plants that don't take up a lot of space, don't replace the
     * space they're anchored to, and rest close to the ground or the wall.
     *
     * Notable examples include pumpkin patches, flowers, and vines.
     */
    SCATTERED_PLANTS(List.of(CaveFeatures.CAVE_VINE, CaveFeatures.CAVE_VINE_IN_MOSS, CaveFeatures.DRIPLEAF, CaveFeatures.GLOW_LICHEN,
            CaveFeatures.MOSS_VEGETATION, CaveFeatures.SPORE_BLOSSOM, PileFeatures.PILE_MELON, PileFeatures.PILE_PUMPKIN,
            VegetationFeatures.BAMBOO_NO_PODZOL, VegetationFeatures.FLOWER_CHERRY, VegetationFeatures.FLOWER_DEFAULT,
            VegetationFeatures.FLOWER_FLOWER_FOREST, VegetationFeatures.FLOWER_MEADOW, VegetationFeatures.FLOWER_PLAIN,
            VegetationFeatures.FLOWER_SWAMP, VegetationFeatures.FOREST_FLOWERS, VegetationFeatures.PATCH_BERRY_BUSH,
            VegetationFeatures.PATCH_BROWN_MUSHROOM, VegetationFeatures.PATCH_CACTUS, VegetationFeatures.PATCH_DEAD_BUSH,
            VegetationFeatures.PATCH_GRASS, VegetationFeatures.PATCH_GRASS_JUNGLE, VegetationFeatures.PATCH_LARGE_FERN,
            VegetationFeatures.PATCH_MELON, VegetationFeatures.PATCH_PUMPKIN, VegetationFeatures.PATCH_RED_MUSHROOM,
            VegetationFeatures.PATCH_SUGAR_CANE, VegetationFeatures.PATCH_SUNFLOWER, VegetationFeatures.PATCH_TAIGA_GRASS,
            VegetationFeatures.PATCH_TALL_GRASS, VegetationFeatures.PATCH_WATERLILY, VegetationFeatures.SINGLE_PIECE_OF_GRASS,
            VegetationFeatures.VINES)),

    /**
     * Features that replace the ground below them, turning it into sand, clay,
     * podzol, and more.
     *
     * It may also replace the ceiling, but with the general rule that this feature
     * replaces blocks more than it adds them.
     */
    REPLACES_FLOOR(List.of(CaveFeatures.DRIPSTONE_CLUSTER, CaveFeatures.CLAY_POOL_WITH_DRIPLEAVES, CaveFeatures.CLAY_WITH_DRIPLEAVES,
            CaveFeatures.LUSH_CAVES_CLAY, CaveFeatures.MOSS_PATCH, CaveFeatures.MOSS_PATCH_BONEMEAL, CaveFeatures.MOSS_PATCH_CEILING,
            CaveFeatures.SCULK_PATCH_ANCIENT_CITY, CaveFeatures.SCULK_PATCH_DEEP_DARK, CaveFeatures.SCULK_VEIN, MiscOverworldFeatures.DISK_CLAY,
            MiscOverworldFeatures.DISK_GRASS, MiscOverworldFeatures.DISK_GRAVEL, MiscOverworldFeatures.DISK_SAND,
            MiscOverworldFeatures.FOREST_ROCK, VegetationFeatures.BAMBOO_SOME_PODZOL)),

    /**
     * Some subsurface water plants and decoration, similar to the scattered plants.
     * The similarity lies in that they all rest close to or are at the bottom of
     * the water.
     */
    UNDERWATER_DECORATION(List.of(CaveFeatures.UNDERWATER_MAGMA, AquaticFeatures.KELP, AquaticFeatures.SEA_PICKLE, AquaticFeatures.SEAGRASS_MID,
            AquaticFeatures.SEAGRASS_SHORT, AquaticFeatures.SEAGRASS_SIMPLE, AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT,
            AquaticFeatures.SEAGRASS_TALL, AquaticFeatures.WARM_OCEAN_VEGETATION));

    public static final Codec<HandpickedFeatureCategory> CODEC = new EnumCodec<HandpickedFeatureCategory>("Hardcoded Feature Categories",
            values());

    private final List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures;

    HandpickedFeatureCategory(List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures) {
        this.includedFeatures = includedFeatures;
    }

    public List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures() {
        return this.includedFeatures;
    }
}
