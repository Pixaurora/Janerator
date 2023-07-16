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

        Graph.getIndices(this.shading, Graph.SHADED)
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
                    sampledShade ? modifiedGenerator : defaultGenerator
                );
            }
        }

        return biomeGeneratorMap;
    }

    private List<Coordinate> findOutlinedPortion() {
        List<Coordinate> outlinedPortion = new ArrayList<>();

        for (Coordinate coordinate : Graph.getIndices(this.shading, Graph.SHADED)) {
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
                            ChunkPos pos = new ChunkPos(this.pos.x + deltaX, this.pos.z + deltaZ);

                            GraphedChunk otherGraphedArea = Graph.doChunkGraphing(pos);

                            neighborShading = otherGraphedArea.shading.get(neighbor.makeLegal().toListIndex());
                        }

                        return neighborShading != Graph.SHADED;
                    }
                );

            if (hasContrastingNeighbor) {
                outlinedPortion.add(coordinate);
            }
        }

        return outlinedPortion;
    }
}
