package net.pixaurora.janerator.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.pixaurora.janerator.config.EnumCodec;

public enum HandpickedFeatureCategory {
    ALL_TREES(List.of(TreeFeatures.ACACIA, TreeFeatures.AZALEA_TREE, TreeFeatures.BIRCH, TreeFeatures.BIRCH_BEES_0002,
            TreeFeatures.BIRCH_BEES_002, TreeFeatures.BIRCH_BEES_005, TreeFeatures.CHERRY, TreeFeatures.CHERRY_BEES_005,
            TreeFeatures.CRIMSON_FUNGUS, TreeFeatures.CRIMSON_FUNGUS_PLANTED, TreeFeatures.DARK_OAK, TreeFeatures.FANCY_OAK,
            TreeFeatures.FANCY_OAK_BEES, TreeFeatures.FANCY_OAK_BEES_0002, TreeFeatures.FANCY_OAK_BEES_002, TreeFeatures.FANCY_OAK_BEES_005,
            TreeFeatures.HUGE_BROWN_MUSHROOM, TreeFeatures.HUGE_RED_MUSHROOM, TreeFeatures.JUNGLE_BUSH, TreeFeatures.JUNGLE_TREE,
            TreeFeatures.JUNGLE_TREE_NO_VINE, TreeFeatures.MANGROVE, TreeFeatures.MEGA_JUNGLE_TREE, TreeFeatures.MEGA_PINE,
            TreeFeatures.MEGA_SPRUCE, TreeFeatures.OAK, TreeFeatures.OAK_BEES_0002, TreeFeatures.OAK_BEES_002, TreeFeatures.OAK_BEES_005,
            TreeFeatures.PINE, TreeFeatures.SPRUCE, TreeFeatures.SUPER_BIRCH_BEES, TreeFeatures.SUPER_BIRCH_BEES_0002, TreeFeatures.SWAMP_OAK,
            TreeFeatures.TALL_MANGROVE, TreeFeatures.WARPED_FUNGUS, TreeFeatures.WARPED_FUNGUS_PLANTED, VegetationFeatures.BAMBOO_NO_PODZOL,
            VegetationFeatures.BAMBOO_SOME_PODZOL, VegetationFeatures.BAMBOO_VEGETATION, VegetationFeatures.BIRCH_TALL,
            VegetationFeatures.DARK_FOREST_VEGETATION, VegetationFeatures.MANGROVE_VEGETATION, VegetationFeatures.MEADOW_TREES,
            VegetationFeatures.MUSHROOM_ISLAND_VEGETATION, VegetationFeatures.TREES_BIRCH_AND_OAK, VegetationFeatures.TREES_GROVE,
            VegetationFeatures.TREES_JUNGLE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA, VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA,
            VegetationFeatures.TREES_PLAINS, VegetationFeatures.TREES_SAVANNA, VegetationFeatures.TREES_SPARSE_JUNGLE,
            VegetationFeatures.TREES_TAIGA, VegetationFeatures.TREES_WATER, VegetationFeatures.TREES_WINDSWEPT_HILLS));

    public static final Codec<HandpickedFeatureCategory> CODEC = new EnumCodec<HandpickedFeatureCategory>(
        "Hardcoded Feature Categories",
        values()
    );

    private final List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures;

    HandpickedFeatureCategory(List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures) {
        this.includedFeatures = includedFeatures;
    }

    public List<ResourceKey<ConfiguredFeature<?, ?>>> includedFeatures() {
        return this.includedFeatures;
    }
}
