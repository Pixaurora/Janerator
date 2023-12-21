package net.pixaurora.janerator.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
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
            TreeFeatures.WARPED_FUNGUS_PLANTED, VegetationFeatures.BAMBOO_NO_PODZOL, VegetationFeatures.BAMBOO_SOME_PODZOL,
            VegetationFeatures.BAMBOO_VEGETATION, VegetationFeatures.BIRCH_TALL, VegetationFeatures.DARK_FOREST_VEGETATION,
            VegetationFeatures.MANGROVE_VEGETATION, VegetationFeatures.MEADOW_TREES, VegetationFeatures.MUSHROOM_ISLAND_VEGETATION,
            VegetationFeatures.TREES_BIRCH_AND_OAK, VegetationFeatures.TREES_GROVE, VegetationFeatures.TREES_JUNGLE,
            VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA, VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA, VegetationFeatures.TREES_PLAINS,
            VegetationFeatures.TREES_SAVANNA, VegetationFeatures.TREES_SPARSE_JUNGLE, VegetationFeatures.TREES_TAIGA,
            VegetationFeatures.TREES_WATER, VegetationFeatures.TREES_WINDSWEPT_HILLS)),

    /**
     * Small surface plants that don't take up a lot of place and rest close to the
     * ground.
     *
     * Notable examples include pumpkin patches, flowers, and a few others.
     */
    SCATTERED_PLANTS(List.of(CaveFeatures.CAVE_VINE, CaveFeatures.CAVE_VINE_IN_MOSS, CaveFeatures.DRIPLEAF, CaveFeatures.MOSS_VEGETATION,
            CaveFeatures.SPORE_BLOSSOM)),

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
