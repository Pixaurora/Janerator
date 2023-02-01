package dev.pixirora.janerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MultiGenerator extends ChunkGenerator {
    private GeneratorFinder generators;

    public MultiGenerator(BiomeSource biomeSource, GeneratorFinder generators) {
        super(biomeSource);
        this.generators = generators;
    }

	@Override
	public Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}


	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        for (GeneratorHolder holder : this.generators.getAll()) {
            holder.generator.buildSurface(
                region,
                structureManager,
                randomState,
                holder.getWrappedAccess(chunk)
            );
        }
	}

	@Override
	public int getSpawnHeight(LevelHeightAccessor world) {
		return this.generators.getDefault().getSpawnHeight(world);
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(
		Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk
	) {
        CompletableFuture<ChunkAccess> placeholderFuture = new CompletableFuture<>();
        CompletableFuture<ChunkAccess> future = placeholderFuture;

        for(GeneratorHolder holder : this.generators.getAll()) {
            future = future.thenCompose(
                access -> holder.generator.fillFromNoise(
                    executor,
                    blender,
                    randomState,
                    structureManager,
                    holder.getWrappedAccess(chunk)
                )
            );
        }

        placeholderFuture.complete(chunk);

        return future.thenApply(access -> chunk);
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState) {
		return this.generators.getAt(x, z).getBaseHeight(x, z, heightmap, world, randomState);
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
		return this.generators.getAt(x, z).getBaseColumn(x, z, world, randomState);
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
    
    @Override
	public CompletableFuture<ChunkAccess> createBiomes(
		Executor executor, RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk
	) {
        return CompletableFuture.supplyAsync(
            () -> {
                chunk.fillBiomesFromNoise(
                    new WrappedBiomeResolver(
                        this.generators,
                        blender,
                        chunk,
                        structureManager,
                        randomState
                    ),
                    randomState.sampler()
                );

                return chunk;
            });
	}

	@Override
	public void applyCarvers(
		WorldGenRegion chunkRegion,
		long seed,
		RandomState randomState,
		BiomeManager biomeAccess,
		StructureManager structureManager,
		ChunkAccess chunk,
		GenerationStep.Carving generationStep
	) {
        for (GeneratorHolder holder : this.generators.getAll()) {
            holder.generator.applyCarvers(
                chunkRegion,
                seed,
                randomState,
                biomeAccess,
                structureManager,
                holder.getWrappedAccess(chunk),
                generationStep
            );
        }
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {
        this.generators.getDefault().spawnOriginalMobs(region);
	}

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager) {
        for (GeneratorHolder holder : this.generators.getAll()) {
            holder.generator.applyBiomeDecoration(
                world,
                holder.getWrappedAccess(chunk),
                structureManager
            );
        }
    }

    @Override
    public void createStructures(
		RegistryAccess registryManager,
		ChunkGeneratorStructureState chunkGeneratorStructureState,
		StructureManager structureManager,
		ChunkAccess chunk,
		StructureTemplateManager templateManager
	) {
        for (GeneratorHolder holder : this.generators.getAll()) {
            holder.generator.createStructures(
                registryManager,
                chunkGeneratorStructureState,
                structureManager,
                holder.getWrappedAccess(chunk),
                templateManager
            );
        }
    }

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getGenDepth() {
		return 384;
	}

	@Override
	public int getSeaLevel() {
		return -63;
	}
}
