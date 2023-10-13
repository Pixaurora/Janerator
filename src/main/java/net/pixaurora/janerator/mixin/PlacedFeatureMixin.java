package net.pixaurora.janerator.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.pixaurora.janerator.worldgen.generator.MultiGenOrganizer;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {
    @Shadow
    private Holder<ConfiguredFeature<?, ?>> feature;

    @ModifyVariable(
        method = "placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
        at = @At(value = "LOAD", ordinal = 1)
    )
    public Stream<BlockPos> janerator$filterFeatureStarts(Stream<BlockPos> featureStarts, PlacementContext context) {
        ChunkGenerator generator = context.generator();

        if (generator.janerator$isDoingMultigen()) {
            // Skip filtering for FlatLevelSource as they filter features at the block level instead
            boolean alwaysSkip = generator instanceof FlatLevelSource;

            if (!alwaysSkip && generator.janerator$getParent().getSelectedFeatures().filtersOut(this.feature)) {
                MultiGenOrganizer organizer = generator.janerator$getOrganizer();

                featureStarts = featureStarts.filter(
                    originPos -> organizer.getGenerators(new ChunkPos(originPos)).getAt(originPos) == generator
                );
            }
        }

        return featureStarts;
    }
}
