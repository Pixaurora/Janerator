package dev.pixirora.janerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;

import dev.pixirora.janerator.mixin.ChunkGeneratorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public class MultiGenerator extends ChunkGenerator {
    ArrayList<ArrayList<? extends ChunkGenerator>> generatorMap;
    ChunkGenerator majorityGenerator;

    public MultiGenerator(
        ArrayList<ArrayList<? extends ChunkGenerator>> generatorMap,
        ChunkGenerator majorityGenerator
    ) {
        super(((ChunkGeneratorAccessor) majorityGenerator).getBiomeSource());

        this.majorityGenerator = majorityGenerator;
        this.generatorMap = generatorMap;
    }

    public Codec<? extends ChunkGenerator> codec() {
        return null;
    }

    public int getMinY() {
        return 0;
    }

    public int getGenDepth() {
        return 0;
    }

    public int getSeaLevel() {
        return 0;
    }

    public void spawnOriginalMobs(WorldGenRegion region) {
        majorityGenerator.spawnOriginalMobs(region);
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(
        Executor executor, 
        Blender blender, 
        RandomState randomState, 
        StructureManager structureManager,
        ChunkAccess chunk
    ) {
        return majorityGenerator.fillFromNoise(executor, blender, randomState, structureManager, chunk);
    };

    public void applyCarvers(
        WorldGenRegion chunkRegion,
        long seed,
        RandomState randomState,
        BiomeManager biomeAccess,
        StructureManager structureManager,
        ChunkAccess chunk,
        GenerationStep.Carving generationStep
    ) {
        majorityGenerator.applyCarvers(
            chunkRegion,
            seed,
            randomState,
            biomeAccess,
            structureManager,
            chunk,
            generationStep
        );
    }

    public void buildSurface(
        WorldGenRegion region, 
        StructureManager structureManager, 
        RandomState randomState,
        ChunkAccess chunk
    ) {
        majorityGenerator.buildSurface(region, structureManager, randomState, chunk);
    }

    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
        return majorityGenerator.getBaseColumn(x, z, world, randomState);
    }

    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
        majorityGenerator.addDebugScreenInfo(list, randomState, pos);
    }

    public int getBaseHeight(
        int x, 
        int z, 
        Heightmap.Types heightmap, 
        LevelHeightAccessor world,
        RandomState randomState
    ) {
        return majorityGenerator.getBaseHeight(x, z, heightmap, world, randomState);
    }

}
