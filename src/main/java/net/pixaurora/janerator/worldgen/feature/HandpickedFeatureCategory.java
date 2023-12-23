package net.pixaurora.janerator.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.features.OreFeatures;
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
    ALL_TREES(List.of(CaveFeatures.ROOTED_AZALEA_TREE, EndFeatures.CHORUS_PLANT, TreeFeatures.ACACIA, TreeFeatures.AZALEA_TREE,
            TreeFeatures.BIRCH, TreeFeatures.BIRCH_BEES_0002, TreeFeatures.BIRCH_BEES_002, TreeFeatures.BIRCH_BEES_005, TreeFeatures.CHERRY,
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
     * Notable examples include pumpkin patches, flowers, vines, and plants that
     * grow underwater.
     */
    SCATTERED_PLANTS(List.of(AquaticFeatures.KELP, AquaticFeatures.SEA_PICKLE, AquaticFeatures.SEAGRASS_MID, AquaticFeatures.SEAGRASS_SHORT,
            AquaticFeatures.SEAGRASS_SIMPLE, AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT, AquaticFeatures.SEAGRASS_TALL,
            AquaticFeatures.WARM_OCEAN_VEGETATION, CaveFeatures.CAVE_VINE, CaveFeatures.CAVE_VINE_IN_MOSS, CaveFeatures.DRIPLEAF,
            CaveFeatures.GLOW_LICHEN, CaveFeatures.MOSS_VEGETATION, CaveFeatures.SPORE_BLOSSOM, NetherFeatures.CRIMSON_FOREST_VEGETATION,
            NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, NetherFeatures.NETHER_SPROUTS, NetherFeatures.NETHER_SPROUTS_BONEMEAL,
            NetherFeatures.PATCH_CRIMSON_ROOTS, NetherFeatures.TWISTING_VINES, NetherFeatures.TWISTING_VINES_BONEMEAL,
            NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, NetherFeatures.WARPED_FOREST_VEGETION, NetherFeatures.WEEPING_VINES,
            PileFeatures.PILE_MELON, PileFeatures.PILE_PUMPKIN, VegetationFeatures.BAMBOO_NO_PODZOL, VegetationFeatures.FLOWER_CHERRY,
            VegetationFeatures.FLOWER_DEFAULT, VegetationFeatures.FLOWER_FLOWER_FOREST, VegetationFeatures.FLOWER_MEADOW,
            VegetationFeatures.FLOWER_PLAIN, VegetationFeatures.FLOWER_SWAMP, VegetationFeatures.FOREST_FLOWERS,
            VegetationFeatures.PATCH_BERRY_BUSH, VegetationFeatures.PATCH_BROWN_MUSHROOM, VegetationFeatures.PATCH_CACTUS,
            VegetationFeatures.PATCH_DEAD_BUSH, VegetationFeatures.PATCH_GRASS, VegetationFeatures.PATCH_GRASS_JUNGLE,
            VegetationFeatures.PATCH_LARGE_FERN, VegetationFeatures.PATCH_MELON, VegetationFeatures.PATCH_PUMPKIN,
            VegetationFeatures.PATCH_RED_MUSHROOM, VegetationFeatures.PATCH_SUGAR_CANE, VegetationFeatures.PATCH_SUNFLOWER,
            VegetationFeatures.PATCH_TAIGA_GRASS, VegetationFeatures.PATCH_TALL_GRASS, VegetationFeatures.PATCH_WATERLILY,
            VegetationFeatures.SINGLE_PIECE_OF_GRASS, VegetationFeatures.VINES)),

    /**
     * Features that replace the existing ground, turning it into sand, clay,
     * podzol, ores, and more.
     *
     * It may also replace the ceiling, but with the general rule that this feature
     * replaces blocks more than it adds new ones.
     *
     * Because these features may replace blocks in your flat presets, it makes
     * sense to filter them.
     */
    REPLACES_FLOOR(List.of(CaveFeatures.CLAY_POOL_WITH_DRIPLEAVES, CaveFeatures.CLAY_WITH_DRIPLEAVES, CaveFeatures.DRIPSTONE_CLUSTER,
            CaveFeatures.LUSH_CAVES_CLAY, CaveFeatures.MOSS_PATCH, CaveFeatures.MOSS_PATCH_BONEMEAL, CaveFeatures.MOSS_PATCH_CEILING,
            CaveFeatures.POINTED_DRIPSTONE, CaveFeatures.SCULK_PATCH_ANCIENT_CITY, CaveFeatures.SCULK_PATCH_DEEP_DARK, CaveFeatures.SCULK_VEIN,
            CaveFeatures.UNDERWATER_MAGMA, MiscOverworldFeatures.DISK_CLAY, MiscOverworldFeatures.DISK_GRASS, MiscOverworldFeatures.DISK_GRAVEL,
            MiscOverworldFeatures.DISK_SAND, MiscOverworldFeatures.FOREST_ROCK, MiscOverworldFeatures.ICE_PATCH,
            MiscOverworldFeatures.SPRING_LAVA_FROZEN, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, MiscOverworldFeatures.SPRING_WATER,
            NetherFeatures.SPRING_LAVA_NETHER, NetherFeatures.SPRING_NETHER_CLOSED, NetherFeatures.SPRING_NETHER_OPEN,
            OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, OreFeatures.ORE_ANCIENT_DEBRIS_SMALL, OreFeatures.ORE_ANDESITE, OreFeatures.ORE_BLACKSTONE,
            OreFeatures.ORE_CLAY, OreFeatures.ORE_COAL, OreFeatures.ORE_COAL_BURIED, OreFeatures.ORE_COPPER_LARGE,
            OreFeatures.ORE_COPPPER_SMALL, OreFeatures.ORE_DIAMOND_BURIED, OreFeatures.ORE_DIAMOND_LARGE, OreFeatures.ORE_DIAMOND_MEDIUM,
            OreFeatures.ORE_DIAMOND_SMALL, OreFeatures.ORE_DIORITE, OreFeatures.ORE_DIRT, OreFeatures.ORE_EMERALD, OreFeatures.ORE_GOLD,
            OreFeatures.ORE_GOLD_BURIED, OreFeatures.ORE_GRANITE, OreFeatures.ORE_GRAVEL, OreFeatures.ORE_GRAVEL_NETHER,
            OreFeatures.ORE_INFESTED, OreFeatures.ORE_IRON, OreFeatures.ORE_IRON_SMALL, OreFeatures.ORE_LAPIS, OreFeatures.ORE_LAPIS_BURIED,
            OreFeatures.ORE_MAGMA, OreFeatures.ORE_NETHER_GOLD, OreFeatures.ORE_QUARTZ, OreFeatures.ORE_REDSTONE, OreFeatures.ORE_SOUL_SAND,
            OreFeatures.ORE_TUFF, VegetationFeatures.BAMBOO_SOME_PODZOL)),

    /**
     * Rare features that only show up very occasionally.
     *
     * There's not a ton of reason to filter this category, as these tend to be more
     * akin to structures, and are very neat when they pop up.
     */
    RARITIES(List.of(CaveFeatures.FOSSIL_COAL, CaveFeatures.FOSSIL_DIAMONDS, CaveFeatures.MONSTER_ROOM, EndFeatures.END_GATEWAY_RETURN,
            EndFeatures.END_GATEWAY_DELAYED, MiscOverworldFeatures.BONUS_CHEST, MiscOverworldFeatures.DESERT_WELL,
            MiscOverworldFeatures.VOID_START_PLATFORM)),

    /**
     * Larger features present only at the surface of the water, such as icebergs
     * and shipwrecks.
     */
    WATER_SURFACE(List.of(MiscOverworldFeatures.BLUE_ICE, MiscOverworldFeatures.ICEBERG_BLUE, MiscOverworldFeatures.ICEBERG_PACKED)),

    /**
     * This category is just for features that cover the ground, such as snow & ice,
     * and fire in the nether.
     *
     * Usually there's no reason to filter it unless you're making a preset with the
     * snow biome, but don't want snow/ice there. Although note that unless you use
     * a data pack to override it, snowfall will still cover the ground after it's
     * generated.
     */
    COVERS_GROUND(List.of(MiscOverworldFeatures.FREEZE_TOP_LAYER, NetherFeatures.PATCH_FIRE, NetherFeatures.PATCH_SOUL_FIRE,
            PileFeatures.PILE_HAY, PileFeatures.PILE_ICE, PileFeatures.PILE_SNOW)),

    /**
     * Large natural structures that feel more like they're part of the terrain
     * rather than a jigsaw piece being placed in the world.
     */
    LARGE_NATURAL_STRUCTURE(List.of(CaveFeatures.AMETHYST_GEODE, CaveFeatures.LARGE_DRIPSTONE, EndFeatures.END_SPIKE, EndFeatures.END_ISLAND,
            MiscOverworldFeatures.ICE_SPIKE, MiscOverworldFeatures.LAKE_LAVA, NetherFeatures.BASALT_BLOBS, NetherFeatures.BASALT_PILLAR,
            NetherFeatures.BLACKSTONE_BLOBS, NetherFeatures.DELTA, NetherFeatures.GLOWSTONE_EXTRA, NetherFeatures.LARGE_BASALT_COLUMNS,
            NetherFeatures.SMALL_BASALT_COLUMNS));

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
