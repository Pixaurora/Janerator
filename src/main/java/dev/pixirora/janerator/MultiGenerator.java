package dev.pixirora.janerator;

import java.util.ArrayList;
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
    List<GeneratorHolder> generators;
    ChunkGenerator fallbackGenerator;

    public MultiGenerator(BiomeSource biomeSource, Map<ChunkGenerator, List<List<Integer>>> generatorMap, ChunkGenerator fallbackGenerator) {
        super(biomeSource);

        this.fallbackGenerator = fallbackGenerator;

        this.generators = new ArrayList<>();
        generatorMap.forEach(
            (generator, placements) -> {
                this.generators.add(new GeneratorHolder(generator, placements));
            }
        );        
    }

    private ChunkGenerator getGeneratorAt(int x, int z) {
        for (GeneratorHolder holder : generators) {
            if (holder.isWanted(x, z)) {
                return holder.generator;
            }
        }

        return fallbackGenerator;
    }

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return null;
	}


	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        for (GeneratorHolder holder : this.generators) {
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
		return fallbackGenerator.getSpawnHeight(world);
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(
		Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk
	) {
        CompletableFuture<ChunkAccess> placeHolderFuture = new CompletableFuture<>();
        CompletableFuture<ChunkAccess> future = placeHolderFuture;

        for(GeneratorHolder holder : generators) {
            future = future.thenCompose(
                (access) -> holder.generator.fillFromNoise(
                    executor,
                    blender,
                    randomState,
                    structureManager,
                    holder.getWrappedAccess(chunk)
                )
            );
        }

        future.thenCompose((access) -> CompletableFuture.completedFuture(chunk));
        placeHolderFuture.complete(chunk);

        return future;
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState) {
		return this.getGeneratorAt(x, z).getBaseHeight(x, z, heightmap, world, randomState);
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
		return this.getGeneratorAt(x, z).getBaseColumn(x, z, world, randomState);
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
    
    @Override
	public CompletableFuture<ChunkAccess> createBiomes(
		Executor executor, RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk
	) {
        CompletableFuture<ChunkAccess> placeholderFuture = new CompletableFuture<>();
        CompletableFuture<ChunkAccess> future = placeholderFuture;

        for(GeneratorHolder holder : generators) {
            future = future.thenCompose(
                (access) -> holder.generator.createBiomes(
                    executor,
                    randomState,
                    blender,
                    structureManager,
                    chunk
                )
            );
        }

        future.thenCompose((access) -> CompletableFuture.completedFuture(chunk));
        placeholderFuture.complete(chunk);

        return future;
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
        for (GeneratorHolder holder : this.generators) {
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
        this.fallbackGenerator.spawnOriginalMobs(region);
	}

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager) {
        for (GeneratorHolder holder : this.generators) {
            holder.generator.applyBiomeDecoration(
                world,
                chunk,
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
        for (GeneratorHolder holder : this.generators) {
            holder.generator.createStructures(
                registryManager,
                chunkGeneratorStructureState,
                structureManager,
                chunk,
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
