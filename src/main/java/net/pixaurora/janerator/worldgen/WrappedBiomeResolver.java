package net.pixaurora.janerator.worldgen;

import java.util.HashMap;
import java.util.Map;

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
import net.pixaurora.janerator.graph.Coordinate;
import net.pixaurora.janerator.mixin.NoiseBasedChunkGeneratorAccessor;
import net.pixaurora.janerator.mixin.NoiseChunkAccessor;

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

        for (PlacementSelection selection : generators.getAllSelections()) {
            ChunkGenerator generator = selection.getUsedGenerator();

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
            chunkAccess -> generator.janerator$invokeCreateNoiseChunk(
                chunkAccess,
                structureManager,
                blender,
                randomState
            )
        );
    
        return ((NoiseChunkAccessor) noiseChunk).janerator$invokeCachedClimateSampler(
            randomState.router(),
            ((NoiseGeneratorSettings)generator.janerator$getSettings().value()).spawnTarget()
        );
    }

    public static BiomeResolver getBiomeResolverForGenerator(ChunkGenerator generator, Blender blender, ChunkAccess chunk) {
        if (generator instanceof NoiseBasedChunkGenerator) {
            return BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(generator.getBiomeSource()), chunk);
        }

        return generator.getBiomeSource();
    }

    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        ChunkGenerator generator = this.generators.getAtForBiomes(new Coordinate(x, z, 4));

        return biomeResolvers.get(
            generator
        ).getNoiseBiome(
            x, y, z, this.samplers.getOrDefault(generator, sampler)
        );
    }
}
