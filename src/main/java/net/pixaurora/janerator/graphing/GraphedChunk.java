package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;
import net.pixaurora.janerator.worldgen.FullGeneratorLookup;
import net.pixaurora.janerator.worldgen.generator.MultiGenerator;

public class GraphedChunk {
    public static boolean SHADED = true;
    public static boolean UNSHADED = false;

    private final List<Boolean> shading;
    private final ChunkPos chunk;

    private final ChunkGrapher grapher;

    private GraphedChunk(ChunkGrapher grapher, ChunkPos chunk, List<Boolean> shading) {
        this.chunk = chunk;
        this.shading = shading;

        this.grapher = grapher;
    }

    public static <E> List<E> doGraphing(Function<Coordinate, E> graphEvaluator, ChunkPos chunk) {
        List<E> graph = new ArrayList<>();

        int startX = chunk.getMinBlockX();
        int startZ = chunk.getMinBlockZ();

        int endX = startX + 16;
        int endZ = startZ + 16;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                graph.add(graphEvaluator.apply(new Coordinate(x, z)));
            }
        }

        return graph;
    }

    public GraphedChunk(ChunkGrapher grapher, ChunkPos chunk) {
        this(grapher, chunk, doGraphing(grapher::isPointShaded, chunk));
    }

    public static GraphedChunk allUnshaded(ChunkGrapher grapher, ChunkPos pos) {
        return new GraphedChunk(
            grapher,
            pos,
            Collections.nCopies(256, GraphedChunk.UNSHADED)
        );
    }

    public boolean isShaded(Coordinate pos) {
        return this.shading.get(pos.toListIndex());
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

                boolean sampledShade = generatorSample.entrySet().stream()
                    .max((entry1, entry2) -> entry1.getValue() - entry2.getValue())
                    .get().getKey();

                biomeGeneratorMap.add(
                    sampledShade ? multiGenerator.getShadedGenerator() : multiGenerator.getDefaultGenerator()
                );
            }
        }

        return biomeGeneratorMap;
    }

    private List<Coordinate> findOutlinedPortion() {
        List<Coordinate> outlinedPortion = new ArrayList<>();

        Map<ChunkPos, GraphedChunk> neighboringChunks = new HashMap<>(8);

        for (Coordinate coordinate : GraphingUtils.getIndices(this.shading, GraphedChunk.SHADED)) {
            boolean hasContrastingNeighbor = coordinate.getNeighbors()
                .stream()
                .anyMatch(
                    neighbor -> {
                        boolean neighborShading;

                        if (neighbor.isLegal()) {
                            neighborShading = this.isShaded(neighbor);
                        } else {
                            int deltaX = neighbor.x() < 0 ? -1 : neighbor.x() < 16 ? 0 : 1;
                            int deltaZ = neighbor.z() < 0 ? -1 : neighbor.z() < 16 ? 0 : 1;
                            ChunkPos neighborChunk = new ChunkPos(this.chunk.x + deltaX, this.chunk.z + deltaZ);

                            GraphedChunk neighborChunkGraph = neighboringChunks.computeIfAbsent(neighborChunk, pos -> this.grapher.getChunkGraph(pos));
                            neighborShading = neighborChunkGraph.isShaded(neighbor.makeLegal());
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

    public FullGeneratorLookup toLookup(MultiGenerator generator) {
        return new FullGeneratorLookup(this.getBlockScaleMap(generator), this.getBiomeScaleMap(generator));
    }
}
