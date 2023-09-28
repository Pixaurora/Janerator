package net.pixaurora.janerator.worldgen.generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;
import net.pixaurora.janerator.worldgen.GeneratorLookup;
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
	public CompletableFuture<ChunkAccess> fillFromNoise(
		Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk
	) {
        GeneratorLookup generatorsForChunk = this.getGenerators(chunk).atBiomeScale();

        if (generatorsForChunk.size() == 1) {
            return generatorsForChunk.getDefault().fillFromNoise(executor, blender, randomState, structureManager, chunk);
        }

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
	public CompletableFuture<ChunkAccess> createBiomes(
		Executor executor, RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk
	) {
        GeneratorLookup generatorsForChunk = this.getGenerators(chunk).atBiomeScale();

        if (generatorsForChunk.size() == 1) {
            return generatorsForChunk.getDefault().createBiomes(executor, randomState, blender, structureManager, chunk);
        }

        return CompletableFuture.supplyAsync(
            () -> {
                chunk.fillBiomesFromNoise(
                    new WrappedBiomeResolver(
                        generatorsForChunk,
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

    protected void generate(ChunkAccess chunk, Consumer<ChunkGenerator> generationOp, Predicate<ChunkGenerator> filteringCondition, boolean selectInSections) {
        GeneratorLookup generatorsForChunk = this.getGenerators(chunk);

        if (generatorsForChunk.size() == 1) {
            generationOp.accept(generatorsForChunk.getDefault());
        }

        for (PlacementSelection selection : generatorsForChunk.getAllSelections()) {
            ChunkGenerator usedGenerator = selection.getUsedGenerator();

            if (filteringCondition.test(usedGenerator)) {
                chunk.janerator$withSelection(selection, selectInSections);
            }

            generationOp.accept(usedGenerator);

            chunk.janerator$stopSelecting();
        }
    }

    protected void generate(ChunkAccess chunk, Consumer<ChunkGenerator> generationOp, boolean selectInSections) {
        this.generate(chunk, generationOp, filteringOn -> true, selectInSections);
    }

    @Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        this.generate(
            chunk,
            generator -> generator.buildSurface(region, structureManager, randomState, chunk),
            false
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
        this.generate(
            chunk,
            generator -> generator.applyCarvers(chunkRegion, seed, randomState, biomeAccess, structureManager, chunk, generationStep),
            true
        );
	}

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager) {
        this.generate(
            chunk,
            generator -> generator.applyBiomeDecoration(world, chunk, structureManager),
            generator -> generator instanceof FlatLevelSource,
            false
        );
    }

    @Override
    public void createStructures(
		RegistryAccess registryManager,
		ChunkGeneratorStructureState chunkGeneratorStructureState,
		StructureManager structureManager,
		ChunkAccess chunk,
		StructureTemplateManager templateManager
	) {
        this.generate(
            chunk,
            generator -> generator.createStructures(registryManager, chunkGeneratorStructureState, structureManager, chunk, templateManager),
            filteringOn -> false,
            false
        );
    }

    @Override
	public void spawnOriginalMobs(WorldGenRegion region) {
        this.getGenerators(region.getCenter()).getDefault().spawnOriginalMobs(region);
	}

    @Override
	public int getSpawnHeight(LevelHeightAccessor world) {
		return this.defaultGenerator.getSpawnHeight(world);
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

    @Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
}
