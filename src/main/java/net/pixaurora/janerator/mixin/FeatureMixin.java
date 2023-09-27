package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

@Mixin(Feature.class)
public class FeatureMixin<FC extends FeatureConfiguration> {
    @Inject(
        method = "place(Lnet/minecraft/world/level/levelgen/feature/configurations/FeatureConfiguration;Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z",
        at = @At(value="HEAD"),
        cancellable = true
    )
    public void janerator$decideIfPlace(
        FC config,
        WorldGenLevel world,
        ChunkGenerator generator,
        RandomSource random,
        BlockPos originPos,
        CallbackInfoReturnable<Boolean> cir
    ) {
		if (generator instanceof FlatLevelSource || generator.janerator$notMultiGenerating()) {
            return;
        }

        if (generator.janerator$getParent().getGenerators(new ChunkPos(originPos)).getAt(originPos) != generator) {
            cir.setReturnValue(false);
        }
	}

}
