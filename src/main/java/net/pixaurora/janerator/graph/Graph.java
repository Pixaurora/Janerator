package net.pixaurora.janerator.graph;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.level.ChunkPos;
import net.pixaurora.janerator.graphing.Graphing;

public class Graph {
    public static boolean SHADED = true;
    public static boolean UNSHADED = false;

    private static Map<ChunkPos, GraphedChunk> chunks = new ConcurrentHashMap<>();

    public static CompletableFuture<GraphedChunk> scheduleChunkGraphing(ChunkPos pos) {
        GraphedChunk existingChunk = Graph.chunks.get(pos);

        if (Objects.nonNull(existingChunk)) {
            return CompletableFuture.completedFuture(existingChunk);
        } else {
            return CompletableFuture.supplyAsync(
                () -> {
                    GraphedChunk chunk = new GraphedChunk(pos);
                    Graph.chunks.put(pos, chunk);

                    return chunk;
                },
                Graphing.graphingThreadPool
            );
        }
    }
}
