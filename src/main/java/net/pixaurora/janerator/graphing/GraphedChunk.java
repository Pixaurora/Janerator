package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;
import net.pixaurora.janerator.worldgen.generator.MultiGenerator;

public class GraphedChunk {
    public static boolean SHADED = true;
    public static boolean UNSHADED = false;

    private List<Boolean> shading;
    private ChunkPos pos;

    private ChunkGrapher grapher;

    private GraphedChunk(ChunkGrapher grapher, ChunkPos pos, List<Boolean> shading) {
        this.grapher = grapher;
        this.pos = pos;
        this.shading = shading;
    }

    public static GraphedChunk fromGraphing(ChunkGrapher grapher, ChunkPos pos) {
        List<CompletableFuture<Boolean>> graphingFutures = new ArrayList<>();

        int startX = pos.getMinBlockX();
        int startZ = pos.getMinBlockZ();

        int endX = startX + 16;
        int endZ = startZ + 16;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                graphingFutures.add(grapher.schedulePointGraphing(x, z));
            }
        }

        return new GraphedChunk(
            grapher,
            pos,
            graphingFutures
                .stream()
                .map(future -> GraphingUtils.completeFuture(future))
                .toList()
        );
    }

    public static GraphedChunk allUnshaded(ChunkGrapher grapher, ChunkPos pos) {
        return new GraphedChunk(
            grapher,
            pos,
            Collections.nCopies(256, GraphedChunk.UNSHADED)
        );
    }

    private List<ChunkGenerator> getBlockScaleMap(MultiGenerator multiGenerator) {
        List<ChunkGenerator> generatorMap = new ArrayList<>(
            IntStream.range(0, 256)
                .boxed()
                .map(value -> multiGenerator.getDefaultGenerator())
                .toList()
        );

        GraphingUtils.getIndices(this.shading, GraphedChunk.SHADED)
            .stream()
            .forEach(coord -> generatorMap.set(coord.toListIndex(), multiGenerator.getShadedGenerator()));
        this.findOutlinedPortion()
            .stream()
            .forEach(coord -> generatorMap.set(coord.toListIndex(), multiGenerator.getOutlinesGenerator()));

        return generatorMap;
    }

    private List<ChunkGenerator> getBiomeScaleMap(MultiGenerator multiGenerator) {
        List<ChunkGenerator> biomeGeneratorMap = new ArrayList<>();

        // Because biomes are placed per every 4 blocks, we sample
        // the most common generator in 4 block sections throughout the chunk
        // so that the biome placements line up with the blocks better
        for (int section_x = 0; section_x < 16; section_x += 4) {
            for (int section_z = 0; section_z < 16; section_z += 4) {
                Map<Boolean, Integer> generatorSample = new HashMap<>(2);

                for (int x = section_x; x < section_x + 4; x++) {
                    for (int z = section_z; z < section_z + 4; z++) {
                        boolean shade = this.shading.get(new Coordinate(x, z).toListIndex());
                        int currentScore = generatorSample.getOrDefault(shade, 0);
                        generatorSample.put(shade, currentScore + 1);
                    }
                }

                boolean sampledShade = generatorSample.entrySet()
                    .stream()
                    .max((entry1, entry2) -> entry1.getValue() - entry2.getValue())
                    .get().getKey();

                biomeGeneratorMap.add(
                    sampledShade ? multiGenerator.getShadedGenerator() : multiGenerator.getDefaultGenerator()
                );
            }
        }

        return biomeGeneratorMap;
    }

    public FullGeneratorLookup toLookup(MultiGenerator generator) {
        return new FullGeneratorLookup(this.getBlockScaleMap(generator), this.getBiomeScaleMap(generator));
    }

    private List<Coordinate> findOutlinedPortion() {
        List<Coordinate> outlinedPortion = new ArrayList<>();

        Map<ChunkPos, GraphedChunk> neighboringChunks = new HashMap<>(4);

        for (Coordinate coordinate : GraphingUtils.getIndices(this.shading, GraphedChunk.SHADED)) {
            boolean hasContrastingNeighbor = coordinate.getNeighbors()
                .stream()
                .anyMatch(
                    neighbor -> {
                        boolean neighborShading;

                        if (neighbor.isLegal()) {
                            neighborShading = this.shading.get(neighbor.toListIndex());
                        } else {
                            int deltaX = neighbor.x() < 0 ? -1 : neighbor.x() < 16 ? 0 : 1;
                            int deltaZ = neighbor.z() < 0 ? -1 : neighbor.z() < 16 ? 0 : 1;
                            ChunkPos neighborPos = new ChunkPos(this.pos.x + deltaX, this.pos.z + deltaZ);

                            GraphedChunk neighboringGraphedArea = neighboringChunks.get(neighborPos);

                            if (Objects.isNull(neighboringGraphedArea)) {
                                neighboringGraphedArea = this.grapher.getChunkGraph(neighborPos);
                                neighboringChunks.put(neighborPos, neighboringGraphedArea);
                            }

                            neighborShading = neighboringGraphedArea.shading.get(neighbor.makeLegal().toListIndex());
                        }

                        return neighborShading != GraphedChunk.SHADED;
                    }
                );

            if (hasContrastingNeighbor) {
                outlinedPortion.add(coordinate);
            }
        }

        return outlinedPortion;
    }
}
