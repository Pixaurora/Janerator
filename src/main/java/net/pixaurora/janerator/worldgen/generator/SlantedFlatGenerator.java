package net.pixaurora.janerator.worldgen.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphedChunk;
import net.pixaurora.janerator.graphing.GraphingUtils;
import net.pixaurora.janerator.worldgen.settings.SlantedFlatGeneratorSettings;

public class SlantedFlatGenerator extends ChunkGenerator {
    public static final Codec<SlantedFlatGenerator> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(SlantedFlatGenerator::getBiomeSource),
            SlantedFlatGeneratorSettings.CODEC.fieldOf("settings").forGetter(SlantedFlatGenerator::getSettings)
        ).apply(instance, SlantedFlatGenerator::new)
    );

    private final SlantedFlatGeneratorSettings settings;
    private ThreadLocal<GraphFunction> yOffsetFunction;

    public SlantedFlatGenerator(BiomeSource biomeSource, SlantedFlatGeneratorSettings settings) {
        super(biomeSource);

        this.settings = settings;
        this.yOffsetFunction = ThreadLocal.withInitial(() -> GraphFunction.fromDefinition(this.settings.yOffsetDefinition()));
    }

    public SlantedFlatGeneratorSettings getSettings() {
        return this.settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return SlantedFlatGenerator.CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
        Executor executor,
        Blender blender,
        RandomState randomState,
        StructureManager structureManager,
        ChunkAccess chunk
    ) {
        return CompletableFuture.supplyAsync(() -> doFill(chunk), executor);
    }

    private int getYOffset(Coordinate coord) {
        return (int) Math.floor(this.yOffsetFunction.get().evaluate(coord.x(), coord.z()));
    }

    private List<BlockState> getColumn(int yOffset) {
        int height = this.settings.height();
        List<BlockState> blocks = this.settings.blocks();
        int blockCount = blocks.size();

        List<BlockState> column = new ArrayList<>(height);

        double multiplier = ((double) blockCount) / height;

        for (int layer = 0; layer < height; layer++) {
            double index = multiplier * GraphingUtils.mod(layer + yOffset, height);
            column.add(blocks.get((int) index));
        }

        return column;
    }

    private ChunkAccess doFill(ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        List<Integer> yOffsets = GraphedChunk.doGraphing(this::getYOffset, pos);

        int placeHeight = Math.min(chunk.getHeight(), this.settings.height());
        int lowestY = chunk.getMinBuildHeight();

        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap surface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int yOffset = yOffsets.get(new Coordinate(x, z).toListIndex());
                List<BlockState> layers = this.getColumn(yOffset);

                for (int layer = 0; layer < placeHeight; layer++) {
                    BlockState block = layers.get(layer);
                    int y = lowestY + layer;

                    chunk.setBlockState(new BlockPos(x, y, z), block, false);
                    oceanFloor.update(x, y, z, block);
                    surface.update(x, y, z, block);
                }
            }
        }

        return chunk;
    }

    @Override
    public int getBaseHeight(int x, int z, Types heightmap, LevelHeightAccessor world, RandomState randomState) {
        return this.settings.height();
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
        int yOffset = this.getYOffset(new Coordinate(x, z));

        return new NoiseColumn(
            world.getMinBuildHeight(),
            this.getColumn(yOffset).stream()
                .limit(world.getHeight())
                .toArray(size -> new BlockState[size])
        );
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

    @Override
    public void applyCarvers(
        WorldGenRegion chunkRegion,
        long seed,
        RandomState randomState,
        BiomeManager biomeAccess,
        StructureManager structureManager,
        ChunkAccess chunk,
        Carving generationStep
    ) {}

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {}

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
}
