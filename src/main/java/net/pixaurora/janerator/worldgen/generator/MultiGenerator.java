package net.pixaurora.janerator.worldgen.generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;
import net.pixaurora.janerator.worldgen.PlacementSelection;
import net.pixaurora.janerator.worldgen.WrappedBiomeResolver;

public class MultiGenerator extends ChunkGenerator {
    private ChunkGrapher grapher;

    private ChunkGenerator defaultGenerator;
    private ChunkGenerator shadedGenerator;
    private ChunkGenerator outlinesGenerator;

    private LoadingCache<ChunkPos, FullGeneratorLookup> selectionCache;

    public MultiGenerator(
        ChunkGrapher grapher,
        ChunkGenerator defaultGenerator,
        ChunkGenerator shadedGenerator,
        ChunkGenerator outlinesGenerator
    ) {
        super(defaultGenerator.getBiomeSource());

        this.grapher = grapher;

        this.defaultGenerator = defaultGenerator;
        this.shadedGenerator = shadedGenerator;
        this.outlinesGenerator = outlinesGenerator;

        this.selectionCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(CacheLoader.from(pos -> this.grapher.getChunkGraph(pos).toLookup(this)));

        for (ChunkGenerator generator : List.of(this, defaultGenerator, shadedGenerator, outlinesGenerator)) {
            generator.janerator$setupMultiGenerating(grapher, this);
        }
    }

    public ChunkGrapher getGrapher() {
        return this.grapher;
    }

    public ChunkGenerator getDefaultGenerator() {
        return this.defaultGenerator;
    }

    public ChunkGenerator getShadedGenerator() {
        return this.shadedGenerator;
    }

    public ChunkGenerator getOutlinesGenerator() {
        return this.outlinesGenerator;
    }

    public FullGeneratorLookup getGenerators(ChunkPos pos) {
        try {
            return this.selectionCache.get(pos);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public FullGeneratorLookup getGenerators(ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();

        boolean chunkAlreadyGenerated = chunk instanceof LevelChunk || chunk instanceof ImposterProtoChunk;
        if (chunkAlreadyGenerated) {
            this.selectionCache.put(pos, GraphedChunk.allUnshaded(grapher, pos).toLookup(this));
        }

        return this.getGenerators(pos);
    }

    @Override
    public MultiGenerator janerator$getParent() {
        throw new RuntimeException("MultiGenerator cannot have a MultiGenerator parent.");
    }

	@Override
	public Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        for (PlacementSelection selection : this.getGenerators(chunk).getAllSelections()) {
            selection.getUsedGenerator().buildSurface(
                region,
                structureManager,
                randomState,
                chunk
            );
        }

        chunk.janerator$stopSelecting();
	}

	@Override
	public int getSpawnHeight(LevelHeightAccessor world) {
		return this.defaultGenerator.getSpawnHeight(world);
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(
		Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk
	) {
        CompletableFuture<ChunkAccess> placeholderFuture = new CompletableFuture<>();
        CompletableFuture<ChunkAccess> future = placeholderFuture;

        for(PlacementSelection selection : this.getGenerators(chunk).getAllSelections()) {
            future = future.thenCompose(
                access -> selection.getUsedGenerator().fillFromNoise(
                    executor,
                    blender,
                    randomState,
                    structureManager,
                    chunk.janerator$withSelection(selection, true)
                )
            );
        }

        placeholderFuture.complete(chunk);

        return future.thenApply(access -> {
            chunk.janerator$stopSelecting();
            return chunk;
        });
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState) {
		return this.shadedGenerator.getBaseHeight(x, z, heightmap, world, randomState);
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
		return this.shadedGenerator.getBaseColumn(x, z, world, randomState);
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
                        this.getGenerators(chunk).atBiomeScale(),
                        blender,
                        chunk,
                        structureManager,
                        randomState
                    ),
                    randomState.sampler()
                );

                return chunk;
            },
            executor
        );
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
        for (PlacementSelection selection : this.getGenerators(chunk).getAllSelections()) {
            selection.getUsedGenerator().applyCarvers(
                chunkRegion,
                seed,
                randomState,
                biomeAccess,
                structureManager,
                chunk.janerator$withSelection(selection, true),
                generationStep
            );
        }

        chunk.janerator$stopSelecting();
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {
        this.getGenerators(region.getCenter()).getDefault().spawnOriginalMobs(region);
	}

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager) {
        for (PlacementSelection selection : this.getGenerators(chunk).getAllSelections()) {
            selection.getUsedGenerator().applyBiomeDecoration(
                world,
                chunk,
                structureManager
            );
        }

        chunk.janerator$stopSelecting();
    }

    @Override
    public void createStructures(
		RegistryAccess registryManager,
		ChunkGeneratorStructureState chunkGeneratorStructureState,
		StructureManager structureManager,
		ChunkAccess chunk,
		StructureTemplateManager templateManager
	) {
        for (PlacementSelection selection : this.getGenerators(chunk).getAllSelections()) {
            selection.getUsedGenerator().createStructures(
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
		return defaultGenerator.getMinY();
	}

	@Override
	public int getGenDepth() {
		return defaultGenerator.getGenDepth();
	}

	@Override
	public int getSeaLevel() {
		return defaultGenerator.getSeaLevel();
	}
}
