package dev.pixirora.janerator.worldgen;

import java.util.HashMap;
import java.util.Map;

import dev.pixirora.janerator.mixin.NoiseBasedChunkGeneratorAccessor;
import dev.pixirora.janerator.mixin.NoiseChunkAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public class WrappedBiomeResolver implements BiomeResolver {
    GeneratorFinder generators;
    Map<ChunkGenerator, BiomeResolver> biomeResolvers;
    Map<ChunkGenerator, Sampler> samplers;

    public WrappedBiomeResolver(
        GeneratorFinder generators,
        Blender blender,
        ChunkAccess chunk,
        StructureManager structureManager,
        RandomState randomState
    ) {
        this.generators = generators;
        this.biomeResolvers = new HashMap<>();
        this.samplers = new HashMap<>();

        for (GeneratorHolder holder : generators.getAll()) {
            ChunkGenerator generator = holder.generator;

            this.biomeResolvers.put(generator, getBiomeResolverForGenerator(generator, blender, chunk));
            if (generator instanceof NoiseBasedChunkGenerator) {
                samplers.put(
                    generator,
                    getSampler(
                        (NoiseBasedChunkGeneratorAccessor) generator,
                        chunk,
                        structureManager,
                        blender,
                        randomState
                    )
                );
            }
        }
    }

    public Sampler getSampler(
        NoiseBasedChunkGeneratorAccessor generator,
        ChunkAccess chunk,
        StructureManager structureManager,
        Blender blender,
        RandomState randomState
    ) {
        NoiseChunk noiseChunk = chunk.getOrCreateNoiseChunk(
            chunkAccess -> generator.invokeCreateNoiseChunk(
                chunkAccess,
                structureManager,
                blender,
                randomState
            )
        );
    
        return ((NoiseChunkAccessor) noiseChunk).invokeCachedClimateSampler(
            randomState.router(),
            ((NoiseGeneratorSettings)generator.getSettings().value()).spawnTarget()
        );
    }

    public static BiomeResolver getBiomeResolverForGenerator(ChunkGenerator generator, Blender blender, ChunkAccess chunk) {
        if (generator instanceof NoiseBasedChunkGenerator) {
            return BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(generator.getBiomeSource()), chunk);
        }

        return generator.getBiomeSource();
    }

    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        ChunkGenerator generator = this.generators.getAtForBiomes(x, z);

        return biomeResolvers.get(
            generator
        ).getNoiseBiome(
            x, y, z, this.samplers.getOrDefault(generator, sampler)
        );
    }
}
