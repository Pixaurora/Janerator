package net.pixaurora.janerator.worldgen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.pixaurora.janerator.config.GraphProperties;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphedChunk;

public class MultiGenerator extends ChunkGenerator {
    private boolean generatorsInitialized;
    private ChunkGenerator defaultGenerator;
    private ChunkGenerator modifiedGenerator;
    private ChunkGenerator outlineGenerator;

    private GraphProperties dimensionPreset;
    private ChunkPos pos;

    private GeneratorFinder generators;
    private GeneratorFinder biomeGenerators;

    public MultiGenerator(
        ChunkGenerator defaultGenerator,
        ChunkGenerator modifiedGenerator,
        ChunkGenerator outlineGenerator,
        ChunkAccess chunk,
        GraphProperties dimensionPreset
    ) {
        super(defaultGenerator.getBiomeSource());

        this.generatorsInitialized = false;
        this.defaultGenerator = defaultGenerator;
        this.modifiedGenerator = modifiedGenerator;
        this.outlineGenerator = outlineGenerator;

        this.pos = chunk.getPos();
        this.dimensionPreset = dimensionPreset;

        List.of(this, defaultGenerator, modifiedGenerator, outlineGenerator)
            .stream()
            .forEach(generator -> generator.janerator$setDimension(dimensionPreset.getDimension()));
    }

	@Override
	public Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

    private void initializeGenerators() {
        GraphedChunk graphedArea = dimensionPreset.getGrapher().getChunkGraph(pos);

        this.generators = new GeneratorFinder(graphedArea.getGeneratorMap(this.defaultGenerator, this.modifiedGenerator, this.outlineGenerator));
        this.biomeGenerators = new GeneratorFinder(graphedArea.sampleBiomeGeneratorMap(this.defaultGenerator, this.modifiedGenerator));

        this.generatorsInitialized = true;
    }

    private GeneratorFinder getGenerators() {
        if (!generatorsInitialized) {
            this.initializeGenerators();
        }

        return this.generators;
    }

    private GeneratorFinder getBiomeGenerators() {
        if (!generatorsInitialized) {
            this.initializeGenerators();
        }

        return this.biomeGenerators;
    }

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        for (PlacementSelection selection : this.getGenerators().getAllSelections()) {
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
		return this.getGenerators().getDefault().getSpawnHeight(world);
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(
		Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk
	) {
        CompletableFuture<ChunkAccess> placeholderFuture = new CompletableFuture<>();
        CompletableFuture<ChunkAccess> future = placeholderFuture;

        for(PlacementSelection selection : this.getGenerators().getAllSelections()) {
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
		return this.getGenerators().getAt(new Coordinate(x, z)).getBaseHeight(x, z, heightmap, world, randomState);
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
		return this.getGenerators().getAt(new Coordinate(x, z)).getBaseColumn(x, z, world, randomState);
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
                        this.getBiomeGenerators(),
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
        for (PlacementSelection selection : this.getGenerators().getAllSelections()) {
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
        this.getGenerators().getDefault().spawnOriginalMobs(region);
	}

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureManager) {
        for (PlacementSelection selection : this.getGenerators().getAllSelections()) {
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
        for (PlacementSelection selection : this.getGenerators().getAllSelections()) {
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
