package net.pixaurora.janerator.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.pixaurora.janerator.graphing.Graphing;

public class GraphedChunk {
    List<Boolean> shading;
    ChunkPos pos;

    public GraphedChunk(ChunkPos pos) {
        this.pos = pos;

        List<CompletableFuture<Boolean>> graphingFutures = new ArrayList<>();

        int startX = pos.getMinBlockX();
        int startZ = pos.getMinBlockZ();

        int endX = startX + 16;
        int endZ = startZ + 16;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                graphingFutures.add(Graphing.scheduleGraphing(x, z));
            }
        }

        this.shading = graphingFutures
            .stream()
            .map(future -> Graphing.completeGraphing(future))
            .toList();
    }

    public List<ChunkGenerator> getGeneratorMap(ChunkGenerator defaultGenerator, ChunkGenerator modifiedGenerator, ChunkGenerator outlineGenerator) {
        List<ChunkGenerator> generatorMap = new ArrayList<>(
            IntStream.range(0, 256)
                .boxed()
                .map(value -> defaultGenerator)
                .toList()
        );

        this.getIndices(Graph.SHADED)
            .stream()
            .forEach(coord -> generatorMap.set(coord.toListIndex(), modifiedGenerator));
        this.findOutlinedPortion()
            .stream()
            .forEach(coord -> generatorMap.set(coord.toListIndex(), outlineGenerator));

        return generatorMap;
    }

    public List<ChunkGenerator> sampleBiomeGeneratorMap(ChunkGenerator defaultGenerator, ChunkGenerator modifiedGenerator) {
        List<ChunkGenerator> biomeGeneratorMap = new ArrayList<>();

        // Because biomes are placed per every 4 blocks, we sample
        // the most common generator in 4 block sections throughout the chunk
        // so that the biome placements line up with the blocks better
        for (int section_x = 0; section_x < 16; section_x += 4) {
            for (int section_z = 0; section_z < 16; section_z += 4) {
                Map<Boolean, Integer> generatorSample = new HashMap<>(2);

                for (int x = section_x; x < section_x + 4; x++) {
                    for (int z = section_z; z < section_z + 4; z++) {
                        boolean generator = this.shading.get(new Coordinate(x, z).toListIndex());

                        int currentScore = generatorSample.getOrDefault(generator, 0);
                        generatorSample.put(generator, currentScore + 1);
                    }
                }

                boolean sampledShade = generatorSample.entrySet()
                    .stream()
                    .max((entry1, entry2) -> entry1.getValue() - entry2.getValue())
                    .get().getKey();

                biomeGeneratorMap.add(
                    sampledShade ? modifiedGenerator : defaultGenerator
                );
            }
        }

        return biomeGeneratorMap;
    }

    private List<Coordinate> findOutlinedPortion() {
        List<Coordinate> outlinedPortion = new ArrayList<>();

        for (Coordinate coordinate : this.getIndices(Graph.SHADED)) {
            boolean hasContrastingNeighbor = coordinate.getNeighbors()
                .stream()
                .anyMatch(
                    neighbor -> {
                        if (neighbor.isLegal()) {
                            return this.shading.get(neighbor.toListIndex()) != Graph.SHADED;
                        } else {
                            // TODO: Handle coordinates outside the chunk
                            return false;
                        }
                    }
                );

            if (hasContrastingNeighbor) {
                outlinedPortion.add(coordinate);
            }
        }

        return outlinedPortion;
    }

    public List<Coordinate> getIndices(boolean shade) {
        return IntStream.range(0, 256)
            .filter(index -> this.shading.get(index) == shade)
            .boxed()
            .map(Coordinate::fromListIndex)
            .toList();
    }
}
