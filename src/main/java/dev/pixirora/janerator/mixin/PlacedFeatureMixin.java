package dev.pixirora.janerator.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.pixirora.janerator.Janerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {
    @Shadow
    private Holder<ConfiguredFeature<?, ?>> feature;

    private static List<ConfiguredFeature<?, ?>> janerator$checkedFeatures;

    private static synchronized void janerator$makeRemovedFeatures() {
        Registry<ConfiguredFeature<?, ?>> featureRegistry = Janerator.getRegistry(Registries.CONFIGURED_FEATURE);
        List<ResourceKey<ConfiguredFeature<?, ?>>> keys = List.of(
            CaveFeatures.SCULK_PATCH_DEEP_DARK,
            CaveFeatures.SCULK_PATCH_ANCIENT_CITY,
            CaveFeatures.SCULK_VEIN,
            EndFeatures.CHORUS_PLANT,
            MiscOverworldFeatures.LAKE_LAVA,
            MiscOverworldFeatures.DISK_CLAY,
            MiscOverworldFeatures.DISK_GRAVEL,
            MiscOverworldFeatures.DISK_SAND,
            MiscOverworldFeatures.SPRING_LAVA_FROZEN,
            NetherFeatures.DELTA,
            NetherFeatures.SMALL_BASALT_COLUMNS,
            NetherFeatures.LARGE_BASALT_COLUMNS,
            NetherFeatures.BASALT_BLOBS,
            NetherFeatures.BLACKSTONE_BLOBS,
            NetherFeatures.GLOWSTONE_EXTRA,
            NetherFeatures.CRIMSON_FOREST_VEGETATION,
            NetherFeatures.WARPED_FOREST_VEGETION, // Yes, this is typoed in the code
            NetherFeatures.BASALT_PILLAR,
            NetherFeatures.SPRING_LAVA_NETHER,
            NetherFeatures.SPRING_NETHER_CLOSED,
            NetherFeatures.SPRING_NETHER_OPEN,
            NetherFeatures.PATCH_FIRE,
            NetherFeatures.PATCH_SOUL_FIRE,
            VegetationFeatures.DARK_FOREST_VEGETATION,
            VegetationFeatures.TREES_FLOWER_FOREST,
            VegetationFeatures.MEADOW_TREES,
            VegetationFeatures.TREES_TAIGA,
            VegetationFeatures.TREES_GROVE,
            VegetationFeatures.TREES_SAVANNA,
            VegetationFeatures.BIRCH_TALL,
            VegetationFeatures.TREES_WINDSWEPT_HILLS,
            VegetationFeatures.TREES_WATER,
            VegetationFeatures.TREES_BIRCH_AND_OAK,
            VegetationFeatures.TREES_PLAINS,
            VegetationFeatures.TREES_SPARSE_JUNGLE,
            VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA,
            VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA,
            VegetationFeatures.TREES_JUNGLE,
            VegetationFeatures.BAMBOO_VEGETATION,
            VegetationFeatures.MUSHROOM_ISLAND_VEGETATION,
            VegetationFeatures.MANGROVE_VEGETATION
        );

        janerator$checkedFeatures = new ArrayList<>();
        for (ResourceKey<ConfiguredFeature<?, ?>> key: keys) {
            janerator$checkedFeatures.add(
                featureRegistry.getHolderOrThrow(key).value()
            );
        }
    }

    private static List<ConfiguredFeature<?, ?>> janerator$getRemovedFeatures() {
        if (Objects.isNull(janerator$checkedFeatures)) {
            janerator$makeRemovedFeatures();
        }

        return janerator$checkedFeatures;
    }

    @Redirect(
        method = "placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"
        )
    )
    public void janerator$decideIfPlace(Stream<BlockPos> posStream, Consumer<BlockPos> function) {
        if (janerator$getRemovedFeatures().contains(this.feature.value())) {
            posStream = posStream.filter(pos -> !Janerator.shouldOverride(pos));
        }

        posStream.forEach(function);
    }
}
