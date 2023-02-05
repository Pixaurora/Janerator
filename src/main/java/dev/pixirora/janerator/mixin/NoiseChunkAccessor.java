package dev.pixirora.janerator.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;

@Mixin(NoiseChunk.class)
public interface NoiseChunkAccessor {
    @Invoker("cachedClimateSampler")
    Climate.Sampler janerator$invokeCachedClimateSampler(NoiseRouter noiseRouter, List<ParameterPoint> list);
}
