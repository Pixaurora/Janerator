package net.pixaurora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.worldgen.JaneratorGenerator;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin implements JaneratorGenerator {
    @Inject(
        method = "getBaseHeight(IILnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)I",
        at = @At("HEAD"),
        cancellable = true
    )
    private void janerator$overrideBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState, CallbackInfoReturnable<Integer> cir) {
        NoiseBasedChunkGenerator asGenerator = (NoiseBasedChunkGenerator)(Object) this;

        if (! asGenerator.janerator$isDoingMultigen()) {
            return;
        }

        ChunkGenerator properGenerator = asGenerator.janerator$getOrganizer().getGenerator(new Coordinate(x, z));

        if (asGenerator != properGenerator) {
            cir.setReturnValue(
                properGenerator.getBaseHeight(x, z, heightmap, world, randomState)
            );
        }
    }
}
