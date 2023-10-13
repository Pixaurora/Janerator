package net.pixaurora.janerator.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.grapher.ChunkGrapher;

public class GraphedChunk {
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
            Collections.nCopies(256, false)
        );
    }

    public boolean isShaded(Coordinate pos) {
        return this.shading.get(pos.toListIndex());
    }

    public List<Coordinate> getShadedCoordinates() {
        List<Coordinate> shadedCoordinates = new ArrayList<>(256);

        for (int i = 0; i < 256; i++) {
            if (this.shading.get(i)) {
                shadedCoordinates.add(Coordinate.fromListIndex(i));
            }
        }

        return shadedCoordinates;
    }

    public List<Coordinate> findOutlinedPortion() {
        List<Coordinate> outlinedPortion = new ArrayList<>();

        Map<ChunkPos, GraphedChunk> neighboringChunks = new HashMap<>(8);

        for (Coordinate coordinate : GraphingUtils.getCoordinates(this.shading, true)) {
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

                        return neighborShading != true;
                    }
                );

            if (hasContrastingNeighbor) {
                outlinedPortion.add(coordinate);
            }
        }

        return outlinedPortion;
    }
}
