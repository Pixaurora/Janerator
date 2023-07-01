package dev.pixirora.janerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.pixirora.janerator.config.Generators;
import dev.pixirora.janerator.config.OverrideLogic;
import dev.pixirora.janerator.worldgen.JaneratorGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin implements JaneratorGenerator {
    private ResourceKey<Level> janerator$dimension;

    public void janerator$setDimension(ResourceKey<Level> dimension) {
        this.janerator$dimension = dimension;
    }

    @Inject(
        method = "getBaseHeight(IILnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)I",
        at = @At("HEAD"),
        cancellable = true
    )
    private void janerator$overrideBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState, CallbackInfoReturnable<Integer> cir) {
        if (OverrideLogic.INSTANCE.shouldOverride(x, z)) {
            cir.setReturnValue(
                Generators.get(this.janerator$dimension)
                    .getBaseHeight(x, z, heightmap, world, randomState)
            );
        }
    }
}
